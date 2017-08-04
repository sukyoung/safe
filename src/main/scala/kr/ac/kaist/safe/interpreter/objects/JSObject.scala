/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.util.{ EJSType, EJSCompletionType => CT, NodeUtil => NU }

/*
 * 8.6.2 Object Internal Properties and Methods
 *   Table 8: Internal Properties Common to All Objects
 *     [[Prototype]], [[Class]], [[Extensible]],
 *     [[Get]], [[GetOwnProperty]], [[GetProperty]],
 *     [[Put]], [[CanPut]], [[HasProperty]], [[Delete]],
 *     [[DefaultValue]], [[DefineOwnProperty]]
 */
case class JSObject(
    val I: Interpreter,
    var proto: JSObject,
    var className: String,
    var extensible: Boolean,
    var property: PropTable
) extends Val {
  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  /*
   * 8.12 Algorithms for Object Internal Methods
   * 8.12.1 [[GetOwnProperty]](P)
   * Dom(o) = { x | x |-> _ \in o.@property }
   */
  def isInDomO(p: PName): Boolean = property.containsKey(p)

  def getProp(x: PName): Option[ObjectProp] = property.get(x)
  def putProp(x: PName, op: ObjectProp): Unit = property.put(x, op)

  def isDataProp(x: PName): Boolean = {
    val (op, _) = getProperty(x)
    op != null && I.IH.isDataDescriptor(op)
  }
  def isAccessorProp(x: PName): Boolean = {
    val (op, _) = getProperty(x)
    op != null && I.IH.isAccessorDescriptor(op)
  }

  def isWritable(x: PName): Boolean = {
    val (op, _) = getProperty(x)
    op != null && op.isWritable
  }
  def isEnumerable(x: PName): Boolean = {
    val (op, _) = getProperty(x)
    op != null && op.isEnumerable
  }
  def isConfigurable(x: PName): Boolean = {
    val (op, _) = getProperty(x)
    op != null && op.isConfigurable
  }

  def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = throw new InterpreterError("JSObject::__callBuiltinFunction()", I.IS.span)

  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types
  ////////////////////////////////////////////////////////////////////////////////

  /*
   * 8.12.1 [[GetOwnProperty]](P)
   * A String object has a more elaborate [[GetOwnProperty]] internal method (15.5.5.2)
   * If O doesn't have an own property with name P, return undefined.
   *   Returns null, if the undefined should be returned.
   */
  def getOwnProperty(x: PName): ObjectProp = {
    val v = property.get(x)
    // 1. If O doesn't have an own property with name P, return undefined.
    if (v.isEmpty) {
      null // null means undefined.
    } else {
      v.get.copy()
    }
  }

  /*
   * 8.12.2 [[GetProperty]](P)
   */
  def getProperty(x: PName): (ObjectProp, JSObject) =
    {
      var o: JSObject = this
      var prop: ObjectProp = null
      var cont: Boolean = true
      while (cont) {
        prop = o.getOwnProperty(x)
        // 2. If prop is not undefined, return prop.
        if (prop != null) cont = false
        // 4. If proto is null, return undefined.
        else if (o.proto == IP.nullObj) {
          o = IP.nullObj
          cont = false
        } else o = o.proto
      }
      (prop, o)
    }

  /*
   * 8.12.3 [[Get]](P)
   * H(l).[[Get]](P)
   */
  def get(x: PName): Val = {
    val (desc, _) = getProperty(x)
    // 2. if desc is undefined, return undefined.
    if (desc == null) IP.undefV
    else if (I.IH.isDataDescriptor(desc)) desc.value.get
    // 5. If getter is undefined, return undefined.
    else if (I.IH.isUndef(desc.get.get)) IP.undefV
    else desc.get.get match {
      case getter: JSFunction =>
        // TODO: IS.NoArgs
        // Argument
        val oldValue = I.IS.comp.value
        I.IS.comp.value = null
        val oldEnv = I.IS.env
        val oldTb = I.IS.tb
        I.IH.call(I.IS.info, this, I.IS.NoArgs, getter)
        I.IS.env = oldEnv
        I.IS.tb = oldTb
        if (I.IS.comp.value == null || (I.IS.comp.Type != CT.NORMAL && I.IS.comp.Type != CT.RETURN))
          throw new InterpreterError(className + "._get(" + x + ") error.", I.IS.span)
        val returnValue = I.IS.comp.value
        I.IS.comp.value = oldValue
        returnValue
      case _ => throw new InterpreterError(className + "._get(" + x + ") error.", I.IS.span)
    }
  }

  /*
   * 8.12.4 [[CanPut]](P)
   */
  def canPut(x: PName): Boolean = {
    val desc = getOwnProperty(x)
    // 2. If desc is not undefined, then
    if (desc != null) {
      if (I.IH.isAccessorDescriptor(desc)) {
        // i. If desc.[[Set]] is undefined, then return false.
        if (desc.set.isEmpty || I.IH.isUndef(desc.set.get)) false
        else true
      } else desc.isWritable
    } else if (proto == IP.nullObj) {
      extensible
    } else {
      val (inherited, _) = proto.getProperty(x)
      // 6. If inherited is undefined, return the value of the [[Extensible]] internal property of O.
      if (inherited == null) extensible
      else if (I.IH.isAccessorDescriptor(inherited)) {
        // a. If inherited.[[Set]] is undefined, then return false.
        if (inherited.set.isEmpty || I.IH.isUndef(inherited.set.get)) false
        else true
      } else {
        if (!extensible) false
        else inherited.isWritable
      }
    }
  }

  /*
   * 8.12.5 [[Put]](P,V,Throw)
   */
  def put(x: PName, v: Val, b: Boolean): ValError = {
    if (!canPut(x)) {
      // a. If Throw is true, then throw a TypeError exception.
      if (b) IP.typeError
      else v
    } else {
      val ownDesc = getOwnProperty(x)
      if (ownDesc != null && I.IH.isDataDescriptor(ownDesc)) {
        // a. Let valueDesc be the Property Descriptor {[[Value]]: V}.
        // b. Call the [[DefineOwnPropertry]] internal method of O passing P, valueDesc, and Throw as arguments.
        defineOwnProperty(x, new ObjectProp(Some(v), None, None, None, None, None), b) match {
          case err: JSError => err
          case _ => v
        }
      } else {
        // This may be either an own or inherited accessor property descriptor or an inherited data property descriptor.
        val (desc, _) = getProperty(x)
        if (desc != null && I.IH.isAccessorDescriptor(desc)) {
          // a. Let setter be desc.[[Set]] which cannot be undefined.
          // b. Call the [[Call]] internal method of setter providing O as the this value and providing V as the sole argument.
          desc.set.get match {
            case setter: JSFunction =>
              // Argument
              val args: JSArray = I.IS.ArrayConstructor.apply(IP.plusOneV, "Arguments")
              args.defineOwnProperty("0", I.IH.mkDataProp(v, true, true, true), false)

              val oldValue = I.IS.comp.value
              I.IS.comp.value = null
              val oldEnv = I.IS.env
              val oldTb = I.IS.tb
              I.IH.call(I.IS.info, this, args, setter)
              I.IS.env = oldEnv
              I.IS.tb = oldTb
              if (I.IS.comp.value == null || (I.IS.comp.Type != CT.NORMAL && I.IS.comp.Type != CT.RETURN))
                throw new InterpreterError(className + "._set(" + x + ") error.", I.IS.span)
              val returnValue = I.IS.comp.value
              I.IS.comp.value = oldValue
              returnValue
            case _ => throw new InterpreterError(className + "._set(" + x + ") error.", I.IS.span)
          }
        } else {
          val newDesc = I.IH.mkDataProp(v, true, true, true)
          defineOwnProperty(x, newDesc, b) match {
            case err: JSError => err
            case _ => v
          }
        }
      }
    }
  }

  /*
   * 8.12.6 [[HasProperty]](P)
   */
  def hasProperty(p: PName): Boolean = {
    val (desc, _) = getProperty(p)
    return desc != null
  }

  /*
   * 8.12.7 [[Delete]](P, Throw)
   */
  def delete(x: PName, b: Boolean): ValError = {
    val desc = getOwnProperty(x)
    // 2. If desc is undefined, then return true.
    if (desc == null) return IP.truePV
    else if (desc.isConfigurable) {
      property.remove(x)
      return IP.truePV
    } else if (b) IP.typeError
    else IP.falsePV
  }

  /*
   * 8.12.8 [[DefaultValue]](hint)
   */
  def defaultValue(hint: String): PVal = {
    def getString(): Option[PVal] = {
      val toString: Val = get("toString")
      if (I.IH.isCallable(toString)) {
        val args: JSObject = I.IS.ArrayConstructor.construct(Nil)
        val oldEnv = I.IS.env
        val oldTb = I.IS.tb
        toString.asInstanceOf[JSFunction].call(this, args)
        I.IS.env = oldEnv
        I.IS.tb = oldTb
        if (I.IS.comp.Type == CT.RETURN && I.IS.comp.value.isInstanceOf[PVal]) Some(I.IS.comp.value.asInstanceOf[PVal])
        else None
      } else None
    }
    def getValue(): Option[PVal] = {
      val valueOf = get("valueOf")
      if (I.IH.isCallable(valueOf)) {
        val args: JSObject = I.IS.ArrayConstructor.construct(Nil)
        val oldEnv = I.IS.env
        val oldTb = I.IS.tb
        valueOf.asInstanceOf[JSFunction].call(this, args)
        I.IS.env = oldEnv
        I.IS.tb = oldTb
        if (I.IS.comp.Type == CT.RETURN && I.IS.comp.value.isInstanceOf[PVal]) Some(I.IS.comp.value.asInstanceOf[PVal])
        else None
      } else None
    }

    /*
     * When the [[DefaultValue]] internal method of O is called with no hint,
     *   then it behaves as if the hint were Number, unless O is a Date object (see 15.9.6),
     *   in which case it behaves as if the hint were String.
     */
    val hintType = hint match {
      case "String" => EJSType.STRING
      case "Number" => EJSType.NUMBER
      case _ =>
        if (this.isInstanceOf[JSDate]) EJSType.STRING
        else EJSType.NUMBER
    }

    hintType match {
      case EJSType.STRING =>
        getString match {
          case Some(pv) => pv
          case None =>
            getValue match {
              case Some(pv) => pv
              case None => throw new DefaultValueError(IP.typeError)
            }
        }
      case EJSType.NUMBER =>
        getValue match {
          case Some(pv) => pv
          case None =>
            getString match {
              case Some(pv) => pv
              case None => throw new DefaultValueError(IP.typeError)
            }
        }
    }
  }

  /*
   * 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
   */
  def defineOwnProperty(x: Var, op: ObjectProp, b: Boolean): ValError = {
    // 1. Let current be the result of calling the [[GetOwnProperty]] internal method of O with property name P.
    val current = getOwnProperty(x)
    if (current == null) {
      // 2. Let extensible be the value of the [[Extensible]] internal property of O.
      // 3. If current is undefined and extensible is false, then Reject.
      if (!extensible) { if (b) return IP.typeError else return IP.falsePV }
      // 4. If current is undefined and extensible is true, then
      else {
        // a. If IsGenericDescriptor(Desc) or IsDataDescriptor(Desc) is true, then
        if (I.IH.isGenericDescriptor(op) || I.IH.isDataDescriptor(op)) {
          // i. Create an own data property named P of object O whose [[Value]], [[Writable]], [[Enumerable]] and [[Configurable]] attribute values are described by Desc.
          //    If the value of an attribute field of Desc is absent, the attribute of the newly created property is set to its default value.
          putProp(x, I.IH.mkDataProp(op.getValueOrDefault, op.getWritableOrDefault, op.getEnumerableOrDefault, op.getConfigurableOrDefault))
        } // b. Else, Desc must be an accessor Property Descriptor so,
        else {
          // i. Create an own accessor property named P of object O whose [[Get]], [[Set]], [[Enumerable]] and [[Configurable]] attribute values are described by Desc.
          //    If the value of an attribute field of Desc is absent, the attribute of the newly created property is set to its default value.
          putProp(x, I.IH.mkAccessorProp(op.getGetOrDefault, op.getSetOrDefault, op.getEnumerableOrDefault, op.getConfigurableOrDefault))
        }
      }
      // c. Return true.
      IP.truePV
    } else {
      // 5. Return true, if every field in Desc is absent.
      if (op.areAllAttributesAbsent) return IP.truePV
      // 6. Return true, if every field in Desc also occurs in current and the value of every field in Desc is the same value
      //    as the corresponding field in current when compared using the SameValue algorithm (9.12).
      if (op.equals(current)) return IP.truePV
      // 7. If the [[Configurable]] field of current is false then
      if (!current.isConfigurable) {
        // a. Reject, if the [[Configurable]] field of Desc is true.
        if (op.isConfigurable) if (b) return IP.typeError else return IP.falsePV
        // b. Reject, if the [[Enumerable]] field of Desc is present and the [[Enumerable]] fields of current and Desc are the Boolean negation of each other.
        if (op.enumerable.isDefined && current.enumerable.get != op.enumerable.get) if (b) return IP.typeError else return IP.falsePV
      }
      // 8. If IsGenericDescriptor(Desc) is true, then no further validation is required.
      if (I.IH.isGenericDescriptor(op)) {}
      // 9. Else, if IsDataDescriptor(current) and IsDataDescriptor(Desc) have different result, then
      else if (I.IH.isDataDescriptor(current) != I.IH.isDataDescriptor(op)) {
        // a. Reject, if the [[Configurable]] field of current is false.
        if (!current.isConfigurable) if (b) return IP.typeError else return IP.falsePV
        // b. If IsDataDescriptor(current) is true, then
        if (I.IH.isDataDescriptor(current)) {
          // i. Convert the property named P of object O from a data property to an accessor property.
          //    Preserve the existing values of the converted property's [[Configurable]] and
          //    [[Enumerable]] attributes and set the rest of the property's attributes to their default values.
          putProp(x, I.IH.mkAccessorProp(IP.undefV, IP.undefV, op.isEnumerable, op.isConfigurable))
        } // c. Else,
        else {
          // i. Convert the property named P of object O from a accessor property to an data property.
          //    Preserve the existing values of the converted property's [[Configurable]] and
          //    [[Enumerable]] attributes and set the rest of the property's attributes to their default values.
          putProp(x, I.IH.mkDataProp(IP.undefV, false, op.isEnumerable, op.isConfigurable))
        }
      } // 10. Else, if IsDataDescriptor(current) and IsDataDescriptor(Desc) are both true, then
      else if (I.IH.isDataDescriptor(current) && I.IH.isDataDescriptor(op)) {
        // a. If the [[Configurable]] field of current is false, then
        if (!current.isConfigurable) {
          // i. Reject, if the [[Writable]] field of current is false and the [[Writable]] field of Desc is true.
          if (!current.isWritable && op.isWritable) if (b) return IP.typeError else return IP.falsePV
          // ii. If the [[Writable]] field of current is false, then
          if (!current.isWritable) {
            // 1. Reject, if the [[Value]] field of Desc is present and SameValue(Desc.[[Value]], current.[[Value]]) is false.
            if (op.value.isDefined && !I.IH.sameValue(op.value.get, current.value.get)) if (b) return IP.typeError else return IP.falsePV
          }
        } // b. else, the [[Configurable]] field of current is true, so any change is acceptable.
        else {}
      } // 11. Else, IsAccessorDescriptor(current) and IsAccessorDescriptor(Desc) are both true so,
      else {
        // a. If the [[Configurable]] field of current is false, then
        if (!current.isConfigurable) {
          // i. Reject, if the [[Set]] field of Desc is present and SameValue(Desc.[[Set]], current.[[Set]]) is false.
          if (op.set.isDefined && !I.IH.sameValue(op.set.get, current.set.get)) if (b) return IP.typeError else return IP.falsePV
          // i. Reject, if the [[Get]] field of Desc is present and SameValue(Desc.[[Get]], current.[[Get]]) is false.
          if (op.get.isDefined && !I.IH.sameValue(op.get.get, current.get.get)) if (b) return IP.typeError else return IP.falsePV
        }
      }
      // 12. For each attribute field of Desc that is present, set the correspondingly named attribute of the property named P of object O to the value of the field.
      if (op.value.isDefined) current.value = op.value
      if (op.get.isDefined) current.get = op.get
      if (op.set.isDefined) current.set = op.set
      if (op.writable.isDefined) current.writable = op.writable
      if (op.enumerable.isDefined) current.enumerable = op.enumerable
      if (op.configurable.isDefined) current.configurable = op.configurable
      putProp(x, current)
      // 13. Return true.
      IP.truePV
    }
  }

  // 15.4
  def isSparse: Boolean = {
    val len = get("length")
    for (i <- 0L until I.IH.toUint32(len)) {
      val elem = getOwnProperty(i.toString)
      if (elem == null || I.IH.isUndef(elem)) return true
    }
    false
  }
}
