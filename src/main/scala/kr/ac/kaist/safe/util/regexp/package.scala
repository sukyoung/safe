/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.safe.util

import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet}

package object regexp {
  type CharSet = MHashSet[Char]
  type Continuation = (RegExpState) => MatchResult
  type Matcher = (RegExpState, Continuation) => MatchResult
  type MatchFunc = (String, Int) => MatchResult
  type AssertionTester = (RegExpState) => Boolean

  class RegExpState(var endIndex: Int, var captures: MHashMap[Int, Option[String]])
  type MatchResult = Option[RegExpState]

  abstract class EscapeValue
  case class CharEscapeValue(var ch: Char) extends EscapeValue
  case class IntEscapeValue(var n: Int) extends EscapeValue

  class RegExpEnv(var input: String,
                  var nCapturingParens: Int,
                  var global: Boolean,
                  var ignoreCase: Boolean,
                  var multiline: Boolean)


  abstract class RegExpErrorKind
  case object ESyntax extends RegExpErrorKind
  case object ERegExp4_1_1 extends RegExpErrorKind
  case object ERegExp4_1_2 extends RegExpErrorKind
  case object ERegExp2_5 extends RegExpErrorKind
  case object ERegExp2_15_2 extends RegExpErrorKind

  class SyntaxErrorException(kind: RegExpErrorKind, msg1: String, msg2: String) extends Exception {
    val getKind = kind
    val getMsg1 = msg1
    val getMsg2 = msg2
  }
}
