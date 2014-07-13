/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import java.util.{HashMap => JMap}
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.bug_detector._
import kr.ac.kaist.jsaf.ShellParameters

class BugStat(bugDetector: BugDetector) {
  val lib : Boolean = bugDetector.libMode
  val dtv : Boolean = bugDetector.params.command == ShellParameters.CMD_DTV_APP
  private var startTime : Long = 0L
  private var endTime   : Long = 0L

  private var totalCount           : Int = 0
  private var warningCount         : Int = 0
  private var typeErrorCount       : Int = 0
  private var referenceErrorCount  : Int = 0
  private val BugCount: Array[Int] = new Array(MAX_BUG_COUNT)

  def increaseBugCounter(bugKind: BugKind, bugType: BugType): Unit = {
    bugType match {
      case Warning => warningCount = warningCount + 1
      case TypeError => typeErrorCount = typeErrorCount + 1
      case ReferenceError => referenceErrorCount = referenceErrorCount + 1
    }
    BugCount(bugKind) += 1
  }

  private def countTotalBugs: Unit = totalCount = referenceErrorCount + typeErrorCount + warningCount
  private def printDetectingTime: Unit = System.out.println("# Time for bug Detection(s): %.2f".format((endTime - startTime) / 1000000000.0))
  private def divideByZeroCheck(flag: BugType): Float = if (totalCount <= 0) 0 toFloat else 
    (flag match {case ReferenceError => referenceErrorCount; case TypeError => typeErrorCount; case Warning => warningCount}).toFloat/totalCount*100
  private def printTotalBugCount: Unit = {
    countTotalBugs
    System.out.println
    System.out.println("============== Total Count ==============")
    System.out.println("|  ReferenceErrors   : %6d (%6.2f%%) |".format(referenceErrorCount, divideByZeroCheck(ReferenceError))) 
    System.out.println("|  TypeErrors        : %6d (%6.2f%%) |".format(typeErrorCount, divideByZeroCheck(TypeError)))
    System.out.println("|  Warnings          : %6d (%6.2f%%) |".format(warningCount, divideByZeroCheck(Warning)))
    System.out.println("=========================================")
  }
  private def getUnusedFunctionCount: Int = (if (lib) BugCount(UnreferencedFunction) else 0) + BugCount(UncalledFunction)
  private def printBugStatistics: Unit = {
    System.out.println("============ Statistics =============")
    System.out.println("|  AbsentRead              : %6d |".format(BugCount(AbsentReadProperty) + BugCount(AbsentReadVariable)))
    if (!dtv)
      System.out.println("|  BinaryOperator          : %6d |".format(BugCount(BinaryOpSecondType)))
    System.out.println("|  BuiltinWrongArgType     : %6d |".format(BugCount(BuiltinWrongArgType)))
    if (!dtv)
      System.out.println("|  CallConstFunc           : %6d |".format(BugCount(CallConstFunc)))
    System.out.println("|  CallNonConstructor      : %6d |".format(BugCount(CallNonConstructor)))
    System.out.println("|  CallNonFunction         : %6d |".format(BugCount(CallNonFunction)))
    if (!dtv)
      System.out.println("|  ConditionalBranch       : %6d |".format(BugCount(CondBranch)))
    System.out.println("|  ConvertToNumber         : %6d |".format(BugCount(ConvertUndefToNum)))
    System.out.println("|  DefaultValueTypeError   : %6d |".format(BugCount(DefaultValueTypeError)))
    if (!dtv) {
      System.out.println("|  FunctionArgSize         : %6d |".format(BugCount(FunctionArgSize)))
      System.out.println("|  GlobalThis              : %6d |".format(BugCount(GlobalThis)))
    }
    System.out.println("|  ImplicitTypeConversion  : %6d |".format(/*BugCount(ImplicitCallToString) + BugCount(ImplicitCallValueOf) +*/ BugCount(ImplicitTypeConvert)))
    if (!dtv)
      System.out.println("|  AccessingNullOrUndef    : %6d |".format(BugCount(ObjectNullOrUndef)))
    System.out.println("|  PrimitiveToObject       : %6d |".format(BugCount(PrimitiveToObject)))
    if (!dtv)
      System.out.println("|  Shadowing               : %6d |".format(BugCount(ShadowedFuncByFunc) + BugCount(ShadowedParamByFunc) + BugCount(ShadowedVarByFunc) + BugCount(ShadowedVarByParam) + BugCount(ShadowedVarByVar)))
    if (!dtv && !bugDetector.params.opt_jQuery) {
      System.out.println("|  UncalledFuction         : %6d |".format(getUnusedFunctionCount))
      System.out.println("|  UnreachableCode         : %6d |".format(BugCount(UnreachableCode)))
      System.out.println("|  ValueNeverRead          : %6d |".format(BugCount(UnusedVarProp)))
    }
    System.out.println("|  VaryingTypeArguments    : %6d |".format(BugCount(VaryingTypeArguments)))
    System.out.println("|  WrongThisType           : %6d |".format(BugCount(WrongThisType)))
    System.out.println("=====================================")
  }

  def setStartTime(time: Long): Unit = startTime = time
  def setEndTime(time: Long): Unit = endTime = time

  def reportBugStatistics(quiet: Boolean) = {
    printTotalBugCount
    printBugStatistics
    if (!quiet) printDetectingTime
  }
}
