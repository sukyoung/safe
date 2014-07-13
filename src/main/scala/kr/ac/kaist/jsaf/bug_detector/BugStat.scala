/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters


class BugStat(bugDetector: BugDetector) {
  val lib : Boolean = bugDetector.libMode
  val ts : Boolean = bugDetector.params.command == ShellParameters.CMD_TSCHECK
  private var startTime : Long = 0L
  private var endTime   : Long = 0L

  private var totalCount           : Int = 0
  private var rangeErrorCount      : Int = 0
  private var referenceErrorCount  : Int = 0
  private var syntaxErrorCount     : Int = 0
  private var typeErrorCount       : Int = 0
  private var uriErrorCount        : Int = 0
  private var warningCount         : Int = 0
  private var TSErrorCount         : Int = 0
  private var TSWarningCount       : Int = 0
  private var bugCount             : Array[Int] = null

  def createBugCountArray: Unit = if(bugCount == null) bugCount = new Array(BugKindCounter + 1)
  def decreaseBugCounter(bugKind: BugKind, bugType: BugType): Unit = {
    createBugCountArray
    totalCount-= 1
    bugType match {
      case RangeError => rangeErrorCount-= 1
      case ReferenceError => referenceErrorCount-= 1
      case SyntaxError => syntaxErrorCount-= 1
      case TypeError => typeErrorCount-= 1
      case URIError => uriErrorCount-= 1
      case Warning => warningCount-= 1
      case TSError => TSErrorCount-= 1
      case TSWarning => TSWarningCount-= 1
      case _ => System.out.println(bugType)
    }
    bugCount(bugKind) -= 1
  }
  def increaseBugCounter(bugKind: BugKind, bugType: BugType): Unit = {
    createBugCountArray
    totalCount+= 1
    bugType match {
      case RangeError => rangeErrorCount+= 1
      case ReferenceError => referenceErrorCount+= 1
      case SyntaxError => syntaxErrorCount+= 1
      case TypeError => typeErrorCount+= 1
      case URIError => uriErrorCount+= 1
      case Warning => warningCount+= 1
      case TSError => TSErrorCount+= 1
      case TSWarning => TSWarningCount+= 1
      case _ => System.out.println(bugType)
    }
    bugCount(bugKind) += 1
  }
  def getErrorCount: Int = totalCount - warningCount

  private def printDetectingTime: Unit = System.out.println("# Time for bug Detection(s): %.2f".format((endTime - startTime) / 1000000000.0))
  private def divideByZeroCheck(flag: BugType): Float = {
    if (totalCount <= 0) 0 toFloat
    else (flag match {
      case RangeError => rangeErrorCount
      case ReferenceError => referenceErrorCount
      case SyntaxError => syntaxErrorCount
      case TypeError => typeErrorCount
      case URIError => uriErrorCount
      case Warning => warningCount
      case TSError => TSErrorCount
      case TSWarning => TSWarningCount
    }).toFloat/totalCount*100
  }
  private def printTotalBugCount: Unit = {
    System.out.println
    System.out.println("============== Total Count ==============")
    System.out.println("|  RangeErrors       : %6d (%6.2f%%) |".format(rangeErrorCount, divideByZeroCheck(RangeError)))
    System.out.println("|  ReferenceErrors   : %6d (%6.2f%%) |".format(referenceErrorCount, divideByZeroCheck(ReferenceError)))
    System.out.println("|  SyntaxErrors      : %6d (%6.2f%%) |".format(syntaxErrorCount, divideByZeroCheck(SyntaxError)))
    System.out.println("|  TypeErrors        : %6d (%6.2f%%) |".format(typeErrorCount, divideByZeroCheck(TypeError)))
    System.out.println("|  URIErrors         : %6d (%6.2f%%) |".format(uriErrorCount, divideByZeroCheck(URIError)))
    System.out.println("|  Warnings          : %6d (%6.2f%%) |".format(warningCount, divideByZeroCheck(Warning)))
    if (ts) {
      System.out.println("|  TSErrors          : %6d (%6.2f%%) |".format(TSErrorCount, divideByZeroCheck(TSError)))
      System.out.println("|  TSWarnings        : %6d (%6.2f%%) |".format(TSWarningCount, divideByZeroCheck(TSWarning)))
    }
    System.out.println("=========================================")
  }
  private def getStrictModeCount: Int = {
    var count = 0
    for(i <- StrictModeR1 until StrictModeLastDummy) count+= bugCount(i)
    count
  }
  private def getUnusedFunctionCount: Int = (if (lib) bugCount(UnreferencedFunction) else 0) + bugCount(UncalledFunction)
  private def printBugStatistics: Unit = {
    createBugCountArray
    System.out.println("============ Statistics =============")
    System.out.println("|  AbsentRead              : %6d |".format(bugCount(AbsentReadProperty) + bugCount(AbsentReadVariable)))
    System.out.println("|  BinaryOperator          : %6d |".format(bugCount(BinaryOpSecondType)))
    System.out.println("|  BuiltinTypeError        : %6d |".format(bugCount(BuiltinCallable) + bugCount(BuiltinRegExpConst) + bugCount(BuiltinThisType)))
    System.out.println("|  BuiltinWrongArgType     : %6d |".format(bugCount(BuiltinWrongArgType)))
    System.out.println("|  CallConstFunc           : %6d |".format(bugCount(CallConstFunc)))
    System.out.println("|  CallNonConstructor      : %6d |".format(bugCount(CallNonConstructor)))
    System.out.println("|  CallNonFunction         : %6d |".format(bugCount(CallNonFunction)))
    System.out.println("|  ConditionalBranch       : %6d |".format(bugCount(CondBranch)))
    System.out.println("|  ConvertToNumber         : %6d |".format(bugCount(ConvertUndefToNum)))
    System.out.println("|  DefaultValue            : %6d |".format(bugCount(DefaultValue)))
    System.out.println("|  Deprecated              : %6d |".format(bugCount(RegExpDeprecated)))
    System.out.println("|  FunctionArgSize         : %6d |".format(bugCount(FunctionArgSize)))
    System.out.println("|  GlobalThis              : %6d |".format(bugCount(GlobalThis)))
    System.out.println("|  ImplicitTypeConversion  : %6d |".format(/*BugCount(ImplicitCallToString) + BugCount(ImplicitCallValueOf) +*/ bugCount(ImplicitTypeConvert)))
    System.out.println("|  AccessingNullOrUndef    : %6d |".format(bugCount(ObjectNullOrUndef)))
    System.out.println("|  PrimitiveToObject       : %6d |".format(bugCount(PrimitiveToObject)))
    System.out.println("|  RangeError              : %6d |".format(bugCount(ArrayConstLength) + bugCount(BuiltinRange) + bugCount(Range15_4_5_1) + bugCount(Range15_9_5_43)))
    System.out.println("|  Shadowing               : %6d |".format(bugCount(ShadowedFuncByFunc) + bugCount(ShadowedParamByFunc) + bugCount(ShadowedVarByFunc) + bugCount(ShadowedVarByParam) + bugCount(ShadowedVarByVar) + bugCount(ShadowedParamByParam) + bugCount(ShadowedFuncByVar) + bugCount(ShadowedParamByVar)))
    System.out.println("|  StrictMode              : %6d |".format(getStrictModeCount))
    System.out.println("|  UncalledFunction        : %6d |".format(getUnusedFunctionCount))
    System.out.println("|  UnreachableCode         : %6d |".format(bugCount(UnreachableCode)))
    System.out.println("|  ValueNeverRead          : %6d |".format(bugCount(UnusedVarProp)))
    System.out.println("|  VaryingTypeArguments    : %6d |".format(bugCount(VaryingTypeArguments)))
    System.out.println("|  WrongThisType           : %6d |".format(bugCount(WrongThisType)))
    System.out.println("|  RegularExpression       : %6d |".format(bugCount(RegExp2_5)+bugCount(RegExp2_9_1)+bugCount(RegExp2_9_2)+bugCount(RegExp2_15_2)+bugCount(RegExp4_1_1)+bugCount(RegExp4_1_2)+bugCount(RegExp2_19)))
    System.out.println("|  WrongArgument           : %6d |".format(bugCount(EvalArgSyntax)+bugCount(ParseFunctionBody)+bugCount(ParseFunctionParams)+bugCount(ParseJSON)+bugCount(URIErrorArg)+bugCount(ToPropertyDescriptor)+bugCount(ToPropertyDescriptors)+bugCount(JSONStringify)+bugCount(ArrayReduce1)+bugCount(ArrayReduce2)+bugCount(ArrayReduceRight1)+bugCount(ArrayReduceRight2)+bugCount(ToLocaleString)))
    if (ts) {
      System.out.println("|  TSWrongArgs             : %6d |".format(bugCount(TSWrongArgs)))
      System.out.println("|  TSWrongArgType          : %6d |".format(bugCount(TSWrongArgType)))
    }
    System.out.println("=====================================")
  }

  def setStartTime(time: Long): Unit = startTime = time
  def setEndTime(time: Long): Unit = endTime = time

  def reportBugStatistics(quiet: Boolean) = {
    printTotalBugCount
    printBugStatistics
    if (!quiet || Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) 
      printDetectingTime
  }
}
