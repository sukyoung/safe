/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRNull
import kr.ac.kaist.jsaf.nodes_util.{EJSCompletionType => CT}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import scala.collection.mutable.HashMap
import kr.ac.kaist.jsaf.utils.regexp._

class JSStringPrototype(_I: Interpreter, _proto: JSObject)
  extends JSString(_I, _proto, "String", true, propTable) {
  def init(): Unit = {
    /*
     * 15.5.4 Properties of the String Prototype Object
     */
    property.put(IP.pvpn, I.IH.strProp(""))
    property.put("constructor", I.IH.objProp(I.IS.StringConstructor))
    property.put("toString", I.IH.objProp(I.IS.StringPrototypeToString))
    property.put("valueOf", I.IH.objProp(I.IS.StringPrototypeValueOf))
    property.put("charAt", I.IH.objProp(I.IS.StringPrototypeCharAt))
    property.put("charCodeAt", I.IH.objProp(I.IS.StringPrototypeCharCodeAt))
    property.put("concat", I.IH.objProp(I.IS.StringPrototypeConcat))
    // 15.5.4.7
    // 15.5.4.8
    // 15.5.4.9
    property.put("match", I.IH.objProp(I.IS.StringPrototypeMatch))
    property.put("replace", I.IH.objProp(I.IS.StringPrototypeReplace))
    // 15.5.4.12
    property.put("slice", I.IH.objProp(I.IS.StringPrototypeSlice))
    property.put("split", I.IH.objProp(I.IS.StringPrototypeSplit))
    property.put("substring", I.IH.objProp(I.IS.StringPrototypeSubstring))
    property.put("toLowerCase", I.IH.objProp(I.IS.StringPrototypeToLowerCase))
    // 15.5.4.17
    // 15.5.4.18
    // 15.5.4.19
    // 15.5.4.20
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.StringPrototypeToString => _toString()
      case I.IS.StringPrototypeValueOf => _valueOf()
      case I.IS.StringPrototypeCharAt => _charAt(argsObj._get("0"))
      case I.IS.StringPrototypeCharCodeAt => _charCodeAt(argsObj._get("0"))
      case I.IS.StringPrototypeConcat => _concat(I.IH.arrayToList(argsObj))
      // 15.5.4.7
      // 15.5.4.8
      // 15.5.4.9
      case I.IS.StringPrototypeMatch => _match(argsObj._get("0"))
      case I.IS.StringPrototypeReplace => _replace(argsObj._get("0"), argsObj._get("1"))
      // 15.5.4.12
      case I.IS.StringPrototypeSlice => _slice(argsObj._get("0"), argsObj._get("1"))
      case I.IS.StringPrototypeSplit => _split(argsObj._get("0"), argsObj._get("1"))
      case I.IS.StringPrototypeSubstring => _substring(argsObj._get("0"), argsObj._get("1"))
      case I.IS.StringPrototypeToLowerCase => _toLowerCase()
      // 15.5.4.17
      // 15.5.4.18
      // 15.5.4.19
      // 15.5.4.20
    }
  }

  def _toString(): Unit = {
    // Equivalent to valueOf
    I.IH.toObject(I.IS.tb) match {
      case o: JSString =>
        I.IS.comp.setReturn(o._get(IP.pvpn))
      case o: JSObject =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _valueOf(): Unit = {
    // Equivalent to toString
    I.IH.toObject(I.IS.tb) match {
      case o: JSString => I.IS.comp.setReturn(o._get(IP.pvpn))
      case o: JSObject => I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _charAt(pos: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val position: Int = I.IH.toUint32(pos).toInt
        val size: Int = s.size
        if (position < 0 || position >= size)
          I.IS.comp.setReturn(PVal(I.IH.mkIRStr("")))
        else
          I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s.substring(position, position + 1))))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _charCodeAt(pos: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val position: Int = I.IH.toUint32(pos).toInt
        val size: Int = s.size
        if (position < 0 || position >= size)
          I.IS.comp.setReturn(PVal(IP.NaN))
        else
          I.IS.comp.setReturn(PVal(I.IH.mkIRNum(s(position).toInt)))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _concat(args: List[Val]): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val r: String = s + args.map((v: Val) => I.IH.toString(v)).mkString
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(r)))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  // _replace method has a code clone of _match method.
  def _match(regexp: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val rx: JSRegExp = regexp match {
          case r: JSRegExp => r
          case _ =>
            I.IS.RegExpConstructor.construct(regexp, IP.undefV)
        }
        val global: Boolean = I.IH.toBoolean(rx._get("global"))
        if (!global) {
          // TODO: Env?
          val oldTb: Val = I.IS.tb
          I.IS.tb = rx
          val result: Unit = I.IS.RegExpPrototype._exec(PVal(I.IH.mkIRStr(s)))
          I.IS.tb = oldTb
          result
        } else {
          // TODO:
          rx._put("lastIndex", PVal(I.IH.mkIRNum(0)), true)
          val a: JSArray = I.IS.ArrayConstructor.construct(Nil)
          var previousLastIndex: Int = 0
          var n: Int = 0
          var lastMatch: Boolean = true
          while (lastMatch) {
            // TODO: Env?
            val oldTb: Val = I.IS.tb
            I.IS.tb = rx
            I.IS.RegExpPrototype._exec(PVal(I.IH.mkIRStr(s)))
            if(I.IS.comp.Type == CT.RETURN) {
              I.IS.comp.value match {
                case PVal(_: IRNull) =>
                  I.IS.tb = oldTb
                  lastMatch = false
                case result: JSArray =>
                  I.IS.tb = oldTb
                  val thisIndex: Int = I.IH.toUint32(rx._get("lastIndex")).toInt
                  if (thisIndex == previousLastIndex) {
                    // TODO:
                    rx._put("lastIndex", PVal(I.IH.mkIRNum(thisIndex + 1)), true)
                    previousLastIndex = thisIndex + 1
                  } else {
                    previousLastIndex = thisIndex
                  }
                  val matchStr: Val = result._get("0")
                  a._defineOwnProperty(n.toString,
                                       I.IH.mkDataProp(matchStr, true, true, true),
                                       true)
                  n += 1
                case _ =>
                  I.IS.tb = oldTb
                  return
              }
            }
            else {
              I.IS.tb = oldTb
              return
            }
          }
          if (n == 0)
            I.IS.comp.setReturn(IP.nullV)
          else
            I.IS.comp.setReturn(a)
        }
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  // _replace method has a code clone of _match method.
  def _replace(searchValue: Val, replaceValue: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val (sr: List[(Int, JSArray)], m: Int) = searchValue match {
          case sv: JSRegExp =>
            if (!I.IH.toBoolean(sv._get("global"))) {
              sv.property.put("global", I.IH.boolProp(true))
              // TODO: Env?
              val oldTb: Val = I.IS.tb
              I.IS.tb = sv
              I.IS.RegExpPrototype._exec(PVal(I.IH.mkIRStr(s)))
              if(I.IS.comp.Type == CT.RETURN) {
                I.IS.comp.value match {
                  case PVal(_: IRNull) =>
                    I.IS.tb = oldTb
                    sv._put("lastIndex", IP.plusZeroV, true)
                    sv.property.put("global", I.IH.boolProp(false))
                    (Nil, sv.nCapturingParens)
                  case result: JSArray =>
                    I.IS.tb = oldTb
                    val thisIndex: Int = I.IH.toUint32(sv._get("lastIndex")).toInt
                    sv._put("lastIndex", IP.plusZeroV, true)
                    sv.property.put("global", I.IH.boolProp(false))
                    (List((thisIndex, result)), sv.nCapturingParens)
                  case _ =>
                    I.IS.tb = oldTb
                    return
                }
              }
              else {
                I.IS.tb = oldTb
                return
              }
            } else {
              // TODO:
              sv._put("lastIndex", PVal(I.IH.mkIRNum(0)), true)
              var a: List[(Int, JSArray)] = Nil
              var previousLastIndex: Int = 0
              var lastMatch: Boolean = true
              while (lastMatch) {
                // TODO: Env?
                val oldTb: Val = I.IS.tb
                I.IS.tb = sv
                I.IS.RegExpPrototype._exec(PVal(I.IH.mkIRStr(s)))
                if(I.IS.comp.Type == CT.RETURN) {
                  I.IS.comp.value match {
                    case PVal(_: IRNull) =>
                      I.IS.tb = oldTb
                      lastMatch = false
                    case result: JSArray =>
                      I.IS.tb = oldTb
                      val thisIndex: Int = I.IH.toUint32(sv._get("lastIndex")).toInt
                      if (thisIndex == previousLastIndex) {
                        // TODO: {FireFox, IE, Safari, Chrome} do not update lastIndex here.
                        sv._put("lastIndex", PVal(I.IH.mkIRNum(thisIndex + 1)), true)
                        previousLastIndex = thisIndex + 1
                      } else {
                        previousLastIndex = thisIndex
                      }
                      a ::= (thisIndex, result)
                    case _ =>
                      I.IS.tb = oldTb
                      return
                  }
                }
                else {
                  I.IS.tb = oldTb
                  return
                }
              }
              (a.reverse, sv.nCapturingParens)
            }
          case _ =>
            val searchString: String = I.IH.toString(searchValue)
            val matchStr: Val = PVal(I.IH.mkIRStr(searchString))
            val result: JSArray = I.IS.ArrayConstructor.construct(List(matchStr))
            val thisIndex: Int = s.indexOf(searchString)
            if (thisIndex < 0) (Nil, 0) else (List((thisIndex + searchString.length, result)), 0)
        }
        replaceValue match {
          case fun: JSFunction =>
            var replaced: String = ""
            var lastEndIndex: Int = 0
            for ((index, a) <- sr) {
              val matchStr: String = I.IH.toString(a._get("0"))
              replaced += s.substring(lastEndIndex, index - matchStr.length)
              lastEndIndex = index
              // TODO: Settings for data property
              a._defineOwnProperty((m+1).toString,
                                   I.IH.mkDataProp(PVal(I.IH.mkIRNum(index)), true, true, true),
                                   true)
              a._defineOwnProperty((m+2).toString,
                                   I.IH.mkDataProp(PVal(I.IH.mkIRStr(s)), true, true, true),
                                   true)
              val argsList: JSObject = I.IH.newArgObj(fun, fun.code.getArgs.size, a, I.IS.strict)
              var rs: String = null
              fun._call(I.IS.GlobalObject, argsList)
              if(I.IS.comp.Type == CT.RETURN) rs = I.IH.toString(I.IS.comp.value)
              else return
              replaced += rs
            }
            replaced += s.substring(lastEndIndex)
            I.IS.comp.setReturn(PVal(I.IH.mkIRStr(replaced)))
          case _ =>
            val newstring: String = I.IH.toString(replaceValue)
            val len: Int = newstring.length
            var replaced: String = ""
            var lastEndIndex: Int = 0
            for ((index, a) <- sr) {
              val matchStr: String = I.IH.toString(a._get("0"))
              replaced += s.substring(lastEndIndex, index - matchStr.length)
              lastEndIndex = index
              var i = 0
              while (i < len) {
                if (i+2 <= len && newstring.substring(i, i+2) == "$$") {
                  replaced += '$'
                  i += 2
                } else if (i+2 <= len && newstring.substring(i, i+2) == "$&") {
                  replaced += matchStr
                  i += 2
                } else if (i+2 <= len && newstring.substring(i, i+2) == "$`") {
                  replaced += s.substring(0, index)
                  i += 2
                } else if (i+2 <= len && newstring.substring(i, i+2) == "$'") {
                  replaced += s.substring(index)
                  i += 2
                } else if (i+3 <= len && newstring(i) == '$' &&
                           '0' <= newstring(i+1) && newstring(i+1) <= '9' &&
                           '0' <= newstring(i+2) && newstring(i+2) <= '9' &&
                           newstring.substring(i+1, i+3) != "00") {
                  val v: Val = a._get(newstring.substring(i+1, i+3))
                  val rs: String = if (I.IH.isUndef(v)) "" else I.IH.toString(v)
                  replaced += rs
                  i += 3
                } else if (i+2 <= len && newstring(i) == '$' &&
                           '1' <= newstring(i+1) && newstring(i+1) <= '9') {
                  val v: Val = a._get(newstring.substring(i+1, i+2))
                  val rs: String = if (I.IH.isUndef(v)) "" else I.IH.toString(v)
                  replaced += rs
                  i += 2
                } else {
                  replaced += newstring(i)
                  i += 1
                }
              }
            }
            replaced += s.substring(lastEndIndex)
            I.IS.comp.setReturn(PVal(I.IH.mkIRStr(replaced)))
        }
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _slice(start: Val, end: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val len: Int = s.size
        val intStart: Int = I.IH.toUint32(start).toInt
        val intEnd: Int = if (I.IH.isUndef(end)) len
                          else I.IH.toUint32(end).toInt
        val from: Int = if (intStart < 0) (len + intStart) max 0
                        else intStart min len
        val to: Int = if (intEnd < 0) (len + intEnd) max 0
                      else intEnd min len
        val span: Int = (to - from) max 0
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s.substring(from, from + span))))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _split(separator: Val, limit: Val): Unit = {
    def splitMatch(s: String, q: Int, r: Any): MatchResult = {
      r match {
        case r:JSRegExp => r._match(s, q)
        case r:String =>
          val rl: Int = r.length
          val sl: Int = s.length
          if (q + rl > sl) None
          else {
            var ok: Boolean = true
            for (i <- 0 until rl if s(q+i) != r(i)) { ok = false }
            if (!ok) None
            else {
              val cap = new HashMap[Int, Option[String]]
              Some(new RegExpState(q + rl, cap))
            }
          }
      }
    }
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val str: String = I.IH.toString(I.IS.tb)
        val a: JSArray = I.IS.ArrayConstructor.construct(Nil)
        var lengthA: Int = 0
        // TODO:
        val lim: Int = if (I.IH.isUndef(limit)) 0x7fffffff else I.IH.toUint32(limit).toInt
        val s: Int = str.length
        var p: Int = 0
        val r = separator match {
          case r: JSRegExp => r
          case _ => I.IH.toString(separator)
        }
        if (lim == 0) I.IS.comp.setReturn(a)
        else if (I.IH.isUndef(separator)) {
          a._defineOwnProperty("0",
                               I.IH.mkDataProp(PVal(I.IH.mkIRStr(str)), true, true, true),
                               false)
          I.IS.comp.setReturn(a)
        } else if (s == 0) {
          val z: MatchResult = splitMatch(str, 0, r)
          z match {
            case Some(y) => I.IS.comp.setReturn(a)
            case None =>
              a._defineOwnProperty("0",
                                   I.IH.mkDataProp(PVal(I.IH.mkIRStr(str)), true, true, true),
                                   false)
              I.IS.comp.setReturn(a)
          }
        } else {
          var q: Int = p
          while (q != s) {
            val z: MatchResult = splitMatch(str, q, r)
            z match {
              case Some(z) =>
                val (e, cap) = (z.endIndex, z.captures)
                if (e == p) q += 1
                else {
                  val t: String = str.substring(p, q)
                  a._defineOwnProperty(lengthA.toString,
                                       I.IH.mkDataProp(PVal(I.IH.mkIRStr(t)), true, true, true),
                                       false)
                  lengthA += 1
                  if (lengthA == lim) return I.IS.comp.setReturn(a)
                  p = e
                  var i: Int = 0
                  while (i != cap.size) {
                    i += 1
                    val capI: Val = cap(i) match {
                      case Some(s) => PVal(I.IH.mkIRStr(s))
                      case None => IP.undefV
                    }
                    a._defineOwnProperty(lengthA.toString,
                                         I.IH.mkDataProp(capI, true, true, true),
                                         false)
                    lengthA += 1
                    if (lengthA == lim) return I.IS.comp.setReturn(a)
                  }
                  q = p
                }
              case None => q += 1
            }
          }
          val t: String = str.substring(p, s)
          a._defineOwnProperty(lengthA.toString,
                               I.IH.mkDataProp(PVal(I.IH.mkIRStr(t)), true, true, true),
                               false)
          I.IS.comp.setReturn(a)
        }
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _substring(start: Val, end: Val): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        val len: Int = s.size
        val intStart: Int = I.IH.toUint32(start).toInt
        val intEnd: Int = if (I.IH.isUndef(end)) len
                          else I.IH.toUint32(end).toInt
        val finalStart: Int = (intStart max 0) min len
        val finalEnd: Int = (intEnd max 0) min len
        val from: Int = finalStart min finalEnd
        val to: Int = finalStart max finalEnd
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s.substring(from, to))))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _toLowerCase(): Unit = {
    I.IH.checkObjectCoercible(I.IS.tb) match {
      case v:Val =>
        val s: String = I.IH.toString(I.IS.tb)
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s.toLowerCase)))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }
}
