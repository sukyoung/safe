/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.interpreter

import java.text.DecimalFormat
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.interpreter.objects._
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

class InterpreterHelper(I: Interpreter) {
  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  def isNaN(n: EJSNumber): Boolean = n.num.isNaN
  def isPlusZero(n: EJSNumber): Boolean = java.lang.Double.doubleToLongBits(n.num) == 0x0000000000000000L
  def isMinusZero(n: EJSNumber): Boolean = java.lang.Double.doubleToLongBits(n.num) == 0x8000000000000000L
  def isZero(n: EJSNumber): Boolean = n.num == 0
  def isPlusInfinity(n: EJSNumber): Boolean = n.num == Double.PositiveInfinity
  def isMinusInfinity(n: EJSNumber): Boolean = n.num == Double.NegativeInfinity
  def isInfinite(n: EJSNumber): Boolean = n.num.isInfinite
  def isUndef(id: IRId): Boolean = id.uniqueName.equals(NU.freshGlobalName("undefVar")) || id.uniqueName.equals("undefined")
  def isUndef(v: Val): Boolean = v match {
    case PVal(IRVal(EJSUndef)) => true
    case _ => false
  }
  def isUndef(op: ObjectProp): Boolean = {
    if (op.value.isEmpty) false
    else isUndef(op.value.get)
  }
  def isNull(v: Val): Boolean = v match {
    case PVal(IRVal(EJSNull)) => true
    case _ => false
  }
  def isWhiteSpace(c: Char): Boolean = {
    // 7.2 White Space
    // Table 2 - Whitespace Characters
    c == 0x0009 || // Tab
      c == 0x000B || // Vertical Tab
      c == 0x000C || // Form Feed
      c == 0x0020 || // Space
      c == 0x00A0 || // No-break space
      c == 0xFEFF // Byte Order Mark
    // TODO: c == "Other category -Zs" (Any other Unicode "space separator")
  }
  def isLineTerminator(c: Char): Boolean = {
    // 7.3 Line Terminators
    // Table 3 - Line Terminator Characters
    c == 0x000A || // Line Feed
      c == 0x000D || // Carriage Return
      c == 0x2028 || // Line separator
      c == 0x2029 // Paragraph separator
  }
  def leftTrim(s: String): String = {
    val chars = s.toCharArray()
    for (i <- 0 until chars.length) {
      if (!isWhiteSpace(chars(i)) && !isLineTerminator(chars(i)))
        return s.substring(i)
    }
    ""
  }

  def internVar(s: String): Var = IP.varPrefix + s

  def negate(s: String): String =
    if (s.startsWith("-")) s.drop(1)
    else if (s.startsWith("+")) "-" + s.drop(1)
    else "-" + s
  def negate(n: EJSNumber): EJSNumber = mkIRNum(-n.num)

  def dummyInfo(): ASTNodeInfo = NU.makeASTNodeInfo(NU.dummySpan("forDummyInfo"))
  def getIRBool(b: Boolean): IRVal = if (b) IP.trueV else IP.falseV
  def dummyFtn(length: Int): IRFunctional = {
    val body: List[IRStmt] = (1 to length).map((_) => IF.dummyIRStmt(IF.dummyAST)).toList
    val params: List[IRId] = List(IP.thisTId, IP.argumentsTId).asInstanceOf[List[IRId]]
    val name: IRId = IP.defId
    IF.makeFunctional(false, NF.dummyFunctional, name, params, body)
  }
  def mkIRStr(s: String): EJSString = IF.makeString(s)
  def mkIRStrIR(s: String): IRVal = IF.makeStringIR(s)
  def mkIRBool(b: Boolean): EJSBool = IF.makeBool(b)
  def mkIRBoolIR(b: Boolean): IRVal = IF.makeBoolIR(b)
  def mkIRNum(d: Double): EJSNumber = {
    val name = d.toString
    IF.makeNumber(name, d)
  }
  def mkIRNumIR(d: Double): IRVal = IRVal(mkIRNum(d))
  def mkEmptyObjectProp(): ObjectProp = new ObjectProp(None, None, None, None, None, None)
  def mkDataProp(
    value: Val = IP.undefV,
    writable: Boolean = false,
    enumerable: Boolean = false,
    configurable: Boolean = false
  ): ObjectProp = new ObjectProp(Some(value), None, None, Some(writable), Some(enumerable), Some(configurable))
  def mkDataProp(
    value: Option[Val],
    writable: Option[Boolean],
    enumerable: Boolean,
    configurable: Boolean
  ): ObjectProp = new ObjectProp(value, None, None, writable, Some(enumerable), Some(configurable))
  def mkAccessorProp(
    get: Val = IP.undefV,
    set: Val = IP.undefV,
    enumerable: Boolean = false,
    configurable: Boolean = false
  ): ObjectProp = new ObjectProp(None, Some(get), Some(set), None, Some(enumerable), Some(configurable))
  def mkAccessorProp(
    get: Option[Val],
    set: Option[Val],
    enumerable: Boolean,
    configurable: Boolean
  ): ObjectProp = new ObjectProp(None, get, set, None, Some(enumerable), Some(configurable))
  def strProp(s: String): ObjectProp = mkDataProp(PVal(mkIRStrIR(s)))
  def boolProp(b: Boolean): ObjectProp = mkDataProp(PVal(getIRBool(b)))
  def numProp(d: Double): ObjectProp = mkDataProp(PVal(mkIRNumIR(d)))
  /*
   * 15.1 The Global Object
   * Unless otherwise specified, the standard built-in properties of the global
   * object have attributes {[[Writable]]: true, [[Enumerable]]: false,
   * [[Configurable]]: true}
   */
  def objProp(o: JSObject): ObjectProp = mkDataProp(o, true, false, true)
  def strPropTable(s: String): PropTable = {
    val prop = new PropTable; prop.put(IP.varPrefix + "PrimitiveValue", strProp(s))
    prop
  }
  def boolPropTable(b: Boolean): PropTable = {
    val prop = new PropTable
    prop.put(IP.varPrefix + "PrimitiveValue", boolProp(b))
    prop
  }
  def numPropTable(d: Double): PropTable = {
    val prop = new PropTable
    prop.put(IP.varPrefix + "PrimitiveValue", numProp(d))
    prop
  }
  def inherit(o1: JSObject, o2: JSObject): Boolean = {
    var o1Pt = o1
    while (true) {
      if (o1Pt == IP.nullObj) return false
      else if (o1Pt.equals(o2)) return true
      o1Pt = o1Pt.proto
    }
    false
  }

  def arrayToList(array: JSObject): List[Val] = {
    val length: Int = toNumber(array.get("length")).num.toInt
    val l = for (i <- 0 until length) yield array.get(i.toString)
    l.toList
  }
  def arrayToOptionList(array: JSObject): List[Option[Val]] = {
    val length: Int = toNumber(array.get("length")).num.toInt
    val l = for (i <- 0 until length)
      yield if (array.hasProperty(i.toString)) Some(array.get(i.toString))
    else None
    l.toList
  }
  def argsObjectToArray(argsObj: JSObject, maxLength: Int): Array[Val] = {
    val length: Int = toNumber(argsObj.get("length")).num.toInt
    // If length < maxLength, the args is padded with the undefined.
    (for (i <- 0 until maxLength) yield if (i < length) argsObj.get(i.toString) else IP.undefV).toArray
  }

  def getVal(op: ObjectProp): Val = {
    if (op.value.isDefined) op.value.get
    else if (op.get.isDefined) op.get.get
    else IP.undefV
  }
  //def isGlobal(o: JSObject): Boolean = o.isInstanceOf[JSGlobal]

  def copyEnv(env: Env): Env = env match {
    case EmptyEnv() => env
    case ConsEnv(DeclEnvRec(s), rest) => ConsEnv(DeclEnvRec(s), copyEnv(rest))
    case ConsEnv(ObjEnvRec(o), rest) => ConsEnv(ObjEnvRec(o), copyEnv(rest))
  }

  def valError2NormalCompletion(ve: ValError): Unit = ve match {
    case res: Val => I.IS.comp.setNormal(res)
    case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
  }

  def valError2ReturnCompletion(ve: ValError): Unit = ve match {
    case res: Val => I.IS.comp.setReturn(res)
    case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
  }

  /*
   * 10.4.3 Entering Function Code
   */
  def getThis(v: Val): ValError = v match {
    case PVal(IRVal(EJSUndef | EJSNull)) => I.IS.GlobalObject
    case PVal(IRVal(_: EJSBool | _: EJSNumber | _: EJSString)) => toObject(v)
    case o: JSObject => o
  }

  def call(info: ASTNodeInfo, tb: Val, argsObject: JSObject, fun: JSFunction): Unit = {
    val argsObj = newArgObj(fun, fun.code.args.size, argsObject, false)
    I.IS.env = fun.scope
    val argumentsId = fun.code.params(1)
    newDeclEnv()
    createAndSetBinding(argumentsId, argsObject, false, I.IS.eval) match {
      case vf: Val =>
        I.IS.tb = getThis(tb) match {
          case o: JSObject => o
          case err: JSError => I.IS.comp.setThrow(err, info.span); return
        }

        if (fun.const) fun.call(I.IS.tb, argsObj)
        else if (fun.builtin != null) fun.builtin.callBuiltinFunction(fun, argsObj)
        else I.walkIRs(fun.codeIRs)
      case err: JSError => I.IS.comp.setThrow(err, info.span)
    }
  }

  /*
   * 10.2.1 Environment Records
   * 10.2.1.1 Declarative Environment Records
   * 10.2.1.1.1 HasBinding(N)
   * 10.2.1.2 Object Environment Records
   * 10.2.1.2.1 HasBinding(N)
   */
  def hasBinding(x: Var): Boolean = {
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() => return I.IS.GlobalObject.hasProperty(x)
        case ConsEnv(envRec, rest) => envRec match {
          case DeclEnvRec(s) => return isInDomS(s, x)
          case ObjEnvRec(_) => env = rest
        }
      }
    }
    false
  }
  def isInDomS(s: Store, x: Var): Boolean = s.contains(x)

  /*
   * 10.2.1.1.2 CreateMutableBinding(N,D)
   * A binding must not already exist in this Environment Record for N
   */
  def createBinding(x: IRId, mutable: Boolean, deletable: Boolean): Env = {
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() =>
          if (x.isInstanceOf[IRUserId]) {
            I.IS.GlobalObject.putProp(x.originalName, mkDataProp(IP.undefV, true, true,
              mutable && deletable))
          } else {
            I.IS.GlobalObject.declEnvRec.s += x.originalName -> new StoreValue(IP.undefV, false, true, true)
          }
          return env
        case ConsEnv(envRec, rest) => envRec match {
          case DeclEnvRec(s) =>
            // createBindingToDeclarative()
            s += x.originalName -> new StoreValue(IP.undefV, false, mutable, mutable && deletable)
            return env
          case ObjEnvRec(_) =>
            env = rest
        }
      }
    }
    throw new InterpreterError("createBinding: x=" + x, I.IS.span)
  }
  // Target environment is specified.
  def createBinding(env: Env, x: IRId, mutable: Boolean, deletable: Boolean): Unit = {
    env match {
      case EmptyEnv() =>
        if (x.isInstanceOf[IRUserId]) {
          I.IS.GlobalObject.putProp(x.originalName, mkDataProp(IP.undefV, true, true,
            mutable && deletable))
        } else {
          I.IS.GlobalObject.declEnvRec.s += x.originalName -> new StoreValue(IP.undefV, false, true, true)
        }
      case ConsEnv(envRec, rest) => envRec match {
        case DeclEnvRec(s) =>
          // createBindingToDeclarative()
          s += x.originalName -> new StoreValue(IP.undefV, false, mutable, mutable && deletable)
      }
    }
  }
  // Create a variable to declarative environment
  def createBindingToDeclarative(declEnvRec: DeclEnvRec, x: IRId, mutable: Boolean, deletable: Boolean): Unit = {
    declEnvRec.s += x.originalName -> new StoreValue(IP.undefV, false, mutable, mutable && deletable)
  }

  // Create and set at once
  def createAndSetBinding(x: IRId, v: Val, mutable: Boolean, deletable: Boolean): ValError = {
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() =>
          if (x.isInstanceOf[IRUserId]) I.IS.GlobalObject.putProp(x.originalName, mkDataProp(v, true, true, mutable && deletable))
          else I.IS.GlobalObject.declEnvRec.s += x.originalName -> new StoreValue(v, true, true, true)
          return v
        case ConsEnv(envRec, rest) => envRec match {
          case DeclEnvRec(s) =>
            s += x.originalName -> new StoreValue(v, true, mutable, mutable && deletable)
            return v
          case ObjEnvRec(_) =>
            env = rest
        }
      }
    }
    throw new InterpreterError("createBinding: x=" + x, I.IS.span)
  }

  /*
   * 10.2.1.1.3 SetMutableBinding(N,V,S)
   */
  def setBinding(x: IRId, v: Val, ignoreImmutable: Boolean, strict: Boolean): ValError = {
    val originalName = x.originalName
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() =>
          if (debug > 0) {
            System.out.println("setBinding:x=" + x + " v=" + v)
          }
          if (x.isInstanceOf[IRUserId]) {
            I.IS.GlobalObject.getProp(originalName).get.value = Some(v)
          } else {
            I.IS.GlobalObject.declEnvRec.s(originalName).setValue(v)
          }
          return v
        case ConsEnv(er, rest) => er match {
          case DeclEnvRec(s) =>
            // setBindingToDeclarative()
            val sv = s(originalName)
            if (sv == null) {
              throw new InterpreterError("setBinding:x=" + x + " v=" + v, I.IS.span)
            }
            if (sv.mutable || ignoreImmutable) {
              s(originalName).setValue(v)
            } else if (strict) {
              return IP.typeError
            }
            return v
          case ObjEnvRec(_) =>
            env = rest
        }
      }
    }
    throw new InterpreterError("setBinding:x=" + x + " v=" + v, I.IS.span)
  }
  // Target environment is specified.
  def setBinding(env: Env, x: IRId, v: Val, ignoreImmutable: Boolean, strict: Boolean): ValError = {
    val originalName = x.originalName
    env match {
      case EmptyEnv() =>
        if (x.isInstanceOf[IRUserId]) I.IS.GlobalObject.getProp(originalName).get.value = Some(v)
        else I.IS.GlobalObject.declEnvRec.s(originalName).setValue(v)
        v
      case ConsEnv(envRec, rest) => envRec match {
        case DeclEnvRec(s) =>
          // setBindingToDeclarative()
          val sv = s(originalName)
          if (sv == null) throw new InterpreterError("setBinding:x=" + x + " v=" + v, I.IS.span)
          if (sv.mutable || ignoreImmutable) s(originalName).setValue(v)
          else if (strict) return IP.typeError
          v
      }
    }
  }
  // Set a variable of declarative environment
  def setBindingToDeclarative(declEnvRec: DeclEnvRec, x: IRId, v: Val, ignoreImmutable: Boolean, strict: Boolean): ValError = {
    val originalName = x.originalName
    val sv = declEnvRec.s(originalName)
    if (sv == null) throw new InterpreterError("setBindingToDeclarative:x=" + x + " v=" + v, I.IS.span)
    if (sv.mutable || ignoreImmutable) declEnvRec.s(originalName).setValue(v)
    else if (strict) return IP.typeError
    v
  }

  /*
   * 10.2.1.1.4 GetBindingValue(N,S)
   * Assert: x \in Dom(\sigma)
   * 10.2.1.2.4 GetBindingValue(N,S)
   */
  def getBindingValue(er: EnvRec, x: Var): ValError = er match {
    case DeclEnvRec(s) =>
      val sv = s(x)
      if (sv.init || sv.mutable) sv.value
      else {
        if (I.IS.strict) IP.referenceError(x + " from getBindingValue")
        else IP.undefV
      }
    case ObjEnvRec(o) =>
      if (o == IP.nullObj) return IP.referenceError(x + " from getBindingValue")
      val v = o.get(x)
      if (!isUndef(v)) v
      else if (I.IS.strict) IP.referenceError(x + " from getBindingValue")
      else IP.undefV
  }

  /*
   * 10.2.1.1.7 CreateImmutableBinding(N)
   * A binding must not already exist in this Environment Record for N
   */
  /*def createImmBinding(IS: InterpreterState, x: Var, b: Boolean) = IS.env match {
    case ce@ConsEnv(DeclEnvRec(s), _) =>
      s.put(x, new StoreValue(IP.undefV, false, false, b))
      ce.first = DeclEnvRec(s)
    case _ =>
      throw new InterpreterError("createImmutableBinding: "+x, IS.span)
  }*/

  /*
   * 10.2.1.1.8 InitializeImmutableBinding(N,V)
   */
  /*def initImmBinding(IS: InterpreterState, x: Var, v: Val, b: Boolean): ValError = IS.env match {
    case ConsEnv(DeclEnvRec(s), _) =>
      s.put(x, new StoreValue(v, true, false, b))
      v
    case _ =>
      throw new InterpreterError("initializeImmutableBinding: "+x, IS.span)
  }*/

  /*
   * 10.2.2.1 GetIdentifierReference(lex, name, strict)
   */
  def lookup(x: IRId): EnvRec = {
    val originalName = x.originalName
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() =>
          x match {
            case _: IRUserId =>
              if (I.IS.GlobalObject.isInDomO(originalName)) {
                return I.IS.globalObjEnvRec
              }
            case _: IRTmpId =>
              if (I.IS.GlobalObject.declEnvRec.s.contains(originalName)) {
                return I.IS.GlobalObject.declEnvRec
              }
          }
          return I.IS.nullObjEnvRec
        case ConsEnv(er, rest) => er match {
          case DeclEnvRec(s) =>
            if (s.contains(originalName)) {
              return er
            } else {
              env = rest
            }
          case ObjEnvRec(o) =>
            val (_, obj) = o.getProperty(originalName)
            if (obj != IP.nullObj) return er
            else env = rest
        }
      }
    }
    throw new InterpreterError("lookup: " + x, I.IS.span)
  }

  def isInt(s: String): Boolean = {
    try {
      s.toInt
      true
    } catch {
      case _: NumberFormatException => false
    }
  }
  def isUint(s: String): Boolean = {
    try {
      s.toInt >= 0
    } catch {
      case _: NumberFormatException => false
    }
  }

  /*
   * 12.6.4 The for-in Statement
   */
  def iteratorInit(pn: List[PName]): JSObject = {
    val prop = propTable
    prop.put("@i", numProp(0))
    prop.put("length", numProp(pn.size))
    for (i <- pn.indices) prop.put(i.toString, strProp(pn(i)))
    newObj(prop)
  }
  def collectProps(obj: JSObject): List[PName] = {
    if (obj == IP.nullObj) Nil
    else obj.property.keys ++ collectProps(obj.proto)
  }
  def next(o: JSObject, n: Int, obj: JSObject): Int = {
    if (!o.isInDomO(n.toString)) {
      if (n >= toInt(o.property.get("length").get)) n else next(o, n + 1, obj)
    } else if (obj != IP.nullObj && obj.isEnumerable(toStr(o.property.get(n.toString).get))) n
    else next(o, n + 1, obj)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Internal type conversion
  ////////////////////////////////////////////////////////////////////////////////

  def toInt(op: ObjectProp): Int = {
    if (op.value.isDefined) {
      val v: Val = op.value.get
      if (v.isInstanceOf[PVal]) {
        val pv: IRVal = v.asInstanceOf[PVal].v
        if (pv.value.isInstanceOf[EJSNumber]) return pv.value.asInstanceOf[EJSNumber].num.toInt
      }
    }
    throw new InterpreterError("toStr", I.IS.span)
  }
  def toStr(op: ObjectProp): String = {
    if (op.value.isDefined) op.value.get.toString
    else throw new InterpreterError("toStr", I.IS.span)
  }
  def toVal(op: ObjectProp): Val = {
    if (op.value.isDefined) op.value.get
    else throw new InterpreterError("toStr", I.IS.span)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types
  ////////////////////////////////////////////////////////////////////////////////

  def typeOf(v: Val): EJSTypeEnum = v match {
    case PVal(IRVal(EJSUndef)) => EJSType.UNDEFINED
    case PVal(IRVal(EJSNull)) => EJSType.NULL
    case PVal(IRVal(_: EJSBool)) => EJSType.BOOLEAN
    case PVal(IRVal(_: EJSNumber)) => EJSType.NUMBER
    case PVal(IRVal(_: EJSString)) => EJSType.STRING
    case _: JSObject => EJSType.OBJECT
  }
  def typeTag(v: Val): String = v match {
    case PVal(IRVal(EJSUndef)) => "undefined"
    case PVal(IRVal(EJSNull)) => "object"
    case PVal(IRVal(_: EJSBool)) => "boolean"
    case PVal(IRVal(_: EJSNumber)) => "number"
    case PVal(IRVal(_: EJSString)) => "string"
    case _: JSObject => if (!isCallable(v)) "object" else "function"
  }

  /*
   * 8.7 The Reference Specification Type
   * 8.7.1 GetValue(V)
   * We inlined the GetValue function in its call-sites: IRLoad and walkId.
   */

  /*
   * 8.7.2 PutValue(V)
   */
  def putValue(x: IRId, v: Val, b: Boolean): ValError = {
    val bs = lookup(x)
    if (debug > 0)
      System.out.println("putValue:x=" + x.uniqueName + " v=" + v + " bs=" + bs +
        " isInternal(x)=" + NU.isInternal(x.uniqueName))

    bs match {
      // If a generated name by Translator, create a binding.
      case oer: ObjEnvRec =>
        val originalName = x.originalName
        if (oer.o != IP.nullObj) oer.o.put(originalName, v, b)
        else if (NU.isInternal(originalName)) createAndSetBinding(x, v, true, I.IS.eval)
        else if (b) IP.referenceError(x + " from putValue")
        else I.IS.GlobalObject.put(originalName, v, false)
      case der: DeclEnvRec =>
        setBindingToDeclarative(der, x, v, false, b)
    }
  }

  /*
   * 8.10.1 IsAccessorDescriptor(Desc)
   */
  def isAccessorDescriptor(op: ObjectProp): Boolean = (op.get.isDefined || op.set.isDefined)

  /*
   * 8.10.2 IsDataDescriptor(Desc)
   */
  def isDataDescriptor(op: ObjectProp): Boolean = (op.value.isDefined || op.writable.isDefined)

  /*
   * 8.10.3 IsGenericDescriptor(Desc)
   */
  def isGenericDescriptor(op: ObjectProp): Boolean = (!isAccessorDescriptor(op) && !isDataDescriptor(op))

  /*
   * 8.10.4 FromPropertyDescriptor(Desc)
   */
  def fromPropertyDescriptor(op: ObjectProp): JSObject = {
    // The following algorithm assumes that Desc is a fully populated Property Descriptor, such as that returned from [[GetOwnProperty]]
    // 2. Let obj be the result of creating a new object as if by the expression new Object() where Object is the standard built-in constructor with that name.
    var obj: JSObject = newObj()
    // 3. If IsDataDescriptor(Desc) is true, then
    if (isDataDescriptor(op)) {
      if (op.value.isEmpty || op.writable.isEmpty) throw new InterpreterError("fromPropertyDescriptor: 3", I.IS.span)
      obj.defineOwnProperty("value", mkDataProp(op.value.get, true, true, true), false)
      obj.defineOwnProperty("writable", mkDataProp(PVal(getIRBool(op.writable.get)), true, true, true), false)
    } // 4. If IsAccessorDescriptor(Desc) must be true, so
    else if (isAccessorDescriptor(op)) {
      if (op.get.isEmpty || op.set.isEmpty) throw new InterpreterError("fromPropertyDescriptor: 4", I.IS.span)
      obj.defineOwnProperty("get", mkDataProp(op.get.get, true, true, true), false)
      obj.defineOwnProperty("set", mkDataProp(op.set.get, true, true, true), false)
    }
    // 5.
    obj.defineOwnProperty("enumerable", mkDataProp(PVal(getIRBool(op.isEnumerable)), true, true, true), false)
    // 6.
    // NOTE: We don't make a property if the value does not exist.
    obj.defineOwnProperty("configurable", mkDataProp(PVal(getIRBool(op.isConfigurable)), true, true, true), false)
    // 7. Return obj.
    obj
  }

  /*
   * 8.10.5 ToPropertyDescriptor(Obj)
   */
  def toPropertyDescriptor(v: Val): (Option[ObjectProp], Option[JSError]) = {
    // 1. If Type(Obj) is not Object throw a TypeError exception.
    if (typeOf(v) != EJSType.OBJECT) return (None, Some(IP.typeError))
    val obj: JSObject = v.asInstanceOf[JSObject]
    // 2. Let desc be the result of creating a new Property Descriptor that initially has no fields.
    val op: ObjectProp = mkEmptyObjectProp()
    // 3 ~ 6
    if (obj.hasProperty("enumerable")) op.enumerable = Some(toBoolean(obj.get("enumerable")))
    if (obj.hasProperty("configurable")) op.configurable = Some(toBoolean(obj.get("configurable")))
    if (obj.hasProperty("value")) op.value = Some(obj.get("value"))
    if (obj.hasProperty("writable")) op.writable = Some(toBoolean(obj.get("writable")))
    // 7 ~ 8
    if (obj.hasProperty("get")) {
      val getter = obj.get("get")
      if (!isCallable(getter) && !isUndef(getter)) return (None, Some(IP.typeError))
      op.get = Some(obj.get("get"))
    }
    if (obj.hasProperty("set")) {
      val setter = obj.get("set")
      if (!isCallable(setter) && !isUndef(setter)) return (None, Some(IP.typeError))
      op.set = Some(obj.get("set"))
    }
    // 9. If either desc.[[Get]] or desc.[[Set]] are present, then
    if (op.get.isDefined || op.set.isDefined) {
      // a. If either desc.[[Value]] or desc.[[Writable]] are present, then throw a TypeError exception.
      if (op.value.isDefined || op.writable.isDefined) return (None, Some(IP.typeError))
    }
    (Some(op), None)
  }

  def delete(x: String, b: Boolean): ValError = {
    var env = I.IS.env
    while (true) {
      env match {
        case EmptyEnv() =>
          if (I.IS.GlobalObject.isInDomO(x)) return I.IS.GlobalObject.delete(x, b)
          else return IP.truePV
        case ConsEnv(envRec, rest) => envRec match {
          case DeclEnvRec(s) =>
            val sv = s(x)
            if (sv != null) {
              if (sv.configurable) {
                s.remove(x)
                return IP.truePV
              } else return IP.falsePV
            } else env = rest
          case ObjEnvRec(o) =>
            if (o.isInDomO(x)) return o.delete(x, b)
            else env = rest
        }
      }
    }
    throw new InterpreterError("delete: x=" + x, I.IS.span)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 9. Type Conversion and Testing
  ////////////////////////////////////////////////////////////////////////////////

  val decimalFormat: DecimalFormat = new DecimalFormat("#")

  /*
   * 9 Type Conversion and Testing
   * 9.1 ToPrimitive
   */
  def toPrimitive(v: Val, hint: String): PVal = v match {
    case o: JSObject => o.defaultValue(hint)
    case pv: PVal => pv
  }

  /*
   * 9.2 ToBoolean
   */
  def toBoolean(v: Val): Boolean = v match {
    case PVal(IRVal(EJSUndef)) => false
    case PVal(IRVal(EJSNull)) => false
    case PVal(IRVal(EJSBool(b))) => b
    case PVal(IRVal(EJSNumber("NaN", _))) => false
    case PVal(IRVal(EJSNumber(_, d))) => d != 0
    case PVal(IRVal(EJSString(""))) => false
    case PVal(IRVal(EJSString(text))) => true
    case _: JSObject => true
  }

  /*
   * 9.3 ToNumber
   */
  def modulo(x: Double, y: Long): Long = {
    val result = math.abs(x.toLong) % math.abs(y)
    if (math.signum(x) < 0) return math.signum(y) * (math.abs(y) - result)
    math.signum(y) * result
  }

  def toNumber(v: Val): EJSNumber = v match {
    case PVal(IRVal(EJSUndef)) => IP.NaN
    case PVal(IRVal(EJSNull)) => IP.plusZero
    case PVal(IRVal(EJSBool(b))) => if (b) mkIRNum(1) else IP.plusZero
    case PVal(IRVal(n: EJSNumber)) => n
    case PVal(IRVal(EJSString(text))) =>
      val reg =
        "[+-]?(" +
          "([0-9]+[.]?([0-9]+)?([eE][+-]?[0-9]+)?)|" +
          "([.][0-9]+([eE][+-]?[0-9]+)?)|" +
          "(0[xX][0-9a-fA-F]+))"
      text.trim match {
        case "" => IP.plusZero
        case str if (str.matches("[+]?Infinity")) => IP.plusInfinity
        case str if (str.equals("-Infinity")) => IP.minusInfinity
        case str if (str.matches(reg)) =>
          val (sign, s) = if (str.startsWith("-")) ("-", str.slice(1, str.size))
          else if (str.startsWith("+")) ("", str.slice(1, str.size))
          else ("", str)
          val hex = s.startsWith("0x") || s.startsWith("0X")
          val n = if (hex) sign + s.slice(2, str.size) else sign + s
          if (!hex) mkIRNum(java.lang.Double.parseDouble(n))
          else if (!n.contains("."))
            try {
              mkIRNum(java.lang.Integer.parseInt(n, 16))
            } catch {
              case e: Throwable => mkIRNum((new java.math.BigInteger(n, 16)).doubleValue)
            }
          else mkIRNum(java.lang.Double.parseDouble(n))
        case _ => IP.NaN
      }
    case _ => toNumber(toPrimitive(v, "Number"))
  }

  /*
   * 9.4 ToInteger
   */
  def toInteger(v: Val): EJSNumber = toNumber(v) match {
    case n if isNaN(n) => IP.plusZero
    case n if (isZero(n) || isInfinite(n)) => n
    case n => mkIRNum(math.signum(n.num) * math.floor(math.abs(n.num)))
  }

  /*
   * 9.5 ToInt32: (Signed 32 Bit Integer)
   */
  def toInt32(v: Val): Int = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val n = toNumber(v)
    // 2. If number is NaN, +0, -0, +INF or -INF, return +0.
    if (isNaN(n) || n.num == 0 || isInfinite(n)) return 0
    // 3. Let posInt be sign(number) * floor(abs(number))
    val posInt = math.signum(n.num) * math.floor(math.abs(n.num))
    // 4. Let int32bit be posInt modulo 2^32; that is, ...
    val int32bit = modulo(posInt, 0x100000000L);
    // 5. If int32bit is greater than or equal to 2^31, return int32bit - 2^32, otherwise return int32bit.
    if (int32bit >= 0x80000000L) (int32bit - 0x100000000L).toInt else int32bit.toInt
  }

  /*
   * 9.6 ToUint32: (Unsigned 32 Bit Integer)
   */
  def toUint32(v: Val): Long = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val n = toNumber(v)
    // 2. If number is NaN, +0, -0, +INF or -INF, return +0.
    if (isNaN(n) || n.num == 0 || isInfinite(n)) return 0
    // 3. Let posInt be sign(number) * floor(abs(number))
    val posInt = math.signum(n.num) * math.floor(math.abs(n.num))
    // 4. Let int32bit be posInt modulo 2^32; that is, ...
    val int32bit = modulo(posInt, 0x100000000L)
    // 5. Return int32bit.
    int32bit.toLong
  }

  /*
   * 9.7 ToUint16: (Unsigned 16 Bit Integer)
   */
  def toUint16(v: Val): Int = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val n = toNumber(v)
    // 2. If number is NaN, +0, -0, +INF or -INF, return +0.
    if (isNaN(n) || n.num == 0 || isInfinite(n)) return 0
    // 3. Let posInt be sign(number) * floor(abs(number))
    val posInt = math.signum(n.num) * math.floor(math.abs(n.num))
    // 4. Let int16bit be posInt modulo 2^16; that is, ...
    val int16bit = modulo(posInt, 0x10000);
    // 5. Return int16bit.
    int16bit.toInt
  }

  /*
   * 9.8 ToString
   */
  def toString(v: Val): String = v match {
    case PVal(IRVal(EJSUndef)) => "undefined"
    case PVal(IRVal(EJSNull)) => "null"
    case PVal(IRVal(EJSBool(b))) => b.toString
    case PVal(IRVal(n: EJSNumber)) => toString(n)
    case PVal(IRVal(s: EJSString)) => s.str
    case _: JSObject => toString(toPrimitive(v, "String"))
  }
  def toString(n: EJSNumber): String = {
    if (isNaN(n)) "NaN"
    else if (isPlusZero(n) || isMinusZero(n)) "0"
    else if (n.num < 0) "-" + toString(negate(n))
    else if (isInfinite(n)) "Infinity"
    else {
      // TODO: 9.8.1.5-10.
      toString(n.num)
    }
  }
  def toString(n: Double): String = {
    if (n % 1 == 0) decimalFormat.format(n).toLowerCase
    else n.toString.toLowerCase
  }

  /*
   * 9.9 ToObject
   */
  def toObject(v: Val): ValError = v match {
    case PVal(IRVal(EJSUndef | EJSNull)) => IP.typeError
    case PVal(IRVal(s: EJSString)) => I.IS.StringConstructor.construct(Some(PVal(mkIRStrIR(s.str))))
    case PVal(IRVal(s: EJSNumber)) => I.IS.NumberConstructor.construct(Some(v))
    case PVal(IRVal(s: EJSBool)) => I.IS.BooleanConstructor.construct(v)
    case obj: JSObject => obj
  }

  /*
   * 9.10 CheckObjectCoercible
   */
  def checkObjectCoercible(v: Val): ValError = v match {
    case PVal(IRVal(EJSUndef | EJSNull)) => IP.typeError
    case _ => v
  }

  /*
   * 9.11 IsCallable
   */
  def isCallable(v: Val): Boolean = v.isInstanceOf[JSFunction]

  /*
   * 9.12 SameValue(x, y)
   */
  def sameValue(x: Val, y: Val): Boolean = {
    // The equals() function of Val is overrided.
    x.equals(y)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 11. Expressions
  ////////////////////////////////////////////////////////////////////////////////

  /*
   * 11.5.1 Applying the * Operator
   *   The result of a floating-point multiplication is governed by the rules of IEEE 754 binary double-precision arithmetic: ...
   */
  def applyMultiplication(leftNum: EJSNumber, rightNum: EJSNumber): EJSNumber = mkIRNum(leftNum.num * rightNum.num)

  /*
   * 11.5.2 Applying the / Operator
   *   The result of devision is determined by the specification of IEEE 754 arithmetic: ...
   */
  def applyDivision(leftNum: EJSNumber, rightNum: EJSNumber): EJSNumber = mkIRNum(leftNum.num / rightNum.num)

  /*
   * 11.5.3 Applying the % Operator
   *   The result of a floating-point remainder operation as computed by the % operator is not the same as the "remainder" operation defined by IEEE 754.
   *   The IEEE 754 "remainder" operation computers the remainder from a rounding division, not a truncating division,
   *   and so its behaviour is not analogous to that of the usual integer remainder operator.
   *   Instead the ECMAScript language defined % on floating-point operations to behave in a manner analogous to
   *   that of the java integer remainder operator; this may be compared with the C library function fmod.
   */
  def applyReminder(leftNum: EJSNumber, rightNum: EJSNumber): EJSNumber = mkIRNum(leftNum.num % rightNum.num)

  /*
   * 11.6.3 Applying the Additive Operators to Numbers
   *   The result of an addition is determined using the rules of IEEE 754 binary double-precision arithmetic: ...
   */
  def applyAddition(leftNum: EJSNumber, rightNum: EJSNumber): EJSNumber = mkIRNum(leftNum.num + rightNum.num)

  /*
   * 11.8.5 The Abstract Relational Comparison Algorithm
   *   Returns true, false or undefined.
   */
  def abstractRelationalComparison(x: Val, y: Val, leftFirst: Boolean = true): PVal = {
    // 1. If the LeftFirst flag is true, then
    //   a. Let px be the result of calling ToPrimitive(x, hint Number).
    //   b. Let py be the result of calling ToPrimitive(y, hint Number).
    // 2. Else the order of evaluation needs to be reversed to preserve left to right evaluation
    //   a. Let py be the result of calling ToPrimitive(y, hint Number).
    //   b. Let px be the result of calling ToPrimitive(x, hint Number).
    val (px, py) = if (leftFirst)
      (toPrimitive(x, "Number"), toPrimitive(y, "Number"))
    else {
      val t = toPrimitive(y, "Number")
      (toPrimitive(x, "Number"), t)
    }
    (px, py) match {
      // 4. Else, both px, and py are Strings
      /*
       * NOTE 2
       *   The comparison of Strings uses a simple lexicographic ordering on sequences of code unit values.
       *   There is no attempt to use the more complex, semantically oriented definitions of character or
       *   string equality and collating order defined in the Unicode specification.
       *   Therefore String values that are canonically equal according to the Unicode standard could test as unequal.
       */
      case (PVal(IRVal(EJSString(sx))), PVal(IRVal(EJSString(sy)))) => PVal(getIRBool(sx < sy))
      // 3. If it is not the case that both type(px) is String and Type(py) is String, then
      // a. Let nx be the result of calling ToNumber(px).
      // b. Let ny be the result of calling ToNumber(py).
      case _ => (toNumber(px), toNumber(py)) match {
        // c. If nx is NaN, return undefined.
        // d. If ny is NaN, return undefined.
        case (nx, ny) if (isNaN(nx) || isNaN(ny)) => IP.undefV
        // e. If nx and ny are the same Number value, return false.
        case (nx, ny) if abstractEqualityComparison(PVal(IRVal(nx)), PVal(IRVal(ny))) => IP.falsePV
        // f. If nx is +0 and ny is -0, return false.
        case (nx, ny) if (isPlusZero(nx) && isMinusZero(ny)) => IP.falsePV
        // g. If nx is -0 and ny is +0, return false.
        case (nx, ny) if (isMinusZero(nx) && isPlusZero(ny)) => IP.falsePV
        // h. If nx is +INF, return false.
        case (nx, _) if isPlusInfinity(nx) => IP.falsePV
        // i. If ny is +INF, return true.
        case (_, ny) if isPlusInfinity(ny) => IP.truePV
        // j. If ny is -INF, return false.
        case (_, ny) if isMinusInfinity(ny) => IP.falsePV
        // k. If nx is -INF, return true.
        case (nx, _) if isMinusInfinity(nx) => IP.truePV
        // l. If the mathematical value of nx is less than the mathematical value of ny ...
        case (nx, ny) => PVal(getIRBool(nx.num < ny.num))
      }
    }
  }

  /*
   * 11.9.3 The Abstract Equality Comparison Algorithm
   */
  def abstractEqualityComparison(x: Val, y: Val): Boolean = (typeOf(x), typeOf(y)) match {
    // 1. If Type(x) is the same as Type(y), then
    case (typeOfX, typeOfY) if typeOfX == typeOfY => (x, y) match {
      // a. If Type(x) is Undefined, return true.
      case (PVal(IRVal(EJSUndef)), PVal(IRVal(EJSUndef))) => true
      // b. If Type(x) is Null, return true.
      case (PVal(IRVal(EJSNull)), PVal(IRVal(EJSNull))) => true
      // c. If Type(x) is Number, then
      case (PVal(IRVal(x1: EJSNumber)), PVal(IRVal(y1: EJSNumber))) => {
        //  i. If x is NaN, return false.
        // ii. If y is NaN, return false.
        if (isNaN(x1) || isNaN(y1)) false
        // iii. If x is the same Number value as y, return true.
        else if (x1.num == y1.num) true
        // iv. If x is +0 and y is -0, return true.
        //  v. If x is -0 and y is +0, return true.
        else if ((isPlusZero(x1) && isMinusZero(y1)) || (isMinusZero(x1) && isPlusZero(y1))) true
        // vi. Return false.
        else false
      }
      // d. If Type(x) is String, ...
      case (PVal(IRVal(EJSString(sx))), PVal(IRVal(EJSString(sy)))) => sx == sy
      // e. If Type(x) is Boolean, ...
      case (PVal(IRVal(EJSBool(bx))), PVal(IRVal(EJSBool(by)))) => bx == by
      // f. Return true if x and y refer to the same object. Otherwise, return false.
      case (ox: JSObject, oy: JSObject) => ox == oy
    }
    // 2. If x is null and y is undefined, return true.
    case (EJSType.NULL, EJSType.UNDEFINED) => true
    // 3. If x is undefined and y is null, return true.
    case (EJSType.UNDEFINED, EJSType.NULL) => true
    // 4. If Type(x) is Number and Type(y) is String,
    case (typeOfX, EJSType.STRING) if typeOfX.isNumber =>
      // return the result of the comparison x == ToNumber(y).
      abstractEqualityComparison(x, PVal(IRVal(toNumber(y))))
    // 5. If Type(x) is String and Type(y) is Number,
    case (EJSType.STRING, typeOfY) if typeOfY.isNumber =>
      // return the result of the comparison ToNumber(x) == y.
      abstractEqualityComparison(PVal(IRVal(toNumber(x))), y)
    // 6. If Type(x) is Boolean, return the result of the comparison ToNumber(x) == y.
    case (EJSType.BOOLEAN, typeOfY) => abstractEqualityComparison(PVal(IRVal(toNumber(x))), y)
    // 7. If Type(y) is Boolean, return the result of the comparison x == ToNumber(y).
    case (typeOfX, EJSType.BOOLEAN) => abstractEqualityComparison(x, PVal(IRVal(toNumber(y))))
    // 8. If Type(x) is either String or Number and Type(y) is Object,
    case (typeOfX, EJSType.OBJECT) if (typeOfX.isString || typeOfX.isNumber) =>
      // return the result of the comparison x == ToPrimitive(y).
      abstractEqualityComparison(x, toPrimitive(y, typeOfX.toString))
    // 9. If Type(x) is either String or Number and Type(y) is Object,
    case (EJSType.OBJECT, typeOfY) if (typeOfY.isString || typeOfY.isNumber) =>
      // return the result of the comparison ToPrimitive(x) == y.
      abstractEqualityComparison(y, toPrimitive(x, typeOfY.toString))
    // 10. Return false.
    case _ => false
  }

  /*
   * 11.9.6 The Strict Equality Comparison Algorithm
   */
  def abstractStrictEqualityComparison(x: Val, y: Val): Boolean = (x, y) match {
    // 2. If Type(x) is Undefined, return true.
    case (PVal(IRVal(EJSUndef)), PVal(IRVal(EJSUndef))) => true
    // 3. If Type(x) is Null, return true.
    case (PVal(IRVal(EJSNull)), PVal(IRVal(EJSNull))) => true
    // 4. If Type(x) is Number, then
    case (PVal(IRVal(x1: EJSNumber)), PVal(IRVal(y1: EJSNumber))) => {
      // a. If x is NaN, return false.
      // b. If y is NaN, return false.
      if (isNaN(x1) || isNaN(y1)) false
      // c. If x is the same Number value as y, return true.
      else if (x1.num == y1.num) true
      // d. If x is +0 and y is -0, return true.
      // e. If x is -0 and y is +0, return true.
      else if ((isPlusZero(x1) && isMinusZero(y1)) || (isMinusZero(x1) && isPlusZero(y1))) true
      // f. Return false.
      else false
    }
    // 5. If Type(x) is String, then return true if x and y are exactly the same sequence of characters (...); otherwise, return false.
    case (PVal(IRVal(EJSString(sx))), PVal(IRVal(EJSString(sy)))) => sx == sy
    // 6. If Type(x) is Boolean, return true if x and y are both true or both false; otherwise, return false.
    case (PVal(IRVal(EJSBool(bx))), PVal(IRVal(EJSBool(by)))) => bx == by
    // 7. Return true if x and y refer to the same object. Otherwise, return false.
    case (ox: JSObject, oy: JSObject) => ox == oy
    // 1. If Type(x) is different from Type(y), return false.
    case _ => false
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 13. Function Definition
  ////////////////////////////////////////////////////////////////////////////////

  /*
   * 13.2 Creating Function Objects
   * The following properties are omitted for now:
   * [[Get]], [[Call]], [[Construct]], [[HasInstance]], [[FormalParameters]]
   */
  def createFunctionObject(code: IRFunctional, scope: Env, strict: Boolean): JSFunction = {
    // 1 ~ 13
    // TODO:
    val F: JSFunction = new JSFunction13(I, I.IS.FunctionPrototype, "Function", true, propTable, code, scope)
    // 14. Let len be the number of formal parameters specified in FormalparameterList. If no parameters are specified, let len be 0.
    // TODO:
    val len = code.args.size
    // 15. Call the [[DefineOwnProperty]] internal method of F with arguments "length", Property Descriptor
    //     {[[Value]]: len, [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false}, and false.
    F.defineOwnProperty("length", numProp(len), false)
    // 16. Let proto be the result of creating a new object as would be constructed by the expression new Object()
    //     where Object is the standard built-in constructor with that name.
    val proto: JSObject = new JSObject(I, I.IS.ObjectPrototype, "Object", true, propTable) // JSObject::_construct()
    // 17. Call the [[DefineOwnProperty]] internal method of proto with arguments "constructor", Property Descriptor
    //     {[[Value]]: F, [[Writable]]: true, [[Enumerable]]: false, [[Configurable]]: true}, and false.
    proto.defineOwnProperty("constructor", objProp(F), false)
    // 18. Call the [[DefineOwnProperty]] internal method of F with arguments "prototype", Property Descriptor
    //     {[[Value]]: proto, [[Writable]]: true, [[Enumerable]]: false, [[Configurable]]: true}, and false.
    F.defineOwnProperty("prototype", objProp(proto), false)
    // 19. If Strict is true, then
    if (strict == true) {
      // TODO:
    }
    // 20. Return F
    return F
  }
  /*def newFtnObject(IS:InterpreterState, fv: IRFunctional): Loc = {
    val (funcLoc, propLoc) = (IS.newLoc, IS.newLoc)
    val propT = propTable + ("constructor" -> locProp(funcLoc))
    val propO = newObj(propLoc, propT)
    IS.heap = IS.heap + (funcLoc -> newFtnObj(funcLoc, fv, IS.env, propLoc, IS.strict), propLoc -> propO)
    funcLoc
  }
  def newFtnObj(ownLoc: Loc, fv: IRFunctional, env: Env, propO: Loc, b: Boolean): JSFunction = {
    val propT = propTable + ("prototype" -> locProp(propO),
                             "length" -> numProp(fv.getArgs.size))
    new JSFunction13(ownLoc, IP.lFunctionPrototype, "Function", true, propT, fv, env)
  }*/

  ////////////////////////////////////////////////////////////////////////////////
  // 15. Standard Built-in ECMAScript Objects
  ////////////////////////////////////////////////////////////////////////////////

  def newDeclEnv(): DeclEnvRec = {
    val newEnv = new DeclEnvRec(newStore)
    I.IS.env = new ConsEnv(newEnv, I.IS.env)
    newEnv
  }
  def popEnv(): Unit = I.IS.env match {
    case EmptyEnv() =>
    case ce: ConsEnv => I.IS.env = ce.rest
  }

  /*
   * 15.2.2.1 new Object([value])
   */
  def newObj(): JSObject = new JSObject(I, I.IS.ObjectPrototype, "Object", true, propTable)
  def newObj(proto: JSObject): JSObject = new JSObject(I, proto, "Object", true, propTable)
  def newObj(prop: PropTable): JSObject = new JSObject(I, I.IS.ObjectPrototype, "Object", true, prop)

  /*
   * 10.6 Arguments Object
   */
  def newArgObj(fun: JSFunction, np: Int, o: JSObject, b: Boolean): JSObject = {
    val na = toInt(o.property.get("length").get)
    var prop = o.property
    if (na < np)
      for (i <- na until np) {
        o.property.put(i.toString, mkDataProp(IP.undefV, true, true, true))
      }
    o.property.put("length", mkDataProp(PVal(mkIRNumIR(na)), true, false, true))
    if (b) o.property.put("callee", mkDataProp(fun, true, false, true))
    o
  }
}
