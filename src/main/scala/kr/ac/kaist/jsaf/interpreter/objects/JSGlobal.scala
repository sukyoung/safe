/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import edu.rice.cs.plt.tuple.Option;

import kr.ac.kaist.jsaf.compiler.{Parser, Hoister, Disambiguator, WithRewriter, Translator}
import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import kr.ac.kaist.jsaf.nodes.{Program, IRRoot, IRString}
import kr.ac.kaist.jsaf.nodes_util.{ IRFactory => IF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.nodes_util.{ EJSCompletionType => CT }
import kr.ac.kaist.jsaf.nodes_util.Coverage;
import kr.ac.kaist.jsaf.useful.Useful
import kr.ac.kaist.jsaf.scala_src.useful.Lists.toList

class JSGlobal(_I: Interpreter, _proto: JSObject)
  extends JSObject(_I, _proto, "Global", true, propTable) {
  def init(): Unit = {
    // Internal identifiers created by Translator
    property.put(NU.varTrue, I.IH.mkDataProp(IP.truePV, false, false, false))
    property.put(NU.varOne, I.IH.mkDataProp(PVal(IF.oneV), false, false, false))

    // 15.1.1 Value Properties of the Global Object
    property.put("NaN", I.IH.mkDataProp(PVal(IP.NaN), false, false, false))
    property.put("Infinity", I.IH.mkDataProp(PVal(IP.plusInfinity), false, false, false))
    property.put("undefined", I.IH.mkDataProp(PVal(IP.undefined), false, false, false))

    // 15.1.2 Function Properties of the Global Object
    property.put("eval", I.IH.objProp(I.IS.GlobalEval))
    property.put("parseInt", I.IH.objProp(I.IS.GlobalParseInt))
    property.put("parseFloat", I.IH.objProp(I.IS.GlobalParseFloat))
    property.put("isNaN", I.IH.objProp(I.IS.GlobalIsNaN))
    property.put("isFinite", I.IH.objProp(I.IS.GlobalIsFinite))

    // 15.1.3 URI Handling Function properties
    /*
    property.put("decodeURI", I.IH.objProp(I.IS.GlobalDecodeURI))
    property.put("decodeURIComponent", I.IH.objProp(I.IS.GlobalDecodeURIComponent))
    property.put("encodeURI", I.IH.objProp(I.IS.GlobalEncodeURI))
    property.put("encodeURIComponent", I.IH.objProp(I.IS.GlobalEncodeURIComponent))
    */

    // 15.1.4 Constructor Properties of the Global Object
    property.put("Object", I.IH.objProp(I.IS.ObjectConstructor))
    property.put("Function", I.IH.objProp(I.IS.FunctionConstructor))
    property.put("Array", I.IH.objProp(I.IS.ArrayConstructor))
    property.put("String", I.IH.objProp(I.IS.StringConstructor))
    property.put("Boolean", I.IH.objProp(I.IS.BooleanConstructor))
    property.put("Number", I.IH.objProp(I.IS.NumberConstructor))
    property.put("Math", I.IH.objProp(I.IS.MathObject))
    property.put("Date", I.IH.objProp(I.IS.DateConstructor))
    property.put("RegExp", I.IH.objProp(I.IS.RegExpConstructor))
    property.put("Error", I.IH.objProp(I.IS.ErrorConstructor))
    property.put("EvalError", I.IH.objProp(I.IS.EvalErrorConstructor))
    property.put("RangeError", I.IH.objProp(I.IS.RangeErrorConstructor))
    property.put("ReferenceError", I.IH.objProp(I.IS.ReferenceErrorConstructor))
    property.put("SyntaxError", I.IH.objProp(I.IS.SyntaxErrorConstructor))
    property.put("TypeError", I.IH.objProp(I.IS.TypeErrorConstructor))
    property.put("URIError", I.IH.objProp(I.IS.URIErrorConstructor))
  }

  val declEnvRec: DeclEnvRec = new DeclEnvRec(new Store)
  declEnvRec.s.put(NU.freshGlobalName("global"), new StoreValue(this, true, true, true))


  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: Array[Val] = I.IH.argsObjectToArray(argsObj, 2)
    method match {
      case I.IS.GlobalEval => eval(args(0), false)
      case I.IS.GlobalParseInt => parseInt(args(0), args(1))
      case I.IS.GlobalParseFloat => parseFloat(args(0))
      case I.IS.GlobalIsNaN => isNaN(args(0))
      case I.IS.GlobalIsFinite => isFinite(args(0))
      /*
      case I.IS.GlobalDecodeURI => decodeURI(IS, args(0))
      case I.IS.GlobalDecodeURIComponent => decodeURIComponent(IS, args(0))
      case I.IS.GlobalEncodeURI => encodeURI(IS, args(0))
      case I.IS.GlobalEncodeURIComponent => encodeURIComponent(IS, args(0))
      */
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.1.2 Function Properties of the Global Object
  ////////////////////////////////////////////////////////////////////////////////

  // 15.1.2.1 eval(x)
  def eval(x: Val, directCall: Boolean): Unit = {
    x match {
      case PVal(s: IRString) =>
        val prog: IRRoot = try {
          var program: Program = Parser.parsePgm(s.getStr, "eval")
          val hoister = new Hoister(program)
          program = hoister.doit.asInstanceOf[Program]
          val disambiguator = new Disambiguator(program, false)
          program = disambiguator.doit.asInstanceOf[Program]
          val withRewriter = new WithRewriter(program, false)
          program = withRewriter.doit.asInstanceOf[Program]
          val translator = new Translator(program, Option.none[Coverage])
          translator.doit.asInstanceOf[IRRoot]
        } catch {
          case _ =>
            return I.IS.comp.setThrow(IP.syntaxError, I.IS.span)
        }
        /*
         * 10.4.2 Entering Eval Code
         * TODO: strict mode
         */
        val oldEnv = I.IS.env
        val oldTb = I.IS.tb
        if (!directCall) {
          I.IS.env = EmptyEnv()
          I.IS.tb = I.IS.GlobalObject
        }
        /*
         * 10.5 Declaration Binding Instantiation
         */
        val oldEval = I.IS.eval
        I.IS.eval = true
        val oldCompletionValue = I.IS.comp.value
        I.IS.comp.value = null
        I.walkIRs(toList(prog.getFds)++toList(prog.getVds)++toList(prog.getIrs))
        I.IS.env = oldEnv
        I.IS.tb = oldTb
        I.IS.eval = oldEval
        if(I.IS.comp.Type == CT.NORMAL) {
          if(I.IS.comp.value == null) I.IS.comp.setReturn(IP.undefV)
          else I.IS.comp.setReturn()
        }
      case _ => I.IS.comp.setReturn(x)
    }
  }
  
  // 15.1.2.2 parseInt(string, radix)
  def parseInt(string: Val, radix: Val): Unit = {
    // 1. Let inputString be ToString(string).
    val inputString: String = I.IH.toString(string)
    // 2. Let S be a newly created substring of input String consisting of the first character that
    //    is not a StrWhiteSpaceChar and all characters following that character.
    //    (In other words, remove leading white space.)
    //    If inputString does not contain any such characters, let S be the empty string.
    var S: String = I.IH.leftTrim(inputString).toLowerCase
    // 3. Let sign be 1.
    var sign = 1
    // 4. If S is not empty and the first character of S is a minus sign -, let sign be -1.
    if(!S.isEmpty && S.charAt(0) == '-') sign = -1
    // 5. If S is not empty and the first character of S is a plus sign + or a minus sign -,
    //    then remove the first character form S.
    if(!S.isEmpty && (S.charAt(0) == '+' || S.charAt(0) == '-')) S = S.substring(1)
    // 6. Let R = ToInt32(radix).
    var R = I.IH.toInt32(radix)
    // 7. Let stripPrefix be true.
    var stripPrefix = true
    // 8. If R != 0, then
    if(R != 0) {
      // a. If R < 2 or R > 36, then return NaN.
      if(R < 2 || R > 36) {I.IS.comp.setReturn(PVal(IP.NaN)); return}
      // b. If R != 16, let stripPrefix be false.
      stripPrefix = false
    }
    // 9. Else, R = 0
    else if(R == 0) R = 10
    // 10. If stripPrefix is true, then
    if(stripPrefix) {
      // a. If the length of S is at least 2 and the first two characters of S are either "0x" or "0X",
      //    then remove the first two characters from S and let R = 16.
      if(S.length > 2 && S.substring(0, 2).equals("0x")) {
        S = S.substring(2)
        R = 16
      }
    }
    // 11. If S contains any character that is not a radix-R digit,
    //     then let Z be the substring of S consisting of all characters before the first such character;
    //     otherwise, let Z be S.
    // 12. If Z is empty, return NaN.
    val i = S.indexWhere(c => {
      (R <= 10 && !(c >= '0' && c <= '0' + R - 1)) || // 2 ~ 10
      (R > 10 && !((c >= '0' && c <= '9') || (c >= 'a' && c <= 'a' + R - 1))) // 11 ~ 36
    })
    val Z = {if(i == -1) S else S.substring(0, i)}
    if(Z.isEmpty) {I.IS.comp.setReturn(PVal(IP.NaN)); return}
    // 13. Let mathInt be the mathematical integer value that is represented by Z in radix-R notation,
    //     using the letters A-Z and a-z for digits with values 10 through 35. (However, ...)
    var mathInt: Double = 0.0
    for(c <- Z) {
      if(c >= '0' && c <= '9') mathInt = mathInt * R + (c - '0')
      else if(c >= 'a' && c <= 'z') mathInt = mathInt * R + (c - 'a' + 10)
    }
    // 14. Let number be the Number value for mathInt.
    val number = mathInt
    // 15. Return sign * number.
    I.IS.comp.setReturn(PVal(I.IH.mkIRNum(sign * number)))
  }

  // 15.1.2.3 parseFloat(string)
  def parseFloat(string: Val): Unit = {
    // 1. Let inputString be ToString(string).
    val inputString: String = I.IH.toString(string)
    // 2. Let trimmedString be a substring of inputString consisting of the leftmost character that
    //    is not a StrWhiteSpaceChar and all characters to the right of that character.
    //    (In other words, remove leading white space.)
    //    If inputString does not contain any such characters, let trimmedString be the empty string.
    var trimmedString: String = I.IH.leftTrim(inputString)
    // 3. If neither trimmedString nor any prefix of trimmedString satisfies the syntax of a StrDecimalLiteral (see 9.3.1), return NaN.
    // 4. Let numberString be the longest prefix of trimmedString, which might be trimmedString itself, that satisfies the syntax of a StrDecimalLiteral.
    // 5. Return the Number value for the MV of numberString.

    // TODO: String.toDouble is used temporarily T_T
    try {
      I.IS.comp.setReturn(PVal(I.IH.mkIRNum(trimmedString.toDouble)))
    }
    catch {
      case _ => I.IS.comp.setReturn(PVal(IP.NaN))
    }
  }

  // 15.1.2.4 isNaN(number)
  def isNaN(number: Val): Unit = {
    // 1. If ToNumber(number) is NaN, return true.
    // 2. Otherwise, return false.
    I.IS.comp.setReturn(PVal(I.IH.getIRBool(I.IH.isNaN(I.IH.toNumber(number)))))
  }

  // 15.1.2.5 isFinite(number)
  def isFinite(number: Val): Unit = {
    // 1. If ToNumber(number) is NaN, return true.
    // 2. Otherwise, return false.
    val n = I.IH.toNumber(number)
    I.IS.comp.setReturn(PVal(I.IH.getIRBool(I.IH.isNaN(n) || !I.IH.isInfinite(n))))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.1.3 URI Handling Function Properties
  ////////////////////////////////////////////////////////////////////////////////
  
  /*
  // 15.1.3.1 decodeURI(encodedURI)
  // TODO:
  def decodeURI(IS: InterpreterState, encodedURI: Val): Unit = throwErr(nyiError)

  // 15.1.3.2 decodeURIComponent(encodedURIComponent)
  // TODO:
  def decodeURIComponent(IS: InterpreterState, encodedURIComponent: Val): Unit = throwErr(nyiError)

  // 15.1.3.3 encodeURI(uri)
  // TODO:
  def encodeURI(IS: InterpreterState, uri: Val): Unit = throwErr(nyiError)

  // 15.1.3.4 encodeURIComponent(uriComponent)
  // TODO:
  def encodeURIComponent(IS: InterpreterState, uriComponent: Val): Unit = throwErr(nyiError)
  */
}
