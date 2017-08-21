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

package kr.ac.kaist.safe.interpreter

import kr.ac.kaist.safe.errors.error.ECMASpecTestFailedError

/**
  * Assuming the ECMA specification tests (from the tests262/ folder) are being interpreted,
  * ECMASpecChecker relies on the format employed by these tests to verify the correctness of the interpreter.
  *
  * Specifically, variables in these tests are of the form "__resultX" and "__expectX" where the value of __resultX
  * should be equal to the corresponding "expected" variable. ECMASpecChecker looks for all variables in the environment
  * and all properties of the global object of the interpreter that have this format, and then checks whether their
  * values are equal.
  */
object ECMASpecChecker {

  /**
    * Verifies correctness of the interpreter by checking whether all variables and global object properties
    * with a name of the form "__resultX" have the same value as their complementary variable of the form
    * "__expectX".
    * If not, the name of the current test being run is written to the file [[logIncorrectTestsFileName]] and
    * messages are printed in the console to indicate the exact error.
    * @param IS The current InterpreterState.
    * @param fileName The name of the file being executed.
    */
  def doECMASpecTest(IS: InterpreterState, fileName: String): Unit = {
    val errorMessages = findUnexpectedValuesForECMA(IS)
    if (errorMessages.nonEmpty) {
      val fw = new java.io.FileWriter(logIncorrectTestsFileName, true)
      fw.write(fileName + "\n")
      fw.close()
      println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
      errorMessages.foreach((message: String) => println(s"Unexpected value in ECMA spec test: $message"))
      println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
      throw ECMASpecTestFailedError(errorMessages.mkString("; "))
    }
  }

  /**
    * Verifies correctness of the interpreter by checking whether all variables and global object properties
    * with a name of the form "__resultX" have the same value as their complementary variable of the form
    * "__expectX".
    * @param IS The current InterpreterState whose environment and global object will be browsed.
    * @return A boolean indicating whether the interpreter is correct (true) or not (false).
    */
  def isECMASpecTestOk(IS: InterpreterState): Boolean = {
    findUnexpectedValuesForECMA(IS).isEmpty
  }

  /**
    * Verifies correctness of the interpreter by checking whether all variables and global object properties
    * with a name of the form "__resultX" have the same value as their complementary variable of the form
    * "__expectX". Returns a list of messages: one message for each case where the value of the "result" variable
    * did NOT match the value of the "expect" variable.
    * @param IS The current InterpreterState whose environment and global object will be browsed.
    * @return A list of messages indicating problems with the test.
   */
  def findUnexpectedValuesForECMA(IS: InterpreterState): List[String] = {
    val resultPrefix = "__result"
    val expectPrefix = "__expect"

    def isResult(name: String): Boolean = name.startsWith(resultPrefix)
    def resultToExpectedName(name: String): String = {
      expectPrefix + name.stripPrefix(resultPrefix)
    }

    def checkEnv(env: Env): List[String] = env match {
      case EmptyEnv() =>
        Nil
      case ConsEnv(first, rest) => first match {
        case DeclEnvRec(store) =>
          val set: Set[String] = store.keySet.toSet
          val resultsNames = set.filter(isResult).toList
          val firstEnvMessages: List[String] = resultsNames.flatMap((resultName: String) => {
            val resultValue = store(resultName).value
            val expectedName: String = resultToExpectedName(resultName)
            val expectedValue = store(expectedName).value
            if (expectedValue == resultValue) {
              Nil
            } else {
              List[String](s"Value $expectedValue of $expectedName does not match value $resultValue of $resultName")
            }
          })
          firstEnvMessages ++ checkEnv(rest)
      }
    }

    def checkGlobalObject: List[String] = {
      val filteredVarNames: List[String] = IS.GlobalObject.property.keys().filter(isResult)
      filteredVarNames.flatMap((resultName: String) => {
        val expectedName: String = resultToExpectedName(resultName)
        IS.GlobalObject.getProp(expectedName) match {
          case Some(objectProp) =>
            val resultingOptValue: Option[Val] = IS.GlobalObject.getProp(resultName).get.value
            val expectedOptValue: Option[Val] = IS.GlobalObject.getProp(expectedName).get.value
            if (resultingOptValue == expectedOptValue) {
              Nil
            } else {
              List[String](s"Value $expectedOptValue of $expectedName does not match value $resultingOptValue of $resultName")
            }
          case None =>
            List[String](s"$expectedName not present")
        }
      })
    }

    checkEnv(IS.env) ++ checkGlobalObject
  }

}
