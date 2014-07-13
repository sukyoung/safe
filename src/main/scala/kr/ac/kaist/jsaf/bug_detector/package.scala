/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.nodes_util.SourceLoc
import kr.ac.kaist.jsaf.nodes_util.EJSType

package object bug_detector {
  /************************* TYPE DEFINITION ***************************
  * BugKind   : Identifier for each bug kind
  * BugId     : Identifier for each detected bug
  * BugType   : Type of Bug (TypeError, ReferenceError, Warning)
  * BugEntry  : (BugId, FileName, Begin, End, BugType, BugMessage)
  * BugList   : List of reported bugs held by BugEntry
  * BugMap    : Map each bug to its BugType, message and argNum
  * FidSet    : Set of FunctionIds
  * SoundnessLevel : Level of Soundness
  **********************************************************************/
  type BugKind        = Int
  type BugId          = Int
  type BugType        = Int
  type BugEntry       = (BugId, String, SourceLoc, SourceLoc, Int, String)
  type BugList        = List[BugEntry]
  type BugMap         = MMap[BugKind, (BugType, String, Int)]
  type TraceEntry     = (CFGInst, CallContext)
  type TraceMap       = Map[BugId, TraceEntry]
  type FidSet         = Set[FunctionId]
  type SoundnessLevel = Int

  /************ TYPE DEFINITION (UnusedVarProp) **************
  * RWEntry     : (Boolean, Boolean, Loc, String, Info)
  *  L Boolean  : true => Write    | false => Read
  *  L Boolean  : true => Variable | false => Property
  *  L Loc      : Location
  *  L String   : Variable or Property name
  *  L Info     : Info
  * WEntry      : (Boolean, Loc, String, Info)
  * RWMAP       : Map[Node, List[RWEntry]]
  * WMap        : Map[Node, List[WEntry]]
  ************************************************************/
  type RWEntry  = (Boolean, Boolean, Loc, String, Span)
  type WEntry   = (Boolean, Loc, String, Span)
  type RWMap    = Map[Node, List[RWEntry]] 
  type WMap     = Map[Node, Set[WEntry]] 

  /* BugIdCounter, BugKindCounter, BugType Constant, Definite Flag */
  var BugIdCounter    = -1
  var BugKindCounter  = -1
  val RangeError      = 1
  val ReferenceError  = 2
  val SyntaxError     = 3
  val TypeError       = 4
  val URIError        = 5
  val Warning         = 6
  val TSError         = 7
  val TSWarning       = 8
  val WebAPIError     = 9
  val WebAPIWarning   = 10
  val TimeLimitError  = 11
  val GuideWarning    = 12
  val definite_only   = true

  /* Constants */
  val write         = true
  val read          = false
  val variable      = true
  val property      = false
  val CALL_NORMAL   = true
  val CALL_CONST    = false

  /* Options */
  val SOUNDNESS_LEVEL_LOW     = 0 
  val SOUNDNESS_LEVEL_NORMAL  = 1
  val SOUNDNESS_LEVEL_HIGH    = 2

  /* Stores all message formats */
  val bugTable: BugMap = MHashMap()

  def addBugMsgFormat(BugKind: BugKind, kind: BugType, msg: String, argNum: Int): Int = {bugTable(BugKind) = (kind, msg, argNum); BugKind}

  /* BugId generator */
  def newBugId(): BugId = {BugIdCounter += 1; BugIdCounter}

  /* BugKind generator */
  def newBugKind(): BugKind = {
    //if (BugKindCounter >= MAX_BUG_COUNT) throw new RuntimeException("BugKindCounter is bigger then MAX_BUG_COUNT.")
    BugKindCounter += 1; BugKindCounter
  }

  /* BugKind : 0 ~ 9 */
  val ArrayConstLength      :BugKind = addBugMsgFormat(newBugKind, RangeError, "Argument '%s' of 'Array.constructor' should be an unsigned integer.", 1)
  val AbsentReadProperty    :BugKind = addBugMsgFormat(newBugKind, Warning, "Reading absent property '%s' of object %s%s", 3)
  val AbsentReadVariable    :BugKind = addBugMsgFormat(newBugKind, ReferenceError, "Reading absent variable '%s'.", 1)
  val BinaryOpSecondType    :BugKind = addBugMsgFormat(newBugKind, TypeError, "Right-hand side operand '%s' of '%s' operator is %s%s", 4)
  val BuiltinCallable       :BugKind = addBugMsgFormat(newBugKind, TypeError, "'%s' called by the builtin function '%s' is not a function.", 2)
  val BuiltinRange          :BugKind = addBugMsgFormat(newBugKind, RangeError, "Argument '%s' of '%s' should be in [%s, %s].", 4)
  val BuiltinRegExpConst    :BugKind = addBugMsgFormat(newBugKind, TypeError, "Argument 'Pattern' is a 'RegExp' object and argument 'flags' is not undefined.", 0)
  val BuiltinThisType       :BugKind = addBugMsgFormat(newBugKind, TypeError, "'this' referred by the builtin function '%s' is not an object.", 1)
  val BuiltinWrongArgType   :BugKind = addBugMsgFormat(newBugKind, Warning, "%s argument of '%s' should be %s.", 3)
  val CallConstFunc         :BugKind = addBugMsgFormat(newBugKind, Warning, "Calling function '%s' both as a function and a constructor.", 1)
  /* BugKind : 10 ~ 19 */
  val CallNonConstructor    :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling %s as a constructor.", 1)
  val CallNonFunction       :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling %s as a function.", 1)
  val CondBranch            :BugKind = addBugMsgFormat(newBugKind, Warning, "Conditional expression '%s' is always %s.", 2)
  val ConvertUndefToNum     :BugKind = addBugMsgFormat(newBugKind, Warning, "Trying to convert undefined to number.%s", 1)
  val DefaultValue          :BugKind = addBugMsgFormat(newBugKind, Warning, "Assigning a non-function value to '%s' may cause a TypeError.", 1)
  val FunctionArgSize       :BugKind = addBugMsgFormat(newBugKind, Warning, "Too %s arguments to function '%s'.", 2)
  val GlobalThis            :BugKind = addBugMsgFormat(newBugKind, Warning, "'this' refers the global object.", 0)
  val ImplicitTypeConvert   :BugKind = addBugMsgFormat(newBugKind, Warning, "Implicit type-conversion in equality comparison '%s%s %s %s%s'.", 5)
  val ObjectNullOrUndef     :BugKind = addBugMsgFormat(newBugKind, TypeError, "Property is trying to access %s, whose value is %s.", 2)
  val PrimitiveToObject     :BugKind = addBugMsgFormat(newBugKind, Warning, "Trying to convert primitive value(%s) to object.", 1)
  /* BugKind : 20 ~ 29 */
  val ShadowedFuncByFunc    :BugKind = addBugMsgFormat(newBugKind, Warning, "Function '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedParamByFunc   :BugKind = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedVarByFunc     :BugKind = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedVarByParam    :BugKind = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a parameter at '%s'.", 2)
  val ShadowedVarByVar      :BugKind = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a variable at '%s'.", 2)
  val ShadowedParamByParam  :BugKind = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a parameter at '%s'.", 2)
  val ShadowedFuncByVar     :BugKind = addBugMsgFormat(newBugKind, Warning, "Function '%s' is shadowed by a variable at '%s'.", 2)
  val ShadowedParamByVar    :BugKind = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a variable at '%s'.", 2)
  val StrictModeR1          :BugKind = addBugMsgFormat(newBugKind, Warning, "'%s' is classified as FutureReservedWord token within strict mode code.", 1)
  val StrictModeR2          :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Strict mode code may not include the syntax of OctalIntegerLiteral '0%s'.", 1)
  /* BugKind : 30 ~ 39 */
  val StrictModeR3          :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Strict mode code may not include the syntax of OctalEscapeSequence '%s'.", 1)
  val StrictModeR4_1        :BugKind = addBugMsgFormat(newBugKind, ReferenceError, "'%s' must not evaluate to an unresolvable reference within strict mode code.", 1)
  val StrictModeR4_2        :BugKind = addBugMsgFormat(newBugKind, TypeError, "'%s' must not be a reference to a data property with the attribute value {[[Writable]]: false} within strict mode code.", 1)
  val StrictModeR4_4        :BugKind = addBugMsgFormat(newBugKind, TypeError, "'%s' must not be a reference to a non-existent property of an object whose [[Extensible]] internal property has the value false within strict mode code.", 1)
  val StrictModeR5          :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot appear as an identifier of the %s within strict mode code.", 2)
  val StrictModeR6          :BugKind = addBugMsgFormat(newBugKind, TypeError, "Arguments objects for strict mode functions define non-configurable properties named 'caller' and 'callee'.", 0)
  val StrictModeR9          :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "ObjectLiteral in a strict mode code cannot have data properties with the same name, '%s'.", 1)
  val StrictModeR10         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot be used as an identifier of the property %s within strict mode code.", 2)
  val StrictModeR13         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "The delete operator occurs within strict mode code and '%s' is a direct reference to a variable, function argument, or function name.", 1)
  val StrictModeR14         :BugKind = addBugMsgFormat(newBugKind, TypeError, "The delete operator occurs within strict mode code and the property '%s' to be deleted has the attribute {[[Configurable]]: false}.", 1)
  /* BugKind : 40 ~ 49 */
  val StrictModeR15         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot be used as an identifier of the variable declaration within strict mode code.", 1)
  val StrictModeR16         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Strict mode code may not include a WithStatement.", 0)
  val StrictModeR17         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot be used as an identifier of the catch statement within strict mode code.", 1)
  val StrictModeR18         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot be used as an identifier of the formal parameter within strict mode code.", 1)
  val StrictModeR19         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "A function in a strict mode code cannot have formal parameters with the same name, '%s'.", 1)
  val StrictModeR21         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' cannot be used as an identifier of the function name within strict mode code.", 1)
  val StrictMode11_4_1_3_a  :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "'%s' must not be a reference to a non-existent property within strict mode code.", 1)
  val StrictMode10_2_1_2_4  :BugKind = addBugMsgFormat(newBugKind, ReferenceError, "'%s' is not initialized in strict mode.", 1)
  val StrictModeLastDummy   :BugKind = addBugMsgFormat(newBugKind, Warning, "", 0) // to count strict mode bugs by iteration
  val UnreachableCode       :BugKind = addBugMsgFormat(newBugKind, Warning, "Unreachable code '%s' is found.", 1)
  /* BugKind : 50 ~ 59 */
  val UnreferencedFunction  :BugKind = addBugMsgFormat(newBugKind, Warning, "Function '%s' is neither called nor referenced.", 1)
  val UncalledFunction      :BugKind = addBugMsgFormat(newBugKind, Warning, "Function '%s' is never called.", 1)
  val UnusedVarProp         :BugKind = addBugMsgFormat(newBugKind, Warning, "Value assigned to %s is never read.", 1)
  val VaryingTypeArguments  :BugKind = addBugMsgFormat(newBugKind, Warning, "Calling a function '%s' with the %sparameter %sof varying types (%s).", 4)
  val WrongThisType         :BugKind = addBugMsgFormat(newBugKind, TypeError, "Native function '%s' is called when its 'this' value is not of the expected object type.", 1)
  val EvalArgSyntax         :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Calling the eval function with an illegal syntax: '%s'.", 1)
  val RegExp2_5             :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression quantifier's max %s is finite and less than min %s.", 2)
  val RegExp2_9_1           :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's decimal escape value is 0.", 0)
  val RegExp2_9_2           :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's decimal escape value %s is bigger than the total number of left capturing parentheses %s.", 2)
  val RegExp2_15_2          :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's character range is invalid: from %s to %s.", 2)
  /* BugKind : 60 ~ 69 */
  val RegExp4_1_1           :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's pattern syntax is invalid: %s.", 1)
  val RegExp4_1_2           :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's flags syntax is invalid: %s.", 1)
  val RegExp2_19            :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Regular expression's decimal escape character is invalid: %s.", 1)
  val Range15_4_5_1         :BugKind = addBugMsgFormat(newBugKind, RangeError, "Array's new length %s is not equal to its size %s.", 2)
  val Range15_9_5_43        :BugKind = addBugMsgFormat(newBugKind, RangeError, "Date's time value %s is not a finite number.", 1)
  val ParseFunctionBody     :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Constructing Function with a body argument of illegal syntax: %s.", 1)
  val ParseFunctionParams   :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "Constructing Function with a parameter argument of illegal syntax: %s.", 1)
  val ParseJSON             :BugKind = addBugMsgFormat(newBugKind, SyntaxError, "JSON text's syntax is invalid: %s.", 1)
  val URIErrorArg           :BugKind = addBugMsgFormat(newBugKind, URIError, "Calling the %s function with an invalid argument: %s.", 2)
  val ToPropertyDescriptor  :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Object.defineProperty with an invalid argument: %s.", 1)
  /* BugKind : 70 ~ 77 */
  val ToPropertyDescriptors :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Object.defineProperties with an invalid argument: %s.", 1)
  val JSONStringify         :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling JSON.stringify with a cyclic value.", 0)
  val ArrayReduce1          :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Array.prototype.reduce with an empty array without any initial value.", 0)
  val ArrayReduce2          :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Array.prototype.reduce with an empty array without any initial value.", 0)
  val ArrayReduceRight1     :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Array.prototype.reduceRight with an empty array without any initial value.", 0)
  val ArrayReduceRight2     :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Array.prototype.reduceRight with an empty array without any initial value.", 0)
  val ToLocaleString        :BugKind = addBugMsgFormat(newBugKind, TypeError, "Calling Array.prototype.toLocaleString with an invalid element: %s", 1)
  val RegExpDeprecated      :BugKind = addBugMsgFormat(newBugKind, Warning, "Using %s can result in problems, since browser extensions can modify them. Avoid using them.", 1)

  //val DefaultValueTypeError :BugKind = addBugMsgFormat(newBugKind, TypeError, "Computing default value (toString, valueOf) of %s yields TypeError.", 1)
  //val ImplicitCallToString  :BugKind = addBugMsgFormat(newBugKind, Warning, "Implicit toString type-conversion to object '%s' by non-builtin toString method.", 1)
  //val ImplicitCallValueOf   :BugKind = addBugMsgFormat(newBugKind, Warning, "Implicit valueOf type-conversion to object '%s' by non-builtin valueOf method.", 1)

  /* BugKind : 78 ~ 79 */
  val TSWrongArgs           :BugKind = addBugMsgFormat(newBugKind, TSError, "The number of the arguments to %s should be %s; given number of arguments is %s.", 3)
  val TSWrongArgType        :BugKind = addBugMsgFormat(newBugKind, TSError, "Argument %s of the function %s should be %s.", 3)

  /* BugKind : 80 ~ 89 */
  val WebAPIInvalidNamespace    :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "Name %s is not found in the API %s.", 2)
  val WebAPIWrongArgs           :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "The number of the arguments to %s is %s; provide arguments of size %s.", 3)
  val WebAPIMissingErrorCB      :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "Call to %s is missing an error callback function; provide an error callback function.", 1)
  val WebAPINoExceptionHandling :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "Function %s may raise an exception; call the function inside the try statement.", 1)
  val WebAPIWrongArgType        :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "Argument #%s of the function %s is wrong; the expected type is %s.", 3)
  val WebAPIWrongConstructor    :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "No matching constructors for %s; possible constructors are: %s.", 2)
  val WebAPIWrongDictionaryType :BugKind = addBugMsgFormat(newBugKind, WebAPIError, "The property %s is not a member of the dictionary %s.", 2)
  val TimeLimitExceeded         :BugKind = addBugMsgFormat(newBugKind, TimeLimitError, "Time limit %s is exceeded while analyzing.", 1)
  val GuideVarArg               :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not declare arguments Array as variables in functions.", 0)
  val GuideVarVar               :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Use var to declare the variable %s.", 1)

  /* BugKind : 90 ~ 97 */
  val GuideClosure              :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not capture the variable %s in a closure.", 1)
  val GuideExtraSemicolon       :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not use extra semicolons.", 0)
  val GuideNoDelete             :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not use the delete keyword.", 0)
  val GuideNoLongStr            :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not use multi-line string literals.", 0)
  val GuideNoArrayObjectConstr  :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not use constructors for an %s.", 1)
  val GuideNoBuiltinProto       :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not modify the prototype of a built-in object %s.", 1)
  val GuideNoObjToPrimitive     :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not convert a wrapper object %s to a primitive value.", 1)
  val GuideNoForInArrays        :BugKind = addBugMsgFormat(newBugKind, GuideWarning, "Do not use for-in with arrays.", 0)

  /* 
  * toStringSet: Set of builtin methods that use ToString internally
  * toNumberSet: Set of builtin methods that use ToNumber internally
  * (Optional) distinguish different arguments in built-in Methods?
  */
  val toStringSet: Set[String] = Set("String.prototype.charAt", "String.prototype.charCodeAt", "String.prototype.concat",
    "String.prototype.indexOf", "String.prototype.lastIndexOf", "String.prototype.localeCompare", "String.prototype.match", 
    "String.prototype.replace", "String.prototype.search", "String.prototype.slice", "String.prototype.slice", 
    "String.prototype.split", "String.prototype.substring", "String.prototype.toLowerCase", "String.prototype.trim")

  val toNumberSet: Set[String] = Set("Global.isNaN", "Global.isFinite", "Date.prototype.setTime", "Date.prototype.setMilliseconds",
    "Date.prototype.setUTCMilliseconds", "Date.prototype.setSeconds", "Date.prototype.setUTCSeconds", "Date.prototype.setMinutes", 
    "Date.prototype.setUTCMinutes", "Date.prototype.setHours", "Date.prototype.setUTCHours", "Date.prototype.setDate", "Date.prototype.setUTCDate",
    "Date.prototype.setMonth", "Date.prototype.setUTCMonth", "Date.prototype.setFullYear", "Date.prototype.setUTCFullYear")

  /* Set of all toString and valueOf builtin methods */
  val internalMethodMap: Map[String, Set[String]] = Map(
    "toString" -> Set("Object.prototype.toString", "Function.prototype.toString", "Array.prototype.toString", 
                      "String.prototype.toString", "Boolean.prototype.toString", "Number.prototype.toString", 
                      "Date.prototype.toString", "RegExp.prototype.toString", "Error.prototype.toString"), 
    "valueOf"  -> Set("Object.prototype.valueOf", "String.prototype.valueOf", "Boolean.prototype.valueOf", 
                      "Number.prototype.valueOf", "Date.prototype.valueOf"))
  
  /* Map each builtin methods to its argument size (min, max)  */ 
  val argSizeMap: Map[String, (Int, Int)] = Map(
    // Built-in
    "Global.eval" -> (1,1), "Global.parseInt" -> (1,2), "Global.parseFloat" -> (1,1),	
    "Global.isNaN" -> (1,1), "Global.isFinite" -> (1,1), "Global.decodeURI" -> (1,1),
    "Global.decodeURIComponent" -> (1,1), "Global.encodeURI" -> (1,1), "Global.encodeURIComponent" -> (1,1),
    "Object" -> (0,1), "Object.constructor" -> (0,1), "Object.getPrototypeOf" -> (1,1),
    "Object.getOwnPropertyDescriptor" -> (2,2), "Object.getOwnPropertyNames" -> (1,1), "Object.create" -> (1,2),
    "Object.defineProperty" -> (3,3), "Object.defineProperties" -> (2,2), "Object.seal" -> (1,1),
    "Object.freeze" -> (1,1), "Object.preventExtensions" -> (1,1), "Object.isSealed" -> (1,1),
    "Object.isFrozen" -> (1,1), "Object.isExtensible" -> (1,1), "Object.keys" -> (1,1),
    "Object.prototype.toString" -> (0,0), "Object.prototype.toLocaleString" -> (0,0), "Object.prototype.valueOf" -> (0,0),
    "Object.prototype.hasOwnProperty" -> (1,1), "Object.prototype.isPrototypeOf" -> (1,1), "Object.prototype.propertyIsEnumerable" -> (1,1),
    "Function" -> (0,Int.MaxValue), "Function.constructor" -> (0,Int.MaxValue), "Function.prototype.toString" -> (0,0),
    "Function.prototype.apply" -> (1,2), "Function.prototype.call" -> (1,Int.MaxValue), "Function.prototype.bind" -> (1,Int.MaxValue),
    "Array" -> (0,Int.MaxValue), "Array.constructor" -> (0,Int.MaxValue), "Array.isArray" -> (1,1),
    "Array.prototype.toString" -> (0,0), "Array.prototype.toLocaleString" -> (0,0), "Array.prototype.concat" -> (0,Int.MaxValue),		
    "Array.prototype.join" -> (0,1), "Array.prototype.pop" -> (0,0), "Array.prototype.push" -> (0,Int.MaxValue),
    "Array.prototype.reverse" -> (0,0), "Array.prototype.shift" -> (0,0),		 "Array.prototype.slice" -> (1,2),		
    "Array.prototype.sort" -> (1,1), "Array.prototype.splice" -> (2,Int.MaxValue), "Array.prototype.unshift" -> (0,Int.MaxValue),
    "Array.prototype.indexOf" -> (1,2), "Array.prototype.lastIndexOf" -> (1,2), "Array.prototype.every" -> (1,2),
    "Array.prototype.some" -> (1,2), "Array.prototype.forEach" -> (1,2), "Array.prototype.map" -> (1,2),
    "Array.prototype.filter" -> (1,2), "Array.prototype.reduce" -> (1,2), "Array.prototype.reduceRight" -> (1,2),
    "String" -> (0,1), "String.constructor" -> (0,1), "String.fromCharCode" -> (0,Int.MaxValue),
    "String.prototype.toString" -> (0,0), "String.prototype.valueOf" -> (0,0), "String.prototype.charAt" -> (1,1),
    "String.prototype.charCodeAt" -> (1,1), "String.prototype.concat" -> (0,Int.MaxValue), "String.prototype.indexOf" -> (1,2),
    "String.prototype.lastIndexOf" -> (1,2), "String.prototype.localeCompare" -> (1,1), "String.prototype.match" -> (1,1),
    "String.prototype.replace" -> (2,2), "String.prototype.search" -> (1,1), "String.prototype.slice" -> (1,2),
    "String.prototype.split" -> (0,2), "String.prototype.substring" -> (1,2), "String.prototype.toLowerCase" -> (0,0),
    "String.prototype.toLocaleLowerCase" -> (0,0), "String.prototype.toUpperCase" -> (0,0), "String.prototype.toLocaleUpperCase" -> (0,0),
    "String.prototype.trim" -> (0,0), "String.prototype.substr" -> (1,2),
    "Boolean" -> (1,1),	 "Boolean.constructor" -> (1,1), "Boolean.prototype.toString" -> (0,0), "Boolean.prototype.valueOf" -> (0,0),
    "Number" -> (0,1), "Number.constructor" -> (0,1), "Number.prototype.toString" -> (0,1), "Number.prototype.toLocaleString" -> (0,0),
    "Number.prototype.valueOf" -> (0,0), "Number.prototype.toFixed" -> (0,1), "Number.prototype.toExponential" -> (0,1),
    "Number.prototype.toPrecision" -> (0,1), 
    "Math.abs" -> (1,1), "Math.acos" -> (1,1), "Math.asin" -> (1,1),	"Math.atan" -> (1,1), "Math.atan2" -> (2,2),		
    "Math.ceil" -> (1,1),	"Math.cos" -> (1,1), "Math.exp" -> (1,1), "Math.floor" -> (1,1), "Math.log" -> (1,1),
    "Math.max" -> (0,Int.MaxValue), "Math.min" -> (0,Int.MaxValue), "Math.pow" -> (2,2), "Math.random" -> (0,0),
    "Math.round" -> (1,1), "Math.sin" -> (1,1), "Math.sqrt" -> (1,1), "Math.tan" -> (1,1),
    "Date" -> (0,7), "Date.constructor" -> (0,7), "Date.parse" -> (1,1), "Date.UTC" -> (2,7), "Date.now" -> (0,0),
    "Date.prototype.toString" -> (0,0), "Date.prototype.toDateString" -> (0,0), "Date.prototype.toTimeString" -> (0,0),
    "Date.prototype.toLocaleString" -> (0,0), "Date.prototype.toLocaleDateString" -> (0,0), "Date.prototype.toLocaleTimeString" -> (0,0),
    "Date.prototype.valueOf" -> (0,0), "Date.prototype.getTime" -> (0,0), "Date.prototype.getFullYear" -> (0,0),
    "Date.prototype.getUTCFullYear" -> (0,0), "Date.prototype.getMonth" -> (0,0), "Date.prototype.getUTCMonth" -> (0,0),
    "Date.prototype.getDate" -> (0,0), "Date.prototype.getUTCDate" -> (0,0), "Date.prototype.getDay" -> (0,0),
    "Date.prototype.getUTCDay" -> (0,0), "Date.prototype.getHours" -> (0,0), "Date.prototype.getUTCHours" -> (0,0),
    "Date.prototype.getMinutes" -> (0,0), "Date.prototype.getUTCMinutes" -> (0,0), "Date.prototype.getSeconds" -> (0,0),
    "Date.prototype.getUTCSeconds" -> (0,0), "Date.prototype.getMilliseconds" -> (0,0), "Date.prototype.getUTCMilliseconds" -> (0,0),
    "Date.prototype.getTimezoneOffset" -> (0,0), "Date.prototype.setTime" -> (1,1), "Date.prototype.setMilliseconds" -> (1,1),
    "Date.prototype.setUTCMilliseconds" -> (1,1), "Date.prototype.setSeconds" -> (1,2), "Date.prototype.setUTCSeconds" -> (1,2),
    "Date.prototype.setMinutes" -> (1,3), "Date.prototype.setUTCMinutes" -> (1,3), "Date.prototype.setHours" -> (1,4),
    "Date.prototype.setUTCHours" -> (1,4), "Date.prototype.setDate" -> (1,1), "Date.prototype.setUTCDate" -> (1,1),
    "Date.prototype.setMonth" -> (1,2), "Date.prototype.setUTCMonth" -> (1,2), "Date.prototype.setFullYear" -> (1,3),
    "Date.prototype.setUTCFullYear" -> (1,3), "Date.prototype.toUTCString" -> (0,0), "Date.prototype.toISOString" -> (0,0),
    "Date.prototype.toJSON" -> (1,1),
    "RegExp" -> (1,2), "RegExp.constructor" -> (1,2), "RegExp.prototype.exec" -> (1,1), "RegExp.prototype.test" -> (1,1),
    "RegExp.prototype.toString" -> (0,0),
    "Error" -> (0,1), "Error.constructor" -> (0,1), "Error.prototype.toString" -> (0,0), "EvalError.constructor" -> (0,1),
    "RangeError.constructor	" -> (0,1), "ReferenceError.constructor" -> (0,1), "SyntaxError.constructor" -> (0,1),
    "TypeError.constructor" -> (0,1), "URIError.constructor" -> (0,1),
    "JSON.parse" -> (1,2), "JSON.stringify" -> (1,3),
    // DOM
    "DOMWindow.alert" -> (1,1)
    // Tizen
    // jQuery
  )

  /* Map each builtin methods to its argument type */ 
  val argTypeMap: Map[String, (Int, Int)] = Map(
    "Object.getPrototypeOf" -> (0, EJSType.OBJECT),
    "Object.getOwnPropertyDescriptor" -> (0, EJSType.OBJECT),
    "Object.getOwnPropertyNames" -> (0, EJSType.OBJECT),
    "Object.create" -> (0, EJSType.OBJECT),
    "Object.defineProperty" -> (0, EJSType.OBJECT),
    "Object.defineProperties" -> (0, EJSType.OBJECT),
    "Object.seal" -> (0, EJSType.OBJECT),
    "Object.freeze" -> (0, EJSType.OBJECT),
    "Object.preventExtensions" -> (0, EJSType.OBJECT),
    "Object.isSealed" -> (0, EJSType.OBJECT),
    "Object.isFrozen" -> (0, EJSType.OBJECT),
    "Object.isExtensible" -> (0, EJSType.OBJECT),
    "Object.keys" -> (0, EJSType.OBJECT),
    "Function.prototype.apply" -> (1, EJSType.OBJECT),
    "Array.prototype.sort" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.every" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.some" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.forEach" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.map" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.filter" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.reduce" -> (0, EJSType.OBJECT_FUNCTION),
    "Array.prototype.reduceRight" -> (0, EJSType.OBJECT_FUNCTION)
  )

  /* Map each builtin methods to its argument range */
  val argRangeMap: Map[String, (Int, Int)] = Map(
    "Number.prototype.toString" -> (2, 36),
    "Number.prototype.toFixed" -> (0, 20),
    "Number.prototype.toExponential" -> (0, 20),
    "Number.prototype.toPrecision" -> (1, 21)
  )

  /* Set of builtin methods that cannot be used as a constructor */
  /*val nonConsSet: Set[String] = Set(
    "Global.eval", "Global.parseInt", "Global.parseFloat", "Global.isNaN", "Global.isFinite",
    "Global.decodeURI", "Global.decodeURIComponent", "Global.encodeURI", "Global.encodeURIComponent",
    "Math.abs", "Math.acos", "Math.asin", "Math.atan", "Math.atan2", "Math.ceil", "Math.cos", 
    "Math.exp", "Math.floor", "Math.log", "Math.max", "Math.min", "Math.pow", "Math.random", 
    "Math.round", "Math.sin", "Math.sqrt", "Math.tan", "JSON.parse", "JSON.stringify")*/

  /* Map each builtin methods with its proper type of 'this' */
  val thisTypeMap: Map[String, String] = Map(
    "String.prototype.toString" -> "String", "String.prototype.valueOf" -> "String",
    "Boolean.prototype.toString" -> "Boolean", "Boolean.prototype.valueOf" -> "Boolean",
    "Number.prototype.toString" -> "Number", "Number.prototype.valueOf" -> "Number",
    "Number.prototype.toLocaleString" -> "Number", "Number.prototype.toFixed" -> "Number",
    "Number.prototype.toExponential" -> "Number", "Number.prototype.toPrecision" -> "Number",
    "Date.prototype.valueOf" -> "Date", "Date.prototype.getTime" -> "Date",
    "Date.prototype.getFullYear" -> "Date", "Date.prototype.getUTCFullYear" -> "Date",
    "Date.prototype.getMonth" -> "Date", "Date.prototype.getUTCMonth" -> "Date",
    "Date.prototype.getDate" -> "Date", "Date.prototype.getUTCDate" -> "Date",
    "Date.prototype.getDay" -> "Date", "Date.prototype.getUTCDay" -> "Date",
    "Date.prototype.getHours" -> "Date", "Date.prototype.getUTCHours" -> "Date",
    "Date.prototype.getMinutes" -> "Date", "Date.prototype.getUTCMinutes" -> "Date",
    "Date.prototype.getSeconds" -> "Date", "Date.prototype.getUTCSeconds" -> "Date",
    "Date.prototype.getMilliseconds" -> "Date", "Date.prototype.getUTCMilliseconds" -> "Date",
    "Date.prototype.getTimezoneOffset" -> "Date", "Date.prototype.setMilliseconds" -> "Date",
    "Date.prototype.setUTCMilliseconds" -> "Date", "Date.prototype.setSeconds" -> "Date",
    "Date.prototype.setUTCSeconds" -> "Date", "Date.prototype.setMinutes" -> "Date",
    "Date.prototype.setUTCMinutes" -> "Date", "Date.prototype.setHours" -> "Date",
    "Date.prototype.setUTCHours" -> "Date", "Date.prototype.setDate" -> "Date",
    "Date.prototype.setUTCDate" -> "Date", "Date.prototype.setMonth" -> "Date",
    "Date.prototype.setUTCMonth" -> "Date", "Date.prototype.setFullYear" -> "Date",
    "Date.prototype.setUTCFullYear" -> "Date", "Date.prototype.getYear" -> "Date",
    "Date.prototype.setYear" -> "Date", "RegExp.prototype.exec" -> "RegExp",
    "RegExp.prototype.test" -> "RegExp", "RegExp.prototype.toString" -> "RegExp")

  /* Set of deprecated properties or RegExp */
  val regExpDeprecated: Set[String] = Set("$1", "$2", "$3", "$4", "$5", "$6", "$7", "$8", "$9")
}
