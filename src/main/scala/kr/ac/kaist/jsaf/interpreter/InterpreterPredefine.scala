/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF, _}
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, _}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterHelper => IH, _}
import kr.ac.kaist.jsaf.interpreter.objects.JSObject

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
  val sourceLoc = new SourceLocRats(NU.freshFile("interpreter"), 0, 0, 0)
  val defSpan = new Span(sourceLoc, sourceLoc)
  val defInfo = IF.makeSpanInfo(false, defSpan)
  val defId = IF.dummyIRId("interpreter")

  // Constant values
  val undefined = IF.makeUndef(IF.dummyAst)
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
  val nullV = PVal(IF.makeNull(IF.dummyAst))
  val plusZeroV = PVal(plusZero)
  val minusZeroV = PVal(minusZero)
  val plusOneV = PVal(plusOne)
  val minusOneV = PVal(minusOne)
  val thisName = NU.freshGlobalName("this")
  val argumentsName = NU.freshGlobalName("arguments")
  val thisTId = IF.makeTId(IF.dummySpan("this"), thisName)
  val argumentsTId = IF.makeTId(IF.dummySpan("arguments"), argumentsName)
  //val resUndef = Normal(Some(undefV))
  val undefFtn =
    IF.makeFunctional(false, IF.dummyAst, defId,
                      toJavaList(List(thisTId, argumentsTId).asInstanceOf[List[IRId]]),
                      IF.makeReturn(false, IF.dummyAst, defSpan, toJavaOption(Some(undefVar))))
  val undefDP = new ObjectProp(Some(undefV), None, None, Some(false), Some(false), Some(false)) // IH.mkDataProp()
  val pvpn= varPrefix+"PrimitiveValue"
  val nullObj: JSObject = new JSObject(null, null, "Null", false, new PropTable)
}
