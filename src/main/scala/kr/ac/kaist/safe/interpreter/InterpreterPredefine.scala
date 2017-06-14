/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.interpreter

import kr.ac.kaist.safe.interpreter.{InterpreterHelper => IH}
import kr.ac.kaist.safe.interpreter.objects.JSObject
import kr.ac.kaist.safe.util.{NodeUtil => NU, SourceLoc, Span}
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{IRFactory => IF}

object InterpreterPredefine {
  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  // Prefix
  val varPrefix = "@"

  // Errors
  val error = Error()
  val evalError = EvalError()
  val rangeError = RangeError()
  def referenceError(x: Var) = ReferenceError(x)
  val syntaxError = SyntaxError()
  val typeError = TypeError()
  val uriError = URIError()
  val nyiError = NYIError()

  // Dummy source locations
  // Use only when we cannot get any meaningful source locations
  val sourceLoc = new SourceLoc(0, 0, 0)
  val defSpan = new Span(NU.freshFile("interpreter"), sourceLoc, sourceLoc)
  val defInfo = IF.makeSpanInfo(false, defSpan)
  val defId = IF.dummyIRId("interpreter")

  // Constant values
  val undefined = IF.makeUndef(IF.dummyAST)
  val NaN = IF.makeNumber(false, "NaN", Double.NaN)
  val plusZero = IF.makeNumber(false, "+0.0", +0.0)
  val minusZero = IF.makeNumber(false, "-0.0", -0.0)
  val plusOne = IF.makeNumber(false, "+1.0", +1.0)
  val minusOne = IF.makeNumber(false, "-1.0", -1.0)
  val plusInfinity = IF.makeNumber(false, "Infinity", Double.PositiveInfinity)
  val minusInfinity = IF.makeNumber(false, "-Infinity", Double.NegativeInfinity)
  val undefVar = IF.makeTId(IF.dummySpan("undefVar"), NU.freshGlobalName("undefVar"))
  val falseV = IF.falseV
  val trueV = IF.trueV
  val falsePV = PVal(falseV)
  val truePV = PVal(trueV)
  val undefV = PVal(undefined)
  val nullV = PVal(IF.makeNull(IF.dummyAST))
  val plusZeroV = PVal(IRVal(plusZero))
  val minusZeroV = PVal(IRVal(minusZero))
  val plusOneV = PVal(IRVal(plusOne))
  val minusOneV = PVal(IRVal(minusOne))
  val thisName = NU.freshGlobalName("this")
  val argumentsName = NU.freshGlobalName("arguments")
  val thisTId = IF.makeTId(IF.dummySpan("this"), thisName)
  val argumentsTId = IF.makeTId(IF.dummySpan("arguments"), argumentsName)
  //val resUndef = Normal(Some(undefV))
  val undefFtn =
    IF.makeFunctional(false, IF.dummyAST, defId,
                      List(thisTId, argumentsTId).asInstanceOf[List[IRId]],
                      IF.makeReturn(false, IF.dummyAST, Some(undefVar)))
  val undefDP = new ObjectProp(Some(undefV), None, None, Some(false), Some(false), Some(false)) // IH.mkDataProp()
  val pvpn= varPrefix+"PrimitiveValue"
  val nullObj: JSObject = new JSObject(null, null, "Null", false, new PropTable)
}
