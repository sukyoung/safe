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

package kr.ac.kaist.safe.util

import scala.collection.mutable.{ Map => MMap }
import scala.collection.mutable.{ HashMap => MHashMap }

object BugDefinition {
  /**
   * *********************** TYPE DEFINITION ***************************
   * BugKind   : Identifier for each bug kind
   * BugId     : Identifier for each detected bug
   * BugType   : Type of Bug (TypeError, ReferenceError, Warning)
   * BugEntry  : (BugId, FileName, Begin, End, BugType, BugMessage)
   * BugList   : List of reported bugs held by BugEntry
   * BugMap    : Map each bug to its BugType, message and argNum
   * SoundnessLevel : Level of Soundness
   * ********************************************************************
   */
  type BugKind = Int
  type BugId = Int
  type BugType = Int
  type BugEntry = (BugId, String, SourceLoc, SourceLoc, Int, String)
  type BugList = List[BugEntry]
  type BugMap = MMap[BugKind, (BugType, String, Int)]
  type SoundnessLevel = Int

  /* BugIdCounter, BugKindCounter, BugType Constant, Definite Flag */
  var BugIdCounter = -1
  var BugKindCounter = -1
  val RangeError = 1
  val ReferenceError = 2
  val SyntaxError = 3
  val TypeError = 4
  val URIError = 5
  val Warning = 6
  val TSError = 7
  val TSWarning = 8
  val WebAPIError = 9
  val WebAPIWarning = 10
  val TimeLimitError = 11
  val GuideWarning = 12
  val definite_only = true

  /* BugKind generator */
  def newBugKind: BugKind = {
    BugKindCounter += 1; BugKindCounter
  }

  /* Stores all message formats */
  val bugTable: BugMap = MHashMap()

  def addBugMsgFormat(BugKind: BugKind, kind: BugType, msg: String, argNum: Int): Int =
    { bugTable(BugKind) = (kind, msg, argNum); BugKind }

  val ShadowedFuncByFunc = addBugMsgFormat(newBugKind, Warning, "Function '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedParamByFunc = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedVarByFunc = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a function at '%s'.", 2)
  val ShadowedVarByParam = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a parameter at '%s'.", 2)
  val ShadowedVarByVar = addBugMsgFormat(newBugKind, Warning, "Variable '%s' is shadowed by a variable at '%s'.", 2)
  val ShadowedParamByParam = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a parameter at '%s'.", 2)
  val ShadowedFuncByVar = addBugMsgFormat(newBugKind, Warning, "Function '%s' is shadowed by a variable at '%s'.", 2)
  val ShadowedParamByVar = addBugMsgFormat(newBugKind, Warning, "Parameter '%s' is shadowed by a variable at '%s'.", 2)
  val EvalArgSyntax = addBugMsgFormat(newBugKind, SyntaxError, "Calling the eval function with an illegal syntax: '%s'.", 1)
}
