/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMObject

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeling based on the WHATWG Living Standard 
// Section 2.7.2.1 HTMLAllCollection
object HTMLAllCollection extends DOM {
  private val name = "HTMLAllCollection"

  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor */
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("item",      AbsBuiltinFunc("HTMLAllCollection.item", 1)),
    ("namedItem", AbsBuiltinFunc("HTMLAllCollection.namedItem", 1)),
    ("tags", AbsBuiltinFunc("HTMLAllCollection.tags", 1))
  )

  /* global */

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("HTMLAllCollection.item" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_index </ NumBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            val n_length = lset_this.foldLeft[AbsNumber](NumBot)((n, l) =>
              n + Helper.toNumber(Helper.toPrimitive_better(h, Helper.Proto(h, l, AbsString.alpha("length")))))
            val s_index = Helper.toString(PValue(n_index))
            // Returns the indexth item in the collection.
            val v_return = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, s_index))
            // If index is greater than or equal to the number of nodes in the list, this returns null.
            val v_null = AbsDomUtils.checkIndex(n_index, n_length)
            ((Helper.ReturnStore(h, v_return + v_null), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("HTMLAllCollection.tags" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* imprecise modeling */
         val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)

          /* arguments */
          val tagname = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (tagname </ StrBot) {
            val lset = DOMHelper.findByTag(h_1, tagname)
            val proplist = getInsList(0) 
            val obj = proplist.foldLeft(Obj.empty)((o, p) => o.update(p._1, p._2))
            val newobj = if(lset.size==0) obj
                         else if(lset.size==1) {
                           obj.update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T))).update(
                                       "0", PropValue(ObjectValue(Value(lset), T, T, T)))
                         }
                         else {
                           obj.update("length", PropValue(ObjectValue(Value(UInt), F, T, T))).update(
                                       NumStr, PropValue(ObjectValue(Value(lset), T, T, T)))
                         }
            val h_2 = h_1.update(l_r, newobj)
            ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))

      //case "HTMLCollection.namedItem" => ((h,ctx),(he,ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      ("HTMLAllCollection.item" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_index = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (n_index </ NumBot) {
            val lset_this = h(PureLocalLoc)("@this")._2._2
            val n_length = lset_this.foldLeft[AbsNumber](NumBot)((n, l) =>
              n + PreHelper.toNumber(PreHelper.toPrimitive(PreHelper.Proto(h, l, AbsString.alpha("length")))))
            val s_index = PreHelper.toString(PValue(n_index))
            // Returns the indexth item in the collection.
            val v_return = lset_this.foldLeft(ValueBot)((v, l) => v + PreHelper.Proto(h, l, s_index))
            // If index is greater than or equal to the number of nodes in the list, this returns null.
            val v_null = AbsDomUtils.checkIndex(n_index, n_length)
            ((PreHelper.ReturnStore(h, PureLocalLoc, v_return + v_null), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        }))
      //case "HTMLCollection.namedItem" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("HTMLAllCollection.item" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
      //case "HTMLCollection.namedItem" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("HTMLAllCollection.item" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if (n_index </ NumBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l) =>
              lpset ++ AccessHelper.Proto_use(h, l, AbsString.alpha("length")))
            val s_index = Helper.toString(PValue(n_index))
            // Returns the indexth item in the collection.
            val LP3 = lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AccessHelper.Proto_use(h, l, s_index))
            LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
          }
          else
            LP1
        }))
      //case "HTMLCollection.namedItem" => ((h,ctx),(he,ctxe))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  def getInsList(length: Int): List[(String, PropValue)] = List(
    ("@class",  PropValue(AbsString.alpha("Object"))),
    ("@proto",  PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("length",   PropValue(ObjectValue(AbsNumber.alpha(length), F, F, F)))
  )
}
