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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.{ Helper, TypeConversionHelper }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.HashSet

// 15.10 RegExp
object BuiltinRegExp extends FuncModel(
  name = "RegExp",
  // TODO 15.10.3.1 RegExp(pattern, flags)
  code = BasicCode(
    argLen = 2,
    asiteSet = HashSet(BuiltinRegExpHelper.instanceASite),
    code = BuiltinRegExpHelper.function
  ),
  // TODO 15.10.4.1 new RegExp(pattern, flags)
  construct = Some(BasicCode(
    argLen = 2,
    asiteSet = HashSet(BuiltinRegExpHelper.instanceASite),
    code = BuiltinRegExpHelper.construct
  )),
  protoModel = Some((BuiltinRegExpProto, F, F, F))
)

object BuiltinRegExpProto extends ObjModel(
  name = "RegExp.prototype",
  props = List(
    InternalProp(IClass, PrimModel("RegExp")),

    // TODO exec
    NormalProp("exec", FuncModel(
      name = "RegExp.prototype.exec",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO test
    NormalProp("test", FuncModel(
      name = "RegExp.prototype.test",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "RegExp.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  )
)

private object BuiltinRegExpHelper {
  val instanceASite = PredAllocSite("RegExp<instance>")

  def newREObject(source: AbsStr, g: AbsBool, i: AbsBool, m: AbsBool): AbsObj = {
    val F = AbsBool.False
    AbsObj.Empty
      .update(IClass, AbsIValue(AbsStr("RegExp")))
      .update(IPrototype, AbsIValue(BuiltinRegExpProto.loc))
      .update(IExtensible, AbsIValue(AbsBool.True))
      .update("source", AbsDataProp(source, F, F, F))
      .update("global", AbsDataProp(g, F, F, F))
      .update("ignoreCase", AbsDataProp(i, F, F, F))
      .update("multiline", AbsDataProp(m, F, F, F))
      .update("lastIndex", AbsDataProp(AbsNum(0.0), AbsBool.True, F, F))
  }

  // 15.10.3.1 RegExp(pattern, flags)
  def function(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val heap = st.heap
    val pattern = Helper.propLoad(args, HashSet(AbsStr("0")), heap)
    val flags = Helper.propLoad(args, HashSet(AbsStr("1")), heap)

    // case for pattern is undefined.
    val pattern1 =
      if (pattern.pvalue.undefval !⊑ AbsUndef.Bot) AbsStr("")
      else AbsStr.Bot
    // case for flags is undefined.
    val flags1 =
      if (flags.pvalue.undefval !⊑ AbsUndef.Bot) AbsStr("")
      else AbsStr.Bot

    // case for pattern is an object whose [[class]] is RegExp.
    val aRegExp = AbsStr("RegExp")
    val locSetRE = pattern.locset.filter(loc => aRegExp ⊑ heap.get(loc)(IClass).value)
    // case for pattern is an object whose [[class]] is not a RegExp
    val locSetNotRE = pattern.locset.filter(loc => {
      val aclass = heap.get(loc)(IClass).value.pvalue.strval
      aRegExp != aclass && aclass !⊑ AbsStr.Bot
    })

    // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
    val notUndefPattern = AbsValue(pattern.pvalue.copy(undefval = AbsUndef.Bot), locSetNotRE)
    val pattern3 = TypeConversionHelper.ToString(notUndefPattern, heap)
    // case for flags is a value which is not an undefined or an object.
    val notUndefFlags = AbsValue(flags.pvalue.copy(undefval = AbsUndef.Bot), flags.locset)
    val flags3 = TypeConversionHelper.ToString(notUndefFlags, heap)

    // If pattern is an object R whose [[Class] internal property is "RegExp" and
    // flags is not undefined, then throw a "TypeError" exception.
    val excSet1 =
      if (!locSetRE.isBottom && notUndefFlags !⊑ AbsValue.Bot) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // case for pattern is a value or an object whose [[class]] is not a RegExp.
    val P = pattern1 ⊔ pattern3
    // case for flags is a value or an object.
    val F = flags1 ⊔ flags3

    val (objOpt, excSet2) =
      (P.gamma, F.gamma) match {
        case (ConFin(patternSet), ConFin(flagsSet)) if patternSet.nonEmpty && flagsSet.nonEmpty =>
          val (obj, exc) = patternSet.foldLeft((AbsObj.Bot, ExcSetEmpty))((tpl1, spattern) => {
            flagsSet.foldLeft(tpl1)((tpl2, sflags) => {
              val (aR, excSet) = tpl2
              val pattern: String =
                if (spattern == Str("")) "(?:)"
                else spattern
              // TODO: Implement parsePattern, parseFlag
              // val (source, excSet3) = parsePattern(spattern)
              // val (g, i, m, excSet4) = parseFlag(sflags)
              val newR = newREObject(AbsStr(pattern), AbsBool.Top, AbsBool.Top, AbsBool.Top)
              (aR ⊔ newR, excSet)
            })
          })
          (Some(obj), exc)
        case _ if P !⊑ AbsStr.Bot && F !⊑ AbsStr.Bot =>
          (Some(newREObject(P, AbsBool.Top, AbsBool.Top, AbsBool.Top)), ExcSetEmpty + SyntaxError)
        case _ => (None, ExcSetEmpty)
      }

    val (st2, result1) = objOpt match {
      case Some(obj) =>
        val loc = Loc(instanceASite)
        val st1 = st.oldify(loc)
        val h2 = st1.heap.update(loc, obj)
        (AbsState(h2, st1.context), AbsValue(loc))
      case None => (st, AbsValue.Bot)
    }

    val result2 =
      if (!locSetRE.isBottom && flags.pvalue.undefval !⊑ AbsUndef.Bot) AbsValue(locSetRE)
      else AbsValue.Bot

    (st2, st.raiseException(excSet1 ++ excSet2), result1 ⊔ result2)
  }

  // 15.10.4.1 new RegExp(pattern, flags)
  def construct(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val heap = st.heap
    val pattern = Helper.propLoad(args, HashSet(AbsStr("0")), heap)
    val flags = Helper.propLoad(args, HashSet(AbsStr("1")), heap)

    // case for pattern is undefined.
    val pattern1 =
      if (pattern.pvalue.undefval !⊑ AbsUndef.Bot) AbsStr("")
      else AbsStr.Bot
    // case for flags is undefined.
    val flags1 =
      if (flags.pvalue.undefval !⊑ AbsUndef.Bot) AbsStr("")
      else AbsStr.Bot

    // case for pattern is an object whose [[class]] is RegExp.
    val aRegExp = AbsStr("RegExp")
    val locSetRE = pattern.locset.filter(loc => aRegExp ⊑ heap.get(loc)(IClass).value)
    // case for pattern is an object whose [[class]] is not a RegExp
    val locSetNotRE = pattern.locset.filter(loc => {
      val aclass = heap.get(loc)(IClass).value.pvalue.strval
      aRegExp != aclass && aclass !⊑ AbsStr.Bot
    })

    // If pattern is an object R whose [[Class]] internal property is "RegExp" and flags is undefined,
    // then let P be the pattern used to construct R and let F be the flags used to construct R.
    val (pattern2, flags2) =
      if (!locSetRE.isBottom && flags.pvalue.undefval !⊑ AbsUndef.Bot) (AbsStr.Top, AbsStr.Top)
      else (AbsStr.Top, AbsStr.Top)

    // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
    val notUndefPattern = AbsValue(pattern.pvalue.copy(undefval = AbsUndef.Bot), locSetNotRE)
    val pattern3 = TypeConversionHelper.ToString(notUndefPattern, heap)
    // case for flags is a value which is not an undefined or an object.
    val notUndefFlags = AbsValue(flags.pvalue.copy(undefval = AbsUndef.Bot), flags.locset)
    val flags3 = TypeConversionHelper.ToString(notUndefFlags, heap)

    // If pattern is an object R whose [[Class] internal property is "RegExp" and
    // flags is not undefined, then throw a "TypeError" exception.
    val excSet1 =
      if (!locSetRE.isBottom && notUndefFlags !⊑ AbsValue.Bot) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // case for pattern is a value or an object whose [[class]] is not a RegExp.
    val P = pattern1 ⊔ pattern2 ⊔ pattern3
    // case for flags is a value or an object.
    val F = flags1 ⊔ flags2 ⊔ flags3

    val (objOpt, excSet2) =
      (P.gamma, F.gamma) match {
        case (ConFin(patternSet), ConFin(flagsSet)) if patternSet.nonEmpty && flagsSet.nonEmpty =>
          val (obj, exc) = patternSet.foldLeft((AbsObj.Bot, ExcSetEmpty))((tpl1, spattern) => {
            flagsSet.foldLeft(tpl1)((tpl2, sflags) => {
              val (aR, excSet) = tpl2
              val pattern: String =
                if (spattern == Str("")) "(?:)"
                else spattern
              // TODO: Implement parsePattern, parseFlag
              // val (source, excSet3) = parsePattern(spattern)
              // val (g, i, m, excSet4) = parseFlag(sflags)
              val newR = newREObject(AbsStr(pattern), AbsBool.Top, AbsBool.Top, AbsBool.Top)
              (aR ⊔ newR, excSet)
            })
          })
          (Some(obj), exc)
        case _ if P !⊑ AbsStr.Bot && F !⊑ AbsStr.Bot =>
          (Some(newREObject(P, AbsBool.Top, AbsBool.Top, AbsBool.Top)), ExcSetEmpty + SyntaxError)
        case _ => (None, ExcSetEmpty)
      }

    val (st2, result1) = objOpt match {
      case Some(obj) =>
        val loc = Loc(instanceASite)
        val st1 = st.oldify(loc)
        val h2 = st1.heap.update(loc, obj)
        (AbsState(h2, st1.context), AbsValue(loc))
      case None => (st, AbsValue.Bot)
    }

    (st2, st.raiseException(excSet1 ++ excSet2), result1)
  }
}
