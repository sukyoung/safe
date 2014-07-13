/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF}
import kr.ac.kaist.jsaf.nodes_util.EJSType
import kr.ac.kaist.jsaf.scala_src.nodes.SIRBool

class JSObjectConstructor(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Object", true, propTable, IP.undefFtn, EmptyEnv(), true) {
  def init(): Unit = {
    // 15.2.3 Properties of the Object Constructor
    property.put("length", I.IH.mkDataProp(PVal(IF.oneV), false, false, false))
    property.put("prototype", I.IH.mkDataProp(I.IS.ObjectPrototype, false, false, false))
    /*
    property.put("getPrototypeOf", IH.objProp(IS.ObjectGetPrototypeOf))
    */
    property.put("getOwnPropertyDescriptor", I.IH.objProp(I.IS.ObjectGetOwnPropertyDescriptor))
    /*
    property.put("getOwnPropertyNames", IH.objProp(IS.ObjectGetOwnPropertyNames))
    */
    property.put("create", I.IH.objProp(I.IS.ObjectCreate))
    property.put("defineProperty", I.IH.objProp(I.IS.ObjectDefineProperty))
    property.put("defineProperties", I.IH.objProp(I.IS.ObjectDefineProperties))
    property.put("seal", I.IH.objProp(I.IS.ObjectSeal))
    property.put("freeze", I.IH.objProp(I.IS.ObjectFreeze))
    property.put("preventExtensions", I.IH.objProp(I.IS.ObjectPreventExtensions))
    property.put("isSealed", I.IH.objProp(I.IS.ObjectIsSealed))
    property.put("isFrozen", I.IH.objProp(I.IS.ObjectIsFrozen))
    property.put("isExtensible", I.IH.objProp(I.IS.ObjectIsExtensible))
    /*
    property.put("keys", IH.objProp(IS.ObjectKeys))
    */
  }

  // 15.2.5 Properties of Object Instances
  // None.

  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: Array[Val] = I.IH.argsObjectToArray(argsObj, 3)
    method match {
      /*
      case IS.ObjectGetPrototypeOf.s => getPrototypeOf(args(0))
      */
      case I.IS.ObjectGetOwnPropertyDescriptor => getOwnPropertyDescriptor(args(0), args(1))
      /*
      case IS.ObjectGetOwnPropertyNames.s => getOwnPropertyNames(args(0))
      */
      case I.IS.ObjectCreate => create(args(0), args(1))
      case I.IS.ObjectDefineProperty => defineProperty(args(0), args(1), args(2))
      case I.IS.ObjectDefineProperties => defineProperties(args(0), args(1))
      case I.IS.ObjectSeal => seal(args(0))
      case I.IS.ObjectFreeze => freeze(args(0))
      case I.IS.ObjectPreventExtensions => preventExtensions(args(0))
      case I.IS.ObjectIsSealed => isSealed(args(0))
      case I.IS.ObjectIsFrozen => isFrozen(args(0))
      case I.IS.ObjectIsExtensible => isExtensible(args(0))
      /*
      case IS.ObjectKeys.s => keys(args(0))
      */
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.2 Object Objects
  ////////////////////////////////////////////////////////////////////////////////

  // 15.2.1.1 Object([value])
  override def _call(tb: Val, argsObj: JSObject): Unit = {
    val value: Val = argsObj._get("0")
    if(I.IH.isNull(value) || I.IH.isUndef(value)) {
      val obj: JSObject = _construct(argsObj)
      I.IS.comp.setReturn(obj)
    }
    else I.IH.valError2ReturnCompletion(I.IH.toObject(value))
  }

  // 15.2.2.1 new Object([value])
  override def _construct(argsObj: JSObject): JSObject = {
    val length: Int = I.IH.toNumber(argsObj._get("length")).getNum.toInt
    val value: Val = argsObj._get("0")
    if (length >= 1) {
      val typeOfValue = I.IH.typeOf(value)
      if(typeOfValue == EJSType.OBJECT) {
        //  i. If the value is a native ECMAScript object, do not create a new object but simply return value.
        return value.asInstanceOf[JSObject]
        // ii. If the value is a host object, then actions are taken and a result is returned in an implementation-dependent manner that may depend on the host object.
        // TODO:
      }
      else if(typeOfValue == EJSType.STRING || typeOfValue == EJSType.BOOLEAN || typeOfValue == EJSType.NUMBER) {
        return I.IH.toObject(value).asInstanceOf[JSObject]
      }
    }
    // Assert: The argument value was not supplied or its type was Null or Undefined.
    if (!(length <= 0 || (!I.IH.isNull(value) && !I.IH.isUndef(value)))) throw new InterpreterError("JSObject::__constructByNewOperator()", I.IS.span)
    new JSObject(I, I.IS.ObjectPrototype, "Object", true, propTable)
  }

  /*
  // 15.2.3.2 getPrototypeOf(O)
  // TODO:
  def getPrototypeOf(o: Val): Unit = throwErr(nyiError, IS.Span)
  */

  // 15.2.3.3 getOwnPropertyDescriptor(O, P)
  // TODO:
  def getOwnPropertyDescriptor(o: Val, p: Val): Unit = o match {
    case o: JSObject =>
      val name = I.IH.toString(p)
      val desc = o._getOwnProperty(name)
      if (desc == null)
        I.IS.comp.setReturn(IP.undefV)
      else
        I.IS.comp.setReturn(I.IH.fromPropertyDescriptor(desc))
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  /*
  // 15.2.3.4 getOwnPropertyNames(O)
  // TODO:
  def getOwnPropertyNames(o: Val): Unit = throwErr(nyiError, IS.Span)
  */

  // 15.2.3.5 create(O, [Properties])
  def create(o: Val, properties: Val): Unit = o match {
    case o: JSObject =>
      val prop: PropTable = new PropTable
      prop.put("length", I.IH.numProp(0))
      val args: JSObject = I.IH.newArgObj(I.IS.ObjectConstructor, 1, I.IH.newObj(prop), false)
      val obj: JSObject = _construct(args)
      obj.proto = o
      if (!I.IH.isUndef(properties)) defineProperties(obj, properties)
      I.IS.comp.setReturn(obj)
    case o if I.IH.isNull(o) =>
      val args: JSObject = I.IH.newArgObj(I.IS.ObjectConstructor, 1, I.IH.newObj, false)
      val obj: JSObject = _construct(args)
      obj.proto = IP.nullObj
      if (!I.IH.isUndef(properties)) defineProperties(obj, properties)
      I.IS.comp.setReturn(obj)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.6 defineProperty(O, P, Attributes)
  def defineProperty(o: Val, p: Val, attributes: Val): Unit = o match {
    case o: JSObject =>
      val name = I.IH.toString(p)
      val (desc, e) = I.IH.toPropertyDescriptor(attributes)
      desc match {
        case Some(desc) =>
          o._defineOwnProperty(name, desc, true)
          I.IS.comp.setReturn(o)
        case _ => e match {
          case Some(e) => I.IS.comp.setThrow(e, I.IS.span)
          case _ => throw new InterpreterError("ToPropertyDescriptor gave neither a property descriptor nor an error.", I.IS.span)
        }
      }
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.7 defineProperties(O, Properties)
  def defineProperties(o: Val, properties: Val): Unit = o match {
    case o: JSObject =>
      I.IH.toObject(properties) match {
        case props: JSObject =>
          val names: List[PName] = props.property.keys
          var descriptors: List[(PName, ObjectProp)] = Nil
          for (p <- names) {
            val descObj = props._get(p)
            val (desc, e) = I.IH.toPropertyDescriptor(descObj)
            desc match {
              case Some(desc) =>
                descriptors ++= List((p, desc))
              case _ => e match {
                case Some(e) =>
                  I.IS.comp.setThrow(e, I.IS.span)
                  return
                case _ =>
                  throw new InterpreterError("ToPropertyDescriptor gave neither a property descriptor nor an error.", I.IS.span)
              }
            }
          }
          for ((p, desc) <- descriptors) o._defineOwnProperty(p, desc, true)
          I.IS.comp.setReturn(o)
        case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
      }
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.8 seal(O)
  def seal(o: Val): Unit = o match {
    case o: JSObject =>
      // Optimized.
      val it = o.property.unorderedEntrySet.iterator
      while (it.hasNext) {
        val pair = it.next
        val (p, desc) = (pair.getKey, pair.getValue)
        desc.configurable match {
          case Some(true) => desc.configurable = Some(false)
          case _ =>
        }
      }
      o.extensible = false
      I.IS.comp.setReturn(o)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.9 freeze(O)
  def freeze(o: Val): Unit = o match {
    case o: JSObject =>
      // Optimized.
      val it = o.property.unorderedEntrySet.iterator
      while (it.hasNext) {
        val pair = it.next
        val (p, desc) = (pair.getKey, pair.getValue)
        if (I.IH.isDataDescriptor(desc)) desc.writable match {
          case Some(true) => desc.writable = Some(false)
          case _ =>
        }
        desc.configurable match {
          case Some(true) => desc.configurable = Some(false)
          case _ =>
        }
      }
      o.extensible = false
      I.IS.comp.setReturn(o)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.10 preventExtensions(O)
  def preventExtensions(o: Val): Unit = o match {
    case o: JSObject =>
      o.extensible = false
      I.IS.comp.setReturn(o)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.11 isSealed(O)
  def isSealed(o: Val): Unit = o match {
    case o: JSObject =>
      for (p <- o.property.keys) {
        val desc = o._getOwnProperty(p)
        desc.configurable match {
          case Some(true) =>
            I.IS.comp.setReturn(IP.falsePV)
            return
          case _ =>
        }
      }
      if (!o.extensible) I.IS.comp.setReturn(IP.truePV)
      else I.IS.comp.setReturn(IP.falsePV)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.12 isFrozen(O)
  def isFrozen(o: Val): Unit = o match {
    case o: JSObject =>
      for (p <- o.property.keys) {
        val desc = o._getOwnProperty(p)
        if (I.IH.isDataDescriptor(desc)) desc.writable match {
          case Some(true) =>
            I.IS.comp.setReturn(IP.falsePV)
            return
          case _ =>
        }
        desc.configurable match {
          case Some(true) =>
            I.IS.comp.setReturn(IP.falsePV)
            return
          case _ =>
        }
      }
      if (!o.extensible) I.IS.comp.setReturn(IP.truePV)
      else I.IS.comp.setReturn(IP.falsePV)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  // 15.2.3.13 isExtensible(O)
  def isExtensible(o: Val): Unit = o match {
    case o: JSObject =>
      if (o.extensible) I.IS.comp.setReturn(IP.truePV)
        else I.IS.comp.setReturn(IP.falsePV)
    case _ => I.IS.comp.setThrow(TypeError(), I.IS.span)
  }

  /*
  // 15.2.3.14 keys(O)
  // TODO:
  def keys(o: Val): Unit = throwErr(nyiError, IS.Span)
  */
}
