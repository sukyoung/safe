/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe

import java.util.HashMap

import kr.ac.kaist.safe.interpreter.{InterpreterDebug => ID, InterpreterHelper => IH, InterpreterPredefine => IP}
import kr.ac.kaist.safe.interpreter.objects._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{IRFactory => IF}
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.{EJSCompletionType => CT}

package object interpreter {
  ////////////////////////////////////////////////////////////////////////////////
  // Debug
  ////////////////////////////////////////////////////////////////////////////////

  val debug = 0
  val cov_debug = false

  ////////////////////////////////////////////////////////////////////////////////
  // Type
  ////////////////////////////////////////////////////////////////////////////////
  
  // Type aliases
  type Var = String
  type PName = String
  
  ////////////////////////////////////////////////////////////////////////////////
  // Value & Error
  ////////////////////////////////////////////////////////////////////////////////
  
  // ValError = Val or JSError
  abstract class ValError()
  
  // Val = JSObject or PVal
  abstract class Val() extends ValError {
    override def equals(v: Any): Boolean = (this, v) match {
      case (PVal(IRVal(EJSUndef)), PVal(IRVal(EJSUndef))) => true
      case (PVal(IRVal(EJSNull)), PVal(IRVal(EJSNull))) => true
      case (PVal(IRVal(b1:EJSBool)), PVal(IRVal(b2:EJSBool))) => b1.bool == b2.bool
      case (PVal(IRVal(n1:EJSNumber)), PVal(IRVal(n2:EJSNumber))) => {
        if(n1.num == 0 && n2.num == 0)
          //IH.isPlusZero(n1) == IH.isPlusZero(n2)
          java.lang.Double.doubleToLongBits(n1.num) == java.lang.Double.doubleToLongBits(n2.num)
        else
          n1.num == n2.num
      }
      case (PVal(IRVal(s1:EJSString)), PVal(IRVal(s2:EJSString))) => s1.str.equals(s2.str)
      case (o1:JSObject, o2:JSObject) => o1.eq(o2)
      case _ => false
    }
  }

  // PVal
  case class PVal(v: IRVal) extends Val {
    override def toString(): String = ID.prExpr(v)
  }

  object PVal {
    def apply(value: EJSVal) = PVal(IRVal(value))
    def apply(irVal: IRVal) = PVal(irVal)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Error
  ////////////////////////////////////////////////////////////////////////////////
  
  class InterpreterError(msg: String, span: Span) extends Exception
  class DefaultValueError(var err: JSError) extends Exception
  
  /*
   * 4.2 Language Overview
   * the Error objects Error, EvalError, RangeError, ReferenceError,
   * SyntaxError, TypeError and URIError
   */
  abstract class JSError() extends ValError
  case class Error() extends JSError()
  case class EvalError() extends JSError()
  case class RangeError() extends JSError()
  case class ReferenceError(var x: Var) extends JSError()
  case class SyntaxError() extends JSError()
  case class TypeError() extends JSError()
  case class URIError() extends JSError()
  case class NYIError() extends JSError()

  def exnLoc(I: Interpreter, ve: ValError): Val = ve match {
    case v: Val => v
    case _: Error => I.IS.ErrorConstructor.construct(IP.undefV)
    case _: EvalError => I.IS.EvalErrorConstructor.construct(IP.undefV)
    case _: RangeError => I.IS.RangeErrorConstructor.construct(IP.undefV)
    case _: ReferenceError => I.IS.ReferenceErrorConstructor.construct(IP.undefV)
    case _: SyntaxError => I.IS.SyntaxErrorConstructor.construct(IP.undefV)
    case _: TypeError => I.IS.TypeErrorConstructor.construct(IP.undefV)
    case _: URIError => I.IS.URIErrorConstructor.construct(IP.undefV)
  }
  
  ////////////////////////////////////////////////////////////////////////////////
  // Environment Record
  ////////////////////////////////////////////////////////////////////////////////

  abstract class BindingValue()

  /*
   * 10.2.1 Environment Records
   * An environment record is either a declarative environment record or
   * an object environment record.
   */
  abstract class EnvRec()
  
  /*
   * 10.2.1.1 Declarative Environment Records: A declarative environment record
   * binds the set of identifiers defined by the declarations contained within
   * its scope.
   */
  case class DeclEnvRec(s: Store) extends EnvRec
  type Store = HashMap[Var, StoreValue]
  class StoreValue(var value: ValError, var init: Boolean,
                   var mutable: Boolean, var configurable: Boolean) extends BindingValue {
    def setValue(v: ValError) {value = v; init = true;}
  }
  
  /*
   * 10.2.1.2 Object Environment Records: Each object environment record is
   * associated with an object called its binding object.
   */
  case class ObjEnvRec(o: JSObject) extends EnvRec

  /*
   * 10.2 Lexical Environments
   * A lexical environment consists of an environment record and a possibly null
   * reference to an outer lexical environment.
   */
  abstract class Env()
  
  /*
   * 10.2.3 The Global Environment
   * The global environment is a unique Lexical Environment which is created before
   * any ECMAScript code is executed. The global environment's Environment Record is
   * an object environment record whose binding object is the global object.
   * The global environment's outer environment reference is null.
   */
  case class EmptyEnv() extends Env
  case class ConsEnv(var first: EnvRec, var rest: Env) extends Env

  ////////////////////////////////////////////////////////////////////////////////
  // Property
  ////////////////////////////////////////////////////////////////////////////////

  /*
   * 8.6.1 Property Attributes
   *   Table 5: Attributes of a Named Data Property
   *   [[Value]], [[Writable]], [[Enumerable]], [[Configurable]]
   *   Table 6: Attributes of a Named Accessor Property
   *   [[Get]], [[Set]], [[Enumerable]], [[Configurable]]
   */
  class ObjectProp(var value: Option[Val],
                   var get: Option[Val],
                   var set: Option[Val],
                   var writable: Option[Boolean],
                   var enumerable: Option[Boolean],
                   var configurable: Option[Boolean]) extends BindingValue {
    def areAllAttributesAbsent(): Boolean = value.isEmpty && get.isEmpty && set.isEmpty && writable.isEmpty && enumerable.isEmpty && configurable.isEmpty
    def isWritable(): Boolean = if(writable.isDefined) writable.get else false
    def isEnumerable(): Boolean = if(enumerable.isDefined) enumerable.get else false
    def isConfigurable(): Boolean = if(configurable.isDefined) configurable.get else false
    def copy(): ObjectProp = new ObjectProp(value, get, set, writable, enumerable, configurable)
    
    def getValueOrDefault(): Val = if(value.isDefined) value.get else IP.undefV
    def getGetOrDefault(): Val = if(get.isDefined) get.get else IP.undefV
    def getSetOrDefault(): Val = if(set.isDefined) set.get else IP.undefV
    def getWritableOrDefault(): Boolean = if(writable.isDefined) writable.get else false
    def getEnumerableOrDefault(): Boolean = if(enumerable.isDefined) enumerable.get else false
    def getConfigurableOrDefault(): Boolean = if(configurable.isDefined) configurable.get else false

    override def equals(a: Any): Boolean = {
      if(!a.isInstanceOf[ObjectProp]) false
      else {
        val op = a.asInstanceOf[ObjectProp]
        
        if(value.isDefined != op.value.isDefined || (value.isDefined && !value.get.equals(op.value.get))) return false
        if(get.isDefined != op.get.isDefined || (get.isDefined && !get.get.equals(op.get.get))) return false
        if(set.isDefined != op.set.isDefined || (set.isDefined && !set.get.equals(op.set.get))) return false
        if(writable.isDefined != op.writable.isDefined || (writable.isDefined && writable.get != op.writable.get)) return false
        if(enumerable.isDefined != op.enumerable.isDefined || (enumerable.isDefined && enumerable.get != op.enumerable.get)) return false
        if(configurable.isDefined != op.configurable.isDefined || (configurable.isDefined && configurable.get != op.configurable.get)) return false
        
        true
      }
    }
  }
  class PropTable {
    val map = new HashMap[PName, ObjectProp]
    val order = new HashMap[Int, PName]
    val index = new HashMap[PName, Int]
    var count = 0
    def clear(): Unit = {
      map.clear
      order.clear
      index.clear
      count = 0
    }
    def containsKey(key: PName): Boolean = map.containsKey(key)
    def containsValue(value: ObjectProp): Boolean = map.containsValue(value)
    def get(key: PName): ObjectProp = map.get(key)
    def isEmpty(): Boolean = map.isEmpty
    def keys(): List[PName] = {
      var l = List[PName]()
      val i = order.keySet.iterator
      while (i.hasNext) l ::= order.get(i.next)
      l.reverse
    }
    def unorderedEntrySet() = map.entrySet
    def put(key: PName, value: ObjectProp): ObjectProp = {
      if (!map.containsKey(key)) {
        order.put(count, key)
        index.put(key, count)
        count += 1
      }
      map.put(key, value)
    }
    def remove(key: PName): ObjectProp = {
      order.remove(index.get(key))
      index.remove(key)
      map.remove(key)
    }
    def size(): Int = map.size
    def values(): List[ObjectProp] = {
      var l = List[ObjectProp]()
      val i = order.keySet.iterator
      while (i.hasNext) l ::= map.get(order.get(i.next))
      l.reverse
    }
  }
  def propTable(): PropTable = new PropTable

  ////////////////////////////////////////////////////////////////////////////////
  // Completion
  ////////////////////////////////////////////////////////////////////////////////
  
  /*
  /*
   * 8.9 The Completion Specification Type
   * normal, break, continue, return and throw
   */
  abstract class Completion()
  // Normal(empty) : Normal(None)
  // Normal(v) : Normal(Some(v))
  case class Normal(v: Option[Val]) extends Completion
  /*
   * The term "Abrupt completion" referes to any completion with a type
   * other than normal.
   * Our IR rewrites Continue to Break.
   */
  abstract class Abrupt() extends Completion
  case class Break(v: Option[Val], l: IRId) extends Abrupt
  case class Return(v: Val) extends Abrupt
  case class Throw(e: ValError, sp: Span) extends Abrupt
  */
  class Completion() {
    def setNormal(): Unit = {Type = CT.NORMAL}
    def setNormal(v: Val): Unit = {Type = CT.NORMAL; value = v}
    def setBreak(l: IRId): Unit = {Type = CT.BREAK; label = l}
    def setBreak(v: Val, l: IRId): Unit = {Type = CT.BREAK; value = v; label = l}
    def setReturn(): Unit = {Type = CT.RETURN}
    def setReturn(v: Val): Unit = {Type = CT.RETURN; value = v}
    def setThrow(e: ValError, sp: Span): Unit = {Type = CT.THROW; error = e; span = sp}
    // Set last completion from another completion
    def setLastCompletion(c: Completion): Unit = {
      Type = c.Type
      Type match {
        case CT.NORMAL => if(c.value != null) value = c.value
        case CT.BREAK => if(c.value != null) value = c.value; label = c.label
        case CT.RETURN => value = c.value
        case CT.THROW => error = c.error; span = c.span
      }
    }

    def copy(): Completion = {
      val c = new Completion()
      c.Type = Type
      c.value = value
      c.label = label
      c.error = error
      c.span = span
      c
    }

    var Type: Int = CT.NORMAL
    var value: Val = null
    var label: IRId = null
    var error: ValError = null
    var span: Span = null
  }

  class ErrorException extends Exception
  class EvalErrorException extends Exception
  class RangeErrorException extends Exception
  class ReferenceErrorException extends Exception
  class SyntaxErrorException extends Exception
  class TypeErrorException extends Exception
  class URIErrorException extends Exception
  class NYIErrorException extends Exception
  class ThrowException(var error: ValError) extends Exception
}
