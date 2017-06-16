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

package kr.ac.kaist.safe.util.regexp

import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet}

object JSRegExpSolver {
//  val in = (a: CharSet, ch: Char) => a.contains(ch)
//  val notin = (a: CharSet, ch: Char) => !a.contains(ch)
//
//  val decimalDigit = '0' to '9'
//  val whiteSpace = List('\u0009', '\u000B', '\u000C', '\u0020', '\u00A0', '\uFEFF', '\u1680', '\u180E', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008', '\u2009', '\u200A', '\u202F', '\u205F', '\u3000')
//  val lineTerminator = List('\u000A', '\u000D', '\u2028', '\u2029')
//  val space = whiteSpace ++ lineTerminator
//  val alphanumeric = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_'
//
//  def characterSetMatcher(a: CharSet, invert: Boolean, env: RegExpEnv,
//                          contains: (CharSet, Char) => Boolean): Matcher = {
//    (x: RegExpState, c: Continuation) => {
//      val e = x.endIndex
//      if (e == env.input.length) {
//        None
//      } else {
//        val ch = env.input(e)
//        val cc = canonicalize(ch, env)
//        val aa = a.map(x => canonicalize(x, env))
//        if (!invert && !contains(aa, cc)) {
//          None
//        } else if (invert && contains(aa, cc)) {
//          None
//        } else {
//          val cap = x.captures
//          val y = new RegExpState(e+1, cap)
//          c(y)
//        }
//      }
//    }
//  }
//
//  def canonicalize(ch: Char, env: RegExpEnv): Char = {
//    if (!env.ignoreCase) ch
//    else {
//      val u = ch.toUpper
//      // TODO: 3. If u does not consist of a single character, return ch.
//      val cu = u
//      if (ch.toInt >= 128 && cu < 128) ch
//      else cu
//    }
//  }
//
//  def isWordChar(e: Int, env: RegExpEnv): Boolean = {
//    if (e == -1 || e == env.input.length) false
//    else {
//      val c: Char = env.input(e)
//      alphanumeric.contains(c)
//    }
//  }
//
//  def repeatMatcher(m: Matcher, _min: Int, _max: Option[Int], greedy: Boolean, _x: RegExpState, c: Continuation, parenIndex: Int, parenCount: Int): MatchResult = {
//    var (min: Int, max: Option[Int], x: RegExpState) = (_min, _max, _x)
//    def update(y: RegExpState): Boolean = {
//      if (min == 0 && y.endIndex == x.endIndex) false
//      else {
//        min = min match {
//          case 0 => 0
//          case _ => min-1
//        }
//        max = max match {
//          case Some(max) => Some(max-1)
//          case None => None
//        }
//        x = y
//        true
//      }
//    }
//    // Dummy Continuation
//    val dd: Continuation = (y: RegExpState) => Some(y)
//    // Stack of x
//    var s: List[RegExpState] = Nil
//    // Return value
//    var r: MatchResult = None
//    // Push
//    var cont: Boolean = true
//    while (cont) {
//      if (max.isDefined && max.get == 0) {
//        r = c(x)
//        cont = false
//      } else {
//        val cap = x.captures.clone()
//        for (k <- parenIndex+1 to parenIndex+parenCount) { cap(k) = None }
//        val e = x.endIndex
//        val xr = new RegExpState(e, cap)
//        if (min != 0) {
//          m(xr, dd) match {
//            case Some(y) => cont = update(y)
//            case None => cont = false
//          }
//        } else if (!greedy) {
//          val z = c(x)
//          z match {
//            case Some(s) =>
//              r = z
//              cont = false
//            case None =>
//              m(xr, dd) match {
//                case Some(y) => cont = update(y)
//                case None => cont = false
//              }
//          }
//        } else {
//          s ::= x
//          m(xr, dd) match {
//            case Some(y) => cont = update(y)
//            case None => cont = false
//          }
//        }
//      }
//    }
//    // Pop
//    while (s != Nil) {
//      x = s.head
//      s = s.drop(1)
//      r match {
//        case Some(s) => r = r
//        case None => r = c(x)
//      }
//    }
//    r
//  }
//
//  def getParenCount(node: RegExpAbstractNode): Int = node.getInfo match {
//    case SRegExpCapturingParensInfo(parenCount) => parenCount
//    case _ => 0
//  }
//
//  def walk(node: RegExpNode, env: RegExpEnv, parenIndex: Int): Matcher = node match {
//    /*
//     * 15.10.2.3 Disjunction
//     * Disjunction ::= Alternative
//     *               | Alternative "|" Disjunction
//     */
//    case SRegExpBaseDisjunction(_, a) => walk(a, env, parenIndex)
//    case SRegExpConsDisjunction(_, a, d) =>
//      val (m1, m2) = (walk(a, env, parenIndex), walk(d, env, parenIndex + getParenCount(a)))
//      (x: RegExpState, c: Continuation) => {
//        m1(x, c) match {
//          case Some(s) => Some(s)
//          case None => m2(x, c)
//        }
//      }
//
//    /*
//     * 15.10.2.4 Alternative
//     * Alternative ::= [empty]
//     *               | Alternative Term
//     */
//    case SRegExpEmptyAlternative(_) => (x: RegExpState, c: Continuation) => c(x)
//    case SRegExpConsAlternative(_, a, t) =>
//      val (m1, m2) = (walk(a, env, parenIndex), walk(t, env, parenIndex + getParenCount(a)))
//      (x: RegExpState, c: Continuation) => {
//        val d = (y: RegExpState) => m2(y, c)
//        m1(x, d)
//      }
//
//    /*
//     * 15.10.2.5 Term
//     * Term ::= Assertion
//     *        | Atom
//     *        | Atom Quantifier
//     */
//    case SRegExpAssertion(_) =>
//      /*
//       * Term ::= Assertion
//       * There is an inconsistency in the specification.
//       */
//      val t = node match {
//        /*
//         * 15.10.2.6 Assertion
//         * Assertion ::= ^
//         *             | $
//         *             | \ b
//         *             | \ B
//         *             | ( ? = Disjunction )
//         *             | ( ? ! Disjunction )
//         */
//        case SRegExpAssertionCaret(_) =>
//          (x: RegExpState) => {
//            val e = x.endIndex
//            if (e == 0) true
//            else if (!env.multiline) false
//            else if (lineTerminator.contains(env.input(e-1))) true
//            else false
//          }
//        case SRegExpAssertionDollar(_) =>
//          (x: RegExpState) => {
//            val e = x.endIndex
//            if (e == env.input.length) true
//            else if (!env.multiline) false
//            else if (lineTerminator.contains(env.input(e))) true
//            else false
//          }
//        case SRegExpAssertionLowerCaseB(_) =>
//          (x: RegExpState) => {
//            val e = x.endIndex
//            val a = isWordChar(e-1, env)
//            val b = isWordChar(e, env)
//            if (a && !b) true
//            else if (!a && b) true
//            else false
//          }
//        case SRegExpAssertionUpperCaseB(_) =>
//          (x: RegExpState) => {
//            val e = x.endIndex
//            val a = isWordChar(e-1, env)
//            val b = isWordChar(e, env)
//            if (a && !b) false
//            else if (!a && b) false
//            else true
//          }
//        case SRegExpAssertionEqual(_, d) =>
//          val m: Matcher = walk(d, env, parenIndex)
//          (x: RegExpState, c: Continuation) => {
//            val d: Continuation = (x: RegExpState) => Some(x)
//            val r: MatchResult = m(x, d)
//            r match {
//              case Some(y) =>
//                val cap = y.captures
//                val xe: Int = x.endIndex
//                val z: RegExpState = new RegExpState(xe, cap)
//                c(z)
//              case None => None
//            }
//          }
//        case SRegExpAssertionExcla(_, d) =>
//          val m: Matcher = walk(d, env, parenIndex)
//          (x: RegExpState, c: Continuation) => {
//            val d: Continuation = (x: RegExpState) => Some(x)
//            val r: MatchResult = m(x, d)
//            r match {
//              case Some(y) => None
//              case None => c(x)
//            }
//          }
//      }
//      t match {
//        case t:AssertionTester =>
//          (x: RegExpState, c: Continuation) => {
//            t(x) match {
//              case true => c(x)
//              case false => None
//            }
//          }
//        case m:Matcher => m
//      }
//
//    case SRegExpAtomQuantifier(SRegExpCapturingParensInfo(parenCount), atom, quant) =>
//      val m: Matcher = walk(atom, env, parenIndex)
//      val (min: Int, max: Option[Int], greedy: Boolean) = quant match {
//        /*
//         * 15.10.2.7 Quantifier
//         * Quantifier ::= QuantifierPrefix
//         *              | QuantifierPrefix ?
//         * QuantifierPrefix ::= *
//         *                    | +
//         *                    | ?
//         *                    | { DecimalDigits }
//         *                    | { DecimalDigits , }
//         *                    | { DecimalDigits , DecimalDigits }
//         */
//        case SRegExpQuantifier(_, prefix, greedy) =>
//          val (min: Int, max: Option[Int]) = prefix match {
//            case SRegExpQuantifierStar(_) => (0, None)
//            case SRegExpQuantifierPlus(_) => (1, None)
//            case SRegExpQuantifierQuest(_) => (0, Some(1))
//            case SRegExpQuantifierNumber(_, first, second) => (first, second)
//          }
//          (min, max, greedy)
//      }
//      if (max.isDefined && max.get < min) {
//        throw new SyntaxErrorException(ERegExp2_5, max.get.toString, min.toString)
//      }
//      (x: RegExpState, c: Continuation) => {
//        repeatMatcher(m, min, max, greedy, x, c, parenIndex, parenCount)
//      }
//
//    /*
//     * 15.10.2.8 Atom
//     * Atom ::= PatternCharacter
//     *        | .
//     *        | \ AtomEscape
//     *        | CharacterClass
//     *        | ( Disjunction )
//     *        | ( ? : Disjunction )
//     */
//    case SRegExpPatternChar(_, s) =>
//      val ch = s(0)
//      val a = new MHashSet[Char]
//      a += ch
//      characterSetMatcher(a, false, env, in)
//    case SRegExpDot(_) =>
//      val a = new MHashSet[Char]
//      for (ch <- lineTerminator) { a += ch }
//      characterSetMatcher(a, false, env, notin)
//
//    /*
//     * 15.10.2.9 AtomEscape
//     * AtomEscape ::= DecimalEscape
//     *              | CharacterEscape
//     *              | CharacterClassEscape
//     */
//    case node@SRegExpDecimalEscape(_, s) =>
//      val e: EscapeValue = walkDecimalEscape(node, env)
//      e match {
//        case CharEscapeValue(ch) =>
//          val a = new MHashSet[Char]
//          a += ch
//          characterSetMatcher(a, false, env, in)
//        case IntEscapeValue(n) =>
//          if (n == 0 || n > env.nCapturingParens) {
//          /*
//           * Relaxed the syntax to accept /\2/, for example.
//            if (Config.typingInterface != null) {
//              if(Shell.params.opt_DeveloperMode || !abstraction)
//                if (n.toString.equals("0"))
//                  Config.typingInterface.signal(null, RegExp2_9_1, null, null)
//                else
//                  Config.typingInterface.signal(null, RegExp2_9_2, n.toString, env.nCapturingParens.toString)
//            }
//            throw new SyntaxErrorException
//          */
//            val a = new MHashSet[Char]
//            a += n.toChar
//            characterSetMatcher(a, false, env, in)
//            } else {
//            (x: RegExpState, c: Continuation) => {
//              val cap = x.captures
//              cap(n) match {
//                case Some(s) =>
//                  val e = x.endIndex
//                  val len = s.length
//                  val f = e + len
//                  if (f > env.input.length) None
//                  else {
//                    var failure: Boolean = false
//                    for (i <- 0 until len) {
//                      if (canonicalize(s(i), env) != canonicalize(env.input(e+i), env))
//                        failure = true
//                    }
//                    if (failure) None
//                    else {
//                      val y = new RegExpState(f, cap)
//                      c(y)
//                    }
//                  }
//                case None => c(x)
//              }
//            }
//          }
//      }
//    case node@SRegExpCharacterEscape(_) =>
//      val ch: Char = walkCharacterEscape(node, env)
//      val a = new MHashSet[Char]
//      a += ch
//      characterSetMatcher(a, false, env, in)
//    case node@SRegExpCharacterClassEscape(_, s) =>
//      val (a, contains) = walkCharacterClassEscape(node, env)
//      characterSetMatcher(a, false, env, contains)
//
//    /*
//     * 15.10.2.13 CharacterClass
//     * CharacterClass ::= [ [lookahead \notin {^}] ClassRanges ]
//     *                  | [ ^ ClassRanges ]
//     */
//    case SRegExpCharacterClass(_) =>
//      val (a, invert, contains) = node match {
//        /*
//         * 15.10.2.13 CharacterClass
//         */
//        case SRegExpCharacterClassInclusion(_, r) =>
//          val (a, contains) = walkToCharSet(r, env)
//          (a, false, contains)
//        case SRegExpCharacterClassExclusion(_, r) =>
//          val (a, contains) = walkToCharSet(r, env)
//          (a, true, contains)
//      }
//      characterSetMatcher(a, invert, env, contains)
//
//    case SRegExpParen(_, d) =>
//      val m: Matcher = walk(d, env, parenIndex + 1)
//      (x: RegExpState, c: Continuation) => {
//        val d: Continuation = (y: RegExpState) => {
//          val cap = y.captures.clone()
//          val xe: Int = x.endIndex
//          val ye: Int = y.endIndex
//          val s: String = env.input.substring(xe, ye)
//          cap(parenIndex + 1) = Some(s)
//          val z: RegExpState = new RegExpState(ye, cap)
//          c(z)
//        }
//        m(x, d)
//      }
//    case SRegExpParenOpt(_, d) => walk(d, env, parenIndex)
//  }
//
//  /*
//   * 15.10.2.11 DecimalEscape
//   * DecimalEscape ::= DecimalIntegerLiteral [lookahead \notin DecimalDigit]
//   */
//  def walkDecimalEscape(node: RegExpDecimalEscape, env: RegExpEnv): EscapeValue = {
//    node.getStr.toInt match {
//      case 0 => CharEscapeValue('\u0000')
//      case n => IntEscapeValue(n)
//    }
//  }
//
//  /*
//   * 15.10.2.10 CharacterEscape
//   * CharacterEscape ::= ControlEscape
//   *                   | ControlLetter
//   *                   | HexEscapeSequence
//   *                   | UnicodeEscapeSequence
//   *                   | IdentityEscape
//   */
//  def walkCharacterEscape(node: RegExpCharacterEscape, env: RegExpEnv): Char = {
//    node match {
//      case SRegExpControlEscape(_, s) =>
//        s match {
//          case "t" => '\u0009'
//          case "n" => '\u000A'
//          case "v" => '\u000B'
//          case "f" => '\u000C'
//          case "r" => '\u000D'
//        }
//      case SRegExpControlLetter(_, s) =>
//        val ch: Char = s(0)
//        val i: Int = ch.toInt
//        val j: Int = i % 32
//        j.toChar
//      case SRegExpHexEscapeSequence(_, s) =>
//        Integer.parseInt(s.substring(1), 16).toChar
//      case SRegExpUnicodeEscapeSequence(_, s) =>
//        Integer.parseInt(s.substring(1), 16).toChar
//      case SRegExpIdentityEscape(_, s) =>
//        s(0)
//    }
//  }
//
//  /*
//   * 15.10.2.12 CharacterClassEscape
//   */
//  def walkCharacterClassEscape(node: RegExpCharacterClassEscape, env: RegExpEnv): (CharSet, (CharSet, Char) => Boolean) = {
//    node.getStr match {
//      case "d" =>
//        val a = new MHashSet[Char]
//        for (ch <- decimalDigit) a += ch
//        (a, in)
//      case "D" =>
//        val a = new MHashSet[Char]
//        for (ch <- decimalDigit) a += ch
//        (a, notin)
//      case "s" =>
//        val a = new MHashSet[Char]
//        for (ch <- space) a += ch
//        (a, in)
//      case "S" =>
//        val a = new MHashSet[Char]
//        for (ch <- space) a += ch
//        (a, notin)
//      case "w" =>
//        val a = new MHashSet[Char]
//        for (ch <- alphanumeric) a += ch
//        (a, in)
//      case "W" =>
//        val a = new MHashSet[Char]
//        for (ch <- alphanumeric) a += ch
//        (a, notin)
//    }
//  }
//
//  def characterRange(as: CharSet, bs: CharSet): (CharSet, (CharSet, Char) => Boolean) = {
//    if (as.size != 1 || bs.size != 1) {
//      // 15.10.2.15
//      // syntactically illegal rejected by the parser
//      throw new SyntaxErrorException(ESyntax, null, null)
//    } else {
//      val a: Char = as.head
//      val b: Char = bs.head
//      val i: Int = a.toInt
//      val j: Int = b.toInt
//      if (i > j) {
//        throw new SyntaxErrorException(ERegExp2_15_2, a.toString, b.toString)
//      } else {
//        val c = new MHashSet[Char]
//        for (ch <- i.toChar to j.toChar) {
//          c += ch
//        }
//        (c, in)
//      }
//    }
//  }
//
//  def walkToCharSet(node: RegExpNode, env: RegExpEnv): (CharSet, (CharSet, Char) => Boolean) = node match {
//    /*
//     * 15.10.2.14 ClassRanges
//     * ClassRanges ::= [empty]
//     *               | NonemptyClassRanges
//     */
//    case SRegExpEmptyClassRanges(_) =>
//      val a = new MHashSet[Char]
//      (a, in)
//
//    /*
//     * 15.10.2.15 NonemptyClassRanges
//     * NonemptyClassRanges ::= ClassAtom
//     *                       | ClassAtom NonemptyClassRangesNoDash
//     *                       | ClassAtom - ClassAtom ClassRanges
//     */
//    case SRegExpNonemptyClassRangesAtom(_, atom) =>
//      walkToCharSet(atom, env)
//    case SRegExpNonemptyClassRangesAtoms(_, atom, range) =>
//      val (a, aContains) = walkToCharSet(atom, env)
//      val (b, bContains) = walkToCharSet(range, env)
//      // TODO:
//      (a ++ b, aContains)
//    case SRegExpNonemptyClassRangesDash(_, atom1, atom2, range) =>
//      val (a, aContains) = walkToCharSet(atom1, env)
//      val (b, bContains) = walkToCharSet(atom2, env)
//      val (c, cContains) = walkToCharSet(range, env)
//      val (d, dContains) = characterRange(a, b)
//      // TODO:
//      (d ++ c, dContains)
//
//    /*
//     * 15.10.2.16 NonemptyClassRangesNoDash
//     * NonemptyClassRangesNoDash ::= ClassAtom
//     *                             | ClassAtomNoDash NonemptyClassRangesNoDash
//     *                             | ClassAtomNoDash - ClassAtom ClassRanges
//    case SRegExpNonemptyClassRangesNoDashAtom(_, atom) =>
//      walkToCharSet(atom, env)
//    case SRegExpNonemptyClassRangesNoDashAtomNoDash(_, atom, range) =>
//      val (a, aContains) = walkToCharSet(atom, env)
//      val (b, bContains) = walkToCharSet(range, env)
//      // TODO:
//      (a ++ b, aContains)
//    case SRegExpNonemptyClassRangesNoDashDash(_, atom1, atom2, range) =>
//      val (a, aContains) = walkToCharSet(atom1, env)
//      val (b, bContains) = walkToCharSet(atom2, env)
//      val (c, cContains) = walkToCharSet(range, env)
//      val (d, dContains) = characterRange(a, b)
//      // TODO:
//      (d ++ c, dContains)
//     */
//
//    /*
//     * 15.10.2.17 ClassAtom
//     * ClassAtom ::= -
//     *             | ClassAtomNoDash
//     */
//    case SRegExpClassAtomDash(_) =>
//      val a = new MHashSet[Char]
//      a += '-'
//      (a, in)
//
//    /*
//     * 15.10.2.18 ClassAtomNoDash
//     * ClassAtomNoDash ::= SourceCharacter but not one of \ or ] or -
//     *                   | \ ClassEscape
//     */
//    case SRegExpClassAtomNoDashCharacter(_, str) =>
//      val a = new MHashSet[Char]
//      a += str(0)
//      (a, in)
//
//    /*
//     * 15.10.2.19 ClassEscape
//     * ClassEscape ::= DecimalEscape
//     *               | b
//     *               | CharacterEscape
//     *               | CharacterClassEscape
//     */
//    case SRegExpClassDecimalEscape(_, esc) =>
//      val e: EscapeValue = walkDecimalEscape(esc, env)
//      e match {
//        case CharEscapeValue(ch) =>
//          val a = new MHashSet[Char]
//          a += ch
//          (a, in)
//        case IntEscapeValue(n) =>
//        /*
//         * Relaxed the syntax to accept /[\2]/, for example.
//          if(Shell.params.opt_DeveloperMode || !abstraction)
//            Config.typingInterface.signal(null, RegExp2_19, n.toString, null)
//          throw new SyntaxErrorException
//        */
//          val a = new MHashSet[Char]
//          a += n.toChar
//          (a, in)
//      }
//    case SRegExpClassEscapeB(_) =>
//      val a = new MHashSet[Char]
//      a += '\u0008'
//      (a, in)
//    case SRegExpClassCharacterEscape(_, esc) =>
//      val ch: Char = walkCharacterEscape(esc, env)
//      val a = new MHashSet[Char]
//      a += ch
//      (a, in)
//    case SRegExpClassCharacterClassEscape(_, esc) =>
//      walkCharacterClassEscape(esc, env)
//  }
//
//  var cacheMap = new MHashMap[(String, String), (MatchFunc, Boolean, Boolean, Boolean, Int)]
//
//  def parse(pattern: String, flags: String) : (MatchFunc, Boolean, Boolean, Boolean, Int) = {
//    cacheMap.get((pattern, flags)) match {
//      case Some(result) => result
//      case None =>
//    val abstractPattern = try {
//      RegExpParser.parsePattern(pattern, if ("".equals(Config.fileName)) "RegExp" else Config.fileName)
//    } catch {
//      case e: ParserError => {
//        // 15.10.4.1. "If the characters of P do not have the syntactic form *Pattern*,
//        // then throw a **SyntaxError** exception."
//        throw new SyntaxErrorException(ERegExp4_1_1, pattern, null)
//      }
//      case e: Throwable =>
//        throw new InternalError(e.getMessage)
//    }
//
//    val fl = flags.toList
//    if (!flags.matches("^[gim]*$") || fl.count(x => x == 'g') > 1 ||
//      fl.count(x => x == 'i') > 1 || fl.count(x => x == 'm') > 1) {
//      // 15.10.4.1. "If F contains any character other than 'g', 'i', or 'm',
//      // or if it contains the same character more then once,
//      // then throw a **SyntaxError** exception."
//      throw new SyntaxErrorException(ERegExp4_1_2, flags, null)
//    }
//
//    val nCapturingParens: Int = getParenCount(abstractPattern)
//    val b_g = flags.contains("g")
//    val b_i = flags.contains("i")
//    val b_m = flags.contains("m")
//
//    val result = abstractPattern match {
//      case SRegExpPattern(_, disjunction) =>
//        val env: RegExpEnv = new RegExpEnv("", nCapturingParens, b_g, b_i, b_m)
//        val m: Matcher = walk(disjunction, env, 0)
//        val matcher = (str: String, index: Int) => {
//          env.input = str
//          val c: Continuation = (s: RegExpState) => Some(s)
//          val cap = new MHashMap[Int, Option[String]]
//          for (i <- 1 to env.nCapturingParens) { cap(i) = None }
//          val x = new RegExpState(index, cap)
//          m(x, c)
//        }
//        (matcher, b_g, b_i, b_m, nCapturingParens)
//    }
//      cacheMap += ((pattern, flags) -> result)
//      result
//    }
//  }
//
//  def exec(matcher: MatchFunc, s: String, lastIndex: Int): (Option[Array[Option[String]]], Int, Int, Int) = {
//    val length: Int = s.length
//    var matchSucceeded: Boolean = false
//    var rr: RegExpState = null
//    var i: Int = lastIndex
//
//    while (!matchSucceeded) {
//      if (i < 0 || i > length) {
//        return (None, 0, 0, 0)
//      }
//      matcher(s, i) match {
//        case None =>
//          i += 1
//        case Some(r) =>
//          rr = r
//          matchSucceeded = true
//      }
//    }
//    val e: Int = rr.endIndex
//    val n: Int = rr.captures.size
//    val matchIndex: Int = i
//    val array = new Array[Option[String]](n+1)
//    val matchedSubstr: String = s.substring(i, e)
//    array.update(0, Some(matchedSubstr))
//    for (i <- 1 to n) {
//      array.update(i, rr.captures(i))
//    }
//    // array, lastIndex, index, length
//    (Some(array), e, matchIndex, n+1)
//  }

  // TODO
  def parse(pattern: String, flags: String) : (MatchFunc, Boolean, Boolean, Boolean, Int) = ???

  // TODO
  def exec(matcher: MatchFunc, s: String, lastIndex: Int): (Option[Array[Option[String]]], Int, Int, Int) = ???
}
