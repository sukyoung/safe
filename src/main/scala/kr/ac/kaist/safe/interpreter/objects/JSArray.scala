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

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSArray(
  I: Interpreter,
  proto: JSObject,
  className: String,
  extensible: Boolean,
  property: PropTable
)
    extends JSObject(I, proto, className, extensible, property) {
  /*
   * 15.4.5.1 [[DefineOwnProperty]](P, Desc, Throw) for Array
   * P = x; Desc = op; Throw = b
   */
  override def defineOwnProperty(P: Var, Desc: ObjectProp, Throw: Boolean): ValError = {
    def isArrayIndex(P: Var): Boolean = {
      // ToString(ToUint32(P)) is equal to P and ToUint32(P) is not equal to (2^32)-1
      val intP = I.IH.toUint32(PVal(I.IH.mkIRStrIR(P)))
      I.IH.toString(intP.toDouble).equals(P) && intP != 0xFFFFFFFFL
    }
    // 1. Let oldLenDesc be the result of calling the [[GetOwnProperty]] internal method of A passing "length" as the argument.
    //    The result will never be undefined or an accessor descriptor
    //    because Array objects are created with a length data property that cannot be deleted or reconfigured.
    val oldLenDesc = getOwnProperty("length")
    if (oldLenDesc == null) throw new InterpreterError("_defineOwnPropertyArray", I.IS.span)
    // 2. Let oldLen be oldLenDesc.[[Value]].
    var oldLen = oldLenDesc.value.get.asInstanceOf[PVal].v.value.asInstanceOf[EJSNumber].num.toLong
    // 3. If P is "length", then
    if (P.equals("length")) {
      // a. If the [[Value]] field of Desc is absent, then
      if (Desc.value.isEmpty) {
        // i. Return the result of calling the default [[DefineOwnProperty]] internal method (8.12.9)
        //    on A passing "length", Desc, and Throw as arguments.
        return super.defineOwnProperty("length", Desc, Throw)
      }
      // b. Let newLenDesc be a copy of Desc.
      val newLenDesc = Desc.copy()
      // c. Let newLen be ToUint32(Desc.[[Value]]).
      val newLen = I.IH.toUint32(Desc.value.get)
      // d. If newLen is not equal to ToNumber( Desc.[[Value]]), throw a RangeError exception.
      if (newLen != I.IH.toNumber(Desc.value.get).num) return IP.rangeError
      // e. Set newLenDesc.[[Value] to newLen.
      newLenDesc.value = Some(PVal(IRVal(I.IH.mkIRNum(newLen))))
      // f. If newLen >= oldLen, then
      if (newLen >= oldLen) {
        // i. Return the result of calling the default [[DefineOwnProperty]] internal method
        //    (8.12.9) on A passing "length", newLenDesc, and Throw as arguments.
        return super.defineOwnProperty("length", newLenDesc, Throw)
      }
      // g. Reject if oldLenDesc.[[Writable]] is false.
      if (oldLenDesc.writable.isDefined && !oldLenDesc.writable.get) { if (Throw) return IP.typeError else return IP.falsePV }
      // h. If newLenDesc.[[Writable]] is absent or has the value true, let newWritable be true.
      var newWritable: Boolean = true
      // i. Else,
      if (newLenDesc.writable.isDefined && !newLenDesc.writable.get) {
        // i. Need to defer setting the [[Writable]] attribute to false in case any elements cannot be deleted.
        // ii. Let newWritable be false.
        newWritable = false
        // iii. Set newLenDesc.[[Writable] to true.
        newLenDesc.writable = Some(true)
      }
      // j. Let succeeded be the result of calling the default [[DefineOwnProperty]] internal method (8.12.9)
      //    on A passing "length", newLenDesc, and Throw as arguments.
      super.defineOwnProperty("length", newLenDesc, Throw) match {
        // k. If succeeded is false, return false.
        case f @ PVal(IRVal(EJSBool(false))) => return f
        case f @ PVal(IRVal(EJSBool(true))) =>
          // l. While newLen < oldLen repeat,
          while (newLen < oldLen) {
            // i. Set oldLen to oldLen - 1.
            oldLen = oldLen - 1
            // ii. Let deleteSucceeded be the result of calling the [[Delete]] internal method of A passing
            //     ToString(oldLen) and false as arguments.
            delete(oldLen.toString, false) match {
              // iii. If deleteSucceeded is false, then
              case f @ PVal(IRVal(EJSBool(false))) =>
                // 1. Set newLenDesc.[[Value] to oldLen+1.
                newLenDesc.value = Some(PVal(IRVal(I.IH.mkIRNum(oldLen + 1))))
                // 2. If newWritable is false, set newLenDesc.[[Writable] to false.
                if (!newWritable) { newLenDesc.writable = Some(false) }
                // 3. Call the default [[DefineOwnProperty]] internal method (8.12.9) on A passing
                //    "length", newLenDesc, and false as arguments.
                super.defineOwnProperty("length", newLenDesc, false) match {
                  case err: JSError => return err
                  case _ => if (Throw) return IP.typeError else return IP.falsePV
                }
              case err: JSError => return err
              case _ =>
            }
          }
          // m. If newWritable is false, then
          if (!newWritable) {
            // i. Call the default [[DefineOwnProperty]] internal method (8.12.9) on A passing "length",
            //    Property Descriptor{[[Writable]]: false}, and false as arguments. This call will always
            //    return true.
            super.defineOwnProperty(
              "length",
              new ObjectProp(None, None, None, Some(false), None, None), false
            )
          }
          // n. Return true.
          return IP.truePV
        case err => return err
      }
    } // 4. Else if P is an array index (15.4), then
    else if (isArrayIndex(P)) {
      // a. Let index be ToUint32(P).
      val index = I.IH.toUint32(PVal(I.IH.mkIRStrIR(P)))
      // b. Reject if index >= oldLen and oldLenDesc.[[Writable]] is false.
      if (index >= oldLen && (!oldLenDesc.writable.get)) {
        if (Throw) {
          return IP.typeError
        } else {
          return IP.falsePV
        }
      }
      // c. Let succeeded be the result of calling the default [[DefineOwnProperty]] internal method (8.12.9) on A
      //    passing P, Desc, and false as arguments.
      super.defineOwnProperty(P, Desc, false) match {
        // d. Reject if succeeded is false.
        case f @ PVal(IRVal(EJSBool(false))) =>
          if (Throw) return IP.typeError else return IP.falsePV
        case f @ PVal(IRVal(EJSBool(true))) =>
          // e. If index >= oldLen
          if (index >= oldLen) {
            // i. Set oldLenDesc.[[Value]] to index + 1.
            oldLenDesc.value = Some(PVal(I.IH.mkIRNumIR(index + 1)))
            // ii. Call the default [[DefineOwnProperty]] internal method (8.12.9) on A passing "length",
            //     oldLenDesc, and false as arguments. This call will always return true.
            super.defineOwnProperty("length", oldLenDesc, false)
          }
          // f. Return true.
          return IP.truePV
        case err => return err
      }
    }
    // 5. Return the result of calling the default [[DefineOwnProperty]] internal method (8.12.9)
    //    on A passing P, Desc, and Throw as arguments.
    super.defineOwnProperty(P, Desc, Throw)
  }
}
