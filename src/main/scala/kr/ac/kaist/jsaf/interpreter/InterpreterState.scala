/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter

import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.jsaf.ShellParameters
import kr.ac.kaist.jsaf.compiler.Predefined
import kr.ac.kaist.jsaf.nodes_util.{Coverage, NodeUtil => NU}
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP}
import kr.ac.kaist.jsaf.interpreter.objects._
import kr.ac.kaist.jsaf.nodes.{IRRoot, IRSpanInfo, IRFunctional}
import kr.ac.kaist.jsaf.nodes_util.{Span, IRFactory => IF}
import kr.ac.kaist.jsaf.scala_src.useful.Sets
import kr.ac.kaist.jsaf.Shell
import java.util.HashMap

class InterpreterState(val I: Interpreter) {
  var env: Env = new EmptyEnv()
  var strict: Boolean = false
  var comp: Completion = new Completion()
  var lastComp: Completion = new Completion()
  var eval: Boolean = false
  var span: Span = IP.defSpan
  var coverage: Option[Coverage] = None
  var objectProps = Set[String]()

  def init() {
    // Set properties
    GlobalObject.init()
    ObjectConstructor.init()
    ObjectPrototype.init()
    objectProps = Sets.toSet(ObjectPrototype.property.map.keySet)
    FunctionConstructor.init()
    FunctionPrototype.init()
    ArrayConstructor.init()
    ArrayPrototype.init()
    StringConstructor.init()
    StringPrototype.init()
    BooleanConstructor.init()
    BooleanPrototype.init()
    NumberConstructor.init()
    NumberPrototype.init()
    MathObject.init()
    DateConstructor.init()
    DatePrototype.init()
    RegExpConstructor.init()
    RegExpPrototype.init()
    ErrorConstructor.init()
    ErrorPrototype.init()
    EvalErrorConstructor.init()
    EvalErrorPrototype.init()
    RangeErrorConstructor.init()
    RangeErrorPrototype.init()
    ReferenceErrorConstructor.init()
    ReferenceErrorPrototype.init()
    SyntaxErrorConstructor.init()
    SyntaxErrorPrototype.init()
    TypeErrorConstructor.init()
    TypeErrorPrototype.init()
    URIErrorConstructor.init()
    URIErrorPrototype.init()
    checkPredefined
  }

  def checkPredefined() {
    val notYetImplemented = Set("JSON", "decodeURIComponent", "encodeURIComponent", "encodeURI",
                                "<>Global<>global", "decodeURI", "Exception")
    val predefNames = if (Shell.pred != null)
                        Shell.pred.all.toSet.filterNot(notYetImplemented.contains(_))
                      else
                        (new Predefined(new ShellParameters())).all.toSet.filterNot(notYetImplemented.contains(_))
    val interpNames = Sets.toSet[String](ObjectPrototype.property.map.keySet).filterNot(objectProps.contains(_)) ++
                      Sets.toSet[String](GlobalObject.property.map.keySet)
    if (!interpNames.subsetOf(predefNames)) {
      System.out.println("The following names are defined in the initial heap of the interpreter\n"
                         +"but not in the list of predefined names:\n  ")
      interpNames.filterNot(predefNames.contains(_)).foreach((s:String) => System.out.print(s+" "))
      throw new InterpreterError("Predefined names mismatch.", span)
    }
    if (!predefNames.subsetOf(interpNames)) {
      System.out.println("The following names are defined in the list of predefined names\n"
                         +"but not in the initial heap of the interpreter:\n  ")
      predefNames.filterNot(interpNames.contains(_)).foreach((s:String) => System.out.print(s+" "))
      throw new InterpreterError("Predefined names mismatch.", span)
    }
  }

  def info(): IRSpanInfo = IF.makeSpanInfo(false, span)

  def dummyFtnObj(length: Int, builtin: JSObject): JSFunction = {
    val fv: IRFunctional = I.IH.dummyFtn(length)
    val prop = propTable
    prop.put("length", I.IH.numProp(fv.getArgs.size))
    new JSFunction13(I, FunctionPrototype, "Function", true, prop, fv, EmptyEnv(), false, builtin)
  }

  // ...
  val ftnArrPropTable = new PropTable
  ftnArrPropTable.put("length", I.IH.numProp(0))
  // An empty arguments object
  val NoArgs: JSArray = new JSArray(I, ObjectPrototype, "Arguments", true, ftnArrPropTable)

  // Placeholder
  // THE ORDER OF THE OBJECTS IS IMPORTANT!
  val ObjectPrototype: JSObjectPrototype = new JSObjectPrototype(I, IP.nullObj)
  val FunctionPrototype: JSFunctionPrototype = new JSFunctionPrototype(I, ObjectPrototype)
  val ArrayPrototype: JSArrayPrototype = new JSArrayPrototype(I, ObjectPrototype)
  val StringPrototype: JSStringPrototype = new JSStringPrototype(I, ObjectPrototype)
  val BooleanPrototype: JSBooleanPrototype = new JSBooleanPrototype(I, ObjectPrototype)
  val NumberPrototype: JSNumberPrototype = new JSNumberPrototype(I, ObjectPrototype)
  val DatePrototype: JSDatePrototype = new JSDatePrototype(I, ObjectPrototype)
  val RegExpPrototype: JSRegExpPrototype = new JSRegExpPrototype(I, ObjectPrototype)
  val ErrorPrototype: JSErrorPrototype = new JSErrorPrototype(I, ObjectPrototype)
  val EvalErrorPrototype: JSEvalErrorPrototype = new JSEvalErrorPrototype(I, ErrorPrototype)
  val RangeErrorPrototype: JSRangeErrorPrototype = new JSRangeErrorPrototype(I, ErrorPrototype)
  val ReferenceErrorPrototype: JSReferenceErrorPrototype = new JSReferenceErrorPrototype(I, ErrorPrototype)
  val SyntaxErrorPrototype: JSSyntaxErrorPrototype = new JSSyntaxErrorPrototype(I, ErrorPrototype)
  val TypeErrorPrototype: JSTypeErrorPrototype = new JSTypeErrorPrototype(I, ErrorPrototype)
  val URIErrorPrototype: JSURIErrorPrototype = new JSURIErrorPrototype(I, ErrorPrototype)
  //val BuiltInPrototype = List(ObjectPrototype, FunctionPrototype, ArrayPrototype, StringPrototype, BooleanPrototype, NumberPrototype, DatePrototype, RegExpPrototype)
  val ObjectConstructor: JSObjectConstructor = new JSObjectConstructor(I, FunctionPrototype)
  val FunctionConstructor: JSFunctionConstructor = new JSFunctionConstructor(I, FunctionPrototype)
  val ArrayConstructor: JSArrayConstructor = new JSArrayConstructor(I, FunctionPrototype)
  val StringConstructor: JSStringConstructor = new JSStringConstructor(I, FunctionPrototype)
  val BooleanConstructor: JSBooleanConstructor = new JSBooleanConstructor(I, FunctionPrototype)
  val NumberConstructor: JSNumberConstructor = new JSNumberConstructor(I, FunctionPrototype)
  val DateConstructor: JSDateConstructor = new JSDateConstructor(I, FunctionPrototype)
  val RegExpConstructor: JSRegExpConstructor = new JSRegExpConstructor(I, FunctionPrototype)
  val ErrorConstructor: JSErrorConstructor = new JSErrorConstructor(I, FunctionPrototype)
  val EvalErrorConstructor: JSEvalErrorConstructor = new JSEvalErrorConstructor(I, FunctionPrototype)
  val RangeErrorConstructor: JSRangeErrorConstructor = new JSRangeErrorConstructor(I, FunctionPrototype)
  val ReferenceErrorConstructor: JSReferenceErrorConstructor = new JSReferenceErrorConstructor(I, FunctionPrototype)
  val SyntaxErrorConstructor: JSSyntaxErrorConstructor = new JSSyntaxErrorConstructor(I, FunctionPrototype)
  val TypeErrorConstructor: JSTypeErrorConstructor = new JSTypeErrorConstructor(I, FunctionPrototype)
  val URIErrorConstructor: JSURIErrorConstructor = new JSURIErrorConstructor(I, FunctionPrototype)
  //val BuiltInConstructor = List(ObjectConstructor, FunctionConstructor, ArrayConstructor, StringConstructor, BooleanConstructor, NumberConstructor, DateConstructor, RegExpConstructor)
  val GlobalObject: JSGlobal = new JSGlobal(I, ObjectPrototype)
  val MathObject: JSMath = new JSMath(I, ObjectPrototype)
  //val BuiltInStandAlone = List(GlobalObject, MathObject)

  // 15.1 Global Object
  val GlobalEval: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalParseInt: JSFunction = dummyFtnObj(2, GlobalObject)
  val GlobalParseFloat: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalIsNaN: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalIsFinite: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalDecodeURI: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalDecodeURIComponent: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalEncodeURI: JSFunction = dummyFtnObj(1, GlobalObject)
  val GlobalEncodeURIComponent: JSFunction = dummyFtnObj(1, GlobalObject)
  //val GlobalFunction = List(GlobalEval, GlobalParseInt, GlobalParseFloat, GlobalIsNaN, GlobalIsFinite, GlobalDecodeURI, GlobalDecodeURIComponent, GlobalEncodeURI, GlobalEncodeURIComponent)

  // 15.2 Object
  val ObjectGetPrototypeOf: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectGetOwnPropertyDescriptor: JSFunction = dummyFtnObj(2, ObjectConstructor)
  val ObjectGetOwnPropertyNames: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectCreate: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectDefineProperty: JSFunction = dummyFtnObj(3, ObjectConstructor)
  val ObjectDefineProperties: JSFunction = dummyFtnObj(2, ObjectConstructor)
  val ObjectSeal: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectFreeze: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectPreventExtensions: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectIsSealed: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectIsFrozen: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectIsExtensible: JSFunction = dummyFtnObj(1, ObjectConstructor)
  val ObjectKeys: JSFunction = dummyFtnObj(1, ObjectConstructor)
  //val ObjectFunction = List(ObjectGetPrototypeOf, ObjectGetOwnPropertyDescriptor, ObjectGetOwnPropertyNames, ObjectCreate, ObjectDefineProperty, ObjectDefineProperties, ObjectSeal, ObjectFreeze, ObjectPreventExtensions, ObjectIsSealed, ObjectIsFrozen, ObjectIsExtensible, ObjectKeys)
  val ObjectPrototypeToString: JSFunction = dummyFtnObj(0, ObjectPrototype)
  val ObjectPrototypeToLocaleString: JSFunction = dummyFtnObj(0, ObjectPrototype)
  val ObjectPrototypeValueOf: JSFunction = dummyFtnObj(0, ObjectPrototype)
  val ObjectPrototypeHasOwnProperty: JSFunction = dummyFtnObj(1, ObjectPrototype)
  val ObjectPrototypeIsPrototypeOf: JSFunction = dummyFtnObj(1, ObjectPrototype)
  val ObjectPrototypePropertyIsEnumerable: JSFunction = dummyFtnObj(1, ObjectPrototype)
  //val ObjectPrototypeFunction = List(ObjectPrototypeToString, ObjectPrototypeToLocaleString, ObjectPrototypeValueOf, ObjectPrototypeHasOwnProperty, ObjectPrototypeIsPrototypeOf, ObjectPrototypePropertyIsEnumerable)

  // 15.3 Function
  val FunctionPrototypeToString: JSFunction = dummyFtnObj(0, FunctionPrototype)
  val FunctionPrototypeApply: JSFunction = dummyFtnObj(2, FunctionPrototype)
  val FunctionPrototypeCall: JSFunction = dummyFtnObj(1, FunctionPrototype)
  val FunctionPrototypeBind: JSFunction = dummyFtnObj(1, FunctionPrototype)
  //val FunctionPrototypeFunction = List(FunctionPrototypeToString, FunctionPrototypeApply, FunctionPrototypeCall, FunctionPrototypeBind)

  // 15.4 Array
  val ArrayIsArray: JSFunction = dummyFtnObj(0, ArrayConstructor)
  //val ArrayFunction = List(ArrayIsArray)
  val ArrayPrototypeToString: JSFunction = dummyFtnObj(0, ArrayPrototype)
  // 15.4.4.3
  val ArrayPrototypeConcat: JSFunction = dummyFtnObj(1, ArrayPrototype)
  val ArrayPrototypeJoin: JSFunction = dummyFtnObj(1, ArrayPrototype)
  val ArrayPrototypePop: JSFunction = dummyFtnObj(0, ArrayPrototype)
  val ArrayPrototypePush: JSFunction = dummyFtnObj(1, ArrayPrototype)
  val ArrayPrototypeReverse: JSFunction = dummyFtnObj(0, ArrayPrototype)
  // 15.4.4.9
  val ArrayPrototypeSlice: JSFunction = dummyFtnObj(2, ArrayPrototype)
  val ArrayPrototypeSort: JSFunction = dummyFtnObj(1, ArrayPrototype)
  val ArrayPrototypeSplice: JSFunction = dummyFtnObj(2, ArrayPrototype)
  // 15.4.4.13 - 15.4.4.22
  //val ArrayPrototypeFunction = List(ArrayPrototypeToString, ArrayPrototypeConcat, ArrayPrototypeJoin, ArrayPrototypePop, ArrayPrototypePush, ArrayPrototypeReverse, ArrayPrototypeSlice, ArrayPrototypeSort, ArrayPrototypeSplice)

  // 15.5 String
  val StringFromCharCode: JSFunction = dummyFtnObj(1, StringConstructor)
  //val StringFunction = List(StringFromCharCode)
  val StringPrototypeToString: JSFunction = dummyFtnObj(0, StringPrototype)
  val StringPrototypeValueOf: JSFunction = dummyFtnObj(0, StringPrototype)
  val StringPrototypeCharAt: JSFunction = dummyFtnObj(1, StringPrototype)
  val StringPrototypeCharCodeAt: JSFunction = dummyFtnObj(1, StringPrototype)
  val StringPrototypeConcat: JSFunction = dummyFtnObj(1, StringPrototype)
  // 15.5.4.7
  // 15.5.4.8
  // 15.5.4.9
  val StringPrototypeMatch: JSFunction = dummyFtnObj(1, StringPrototype)
  val StringPrototypeReplace: JSFunction = dummyFtnObj(2, StringPrototype)
  // 15.5.4.12
  val StringPrototypeSlice: JSFunction = dummyFtnObj(2, StringPrototype)
  val StringPrototypeSplit: JSFunction = dummyFtnObj(2, StringPrototype)
  val StringPrototypeSubstring: JSFunction = dummyFtnObj(2, StringPrototype)
  val StringPrototypeToLowerCase: JSFunction = dummyFtnObj(0, StringPrototype)
  // 15.5.4.17
  // 15.5.4.18
  // 15.5.4.19
  // 15.5.4.20
  //val StringPrototypeFunction = List(StringPrototypeToString, StringPrototypeValueOf, StringPrototypeCharAt, StringPrototypeCharCodeAt, StringPrototypeConcat, StringPrototypeMatch, StringPrototypeReplace, StringPrototypeSlice, StringPrototypeSplit, StringPrototypeSubstring, StringPrototypeToLowerCase)

  // 15.6 Boolean
  val BooleanPrototypeToString: JSFunction = dummyFtnObj(0, BooleanPrototype)
  val BooleanPrototypeValueOf: JSFunction = dummyFtnObj(0, BooleanPrototype)
  //val BooleanPrototypeFunction = List(BooleanPrototypeToString, BooleanPrototypeValueOf)

  // 15.7 Number
  val NumberPrototypeToString: JSFunction = dummyFtnObj(1, NumberPrototype)
  // 15.7.4.3
  val NumberPrototypeValueOf: JSFunction = dummyFtnObj(0, NumberPrototype)
  // 15.7.4.5
  // 15.7.4.6
  // 15.7.4.7
  //val NumberPrototypeFunction = List(NumberPrototypeToString, NumberPrototypeValueOf)

  // 15.8 Math
  val MathAbs: JSFunction = dummyFtnObj(1, MathObject)
  val MathAcos: JSFunction = dummyFtnObj(1, MathObject)
  val MathAsin: JSFunction = dummyFtnObj(1, MathObject)
  val MathAtan: JSFunction = dummyFtnObj(1, MathObject)
  val MathAtan2: JSFunction = dummyFtnObj(2, MathObject)
  val MathCeil: JSFunction = dummyFtnObj(1, MathObject)
  val MathCos: JSFunction = dummyFtnObj(1, MathObject)
  val MathExp: JSFunction = dummyFtnObj(1, MathObject)
  val MathFloor: JSFunction = dummyFtnObj(1, MathObject)
  val MathLog: JSFunction = dummyFtnObj(1, MathObject)
  val MathMax: JSFunction = dummyFtnObj(2, MathObject)
  val MathMin: JSFunction = dummyFtnObj(2, MathObject)
  val MathPow: JSFunction = dummyFtnObj(2, MathObject)
  val MathRandom: JSFunction = dummyFtnObj(0, MathObject)
  val MathRound: JSFunction = dummyFtnObj(1, MathObject)
  val MathSin: JSFunction = dummyFtnObj(1, MathObject)
  val MathSqrt: JSFunction = dummyFtnObj(1, MathObject)
  val MathTan: JSFunction = dummyFtnObj(1, MathObject)
  //val MathFunction = List(MathAbs, MathAcos, MathAsin, MathAtan, MathAtan2, MathCeil, MathCos, MathExp, MathFloor, MathLog, MathMax, MathMin, MathPow, MathRandom, MathRound, MathSin, MathSqrt, MathTan)

  // 15.9 Date
  val DateParse: JSFunction = dummyFtnObj(1, DateConstructor)
  val DateUTC: JSFunction = dummyFtnObj(7, DateConstructor)
  val DateNow: JSFunction = dummyFtnObj(0, DateConstructor)
  //val DateFunction = List(DateParse, DateUTC, DateNow)
  val DatePrototypeToString: JSFunction = dummyFtnObj(0, DatePrototype)
  // 15.9.5.3 - 15.9.5.7
  val DatePrototypeValueOf: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetTime: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetFullYear: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCFullYear: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetMonth: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCMonth: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetDate: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCDate: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetDay: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCDay: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetHours: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCHours: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetMinutes: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCMinutes: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetSeconds: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCSeconds: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetMilliseconds: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetUTCMilliseconds: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeGetTimezoneOffset: JSFunction = dummyFtnObj(0, DatePrototype)
  val DatePrototypeSetTime: JSFunction = dummyFtnObj(1, DatePrototype)
  // 15.9.5.28 - 15.9.5.42
  val DatePrototypeToISOString: JSFunction = dummyFtnObj(0, DatePrototype)
  // 15.9.5.44
  //val DatePrototypeFunction = List(DatePrototypeToString, DatePrototypeValueOf, DatePrototypeGetTime, DatePrototypeGetFullYear, DatePrototypeGetUTCFullYear, DatePrototypeGetMonth, DatePrototypeGetUTCMonth, DatePrototypeGetDate, DatePrototypeGetUTCDate, DatePrototypeGetDay, DatePrototypeGetUTCDay, DatePrototypeGetHours, DatePrototypeGetUTCHours, DatePrototypeGetMinutes, DatePrototypeGetUTCMinutes, DatePrototypeGetSeconds, DatePrototypeGetUTCSeconds, DatePrototypeGetMilliseconds, DatePrototypeGetUTCMilliseconds, DatePrototypeSetTime)

  // 15.10 RegExp
  val RegExpPrototypeExec: JSFunction = dummyFtnObj(1, RegExpPrototype)
  val RegExpPrototypeTest: JSFunction = dummyFtnObj(1, RegExpPrototype)
  val RegExpPrototypeToString: JSFunction = dummyFtnObj(0, RegExpPrototype)
  //val RegExpPrototypeFunction = List(RegExpPrototypeExec, RegExpPrototypeTest, RegExpPrototypeToString)

  // 15.11 Error
  val ErrorPrototypeToString: JSFunction = dummyFtnObj(0, ErrorPrototype)

  // 15.12 JSON
  /*
  val JSON: JSFunction = new JSFunction13(IP.lJSON, IP.lNull, "JSON", true, propTable, IP.undefFtn, EmptyEnv())
  */

  //val BuiltInFunction = BuiltInConstructor ++ GlobalFunction ++ ObjectFunction ++ ObjectPrototypeFunction ++ FunctionPrototypeFunction ++ ArrayFunction ++ ArrayPrototypeFunction ++ StringFunction ++ StringPrototypeFunction ++ BooleanPrototypeFunction ++ NumberPrototypeFunction ++ MathFunction ++ DateFunction ++ DatePrototypeFunction ++ RegExpPrototypeFunction

  val globalObjEnvRec: ObjEnvRec = new ObjEnvRec(GlobalObject)
  val nullObjEnvRec: ObjEnvRec = new ObjEnvRec(IP.nullObj)

  var tb: Val = GlobalObject
}
