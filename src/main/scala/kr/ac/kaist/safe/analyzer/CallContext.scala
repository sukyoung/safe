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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain.{ Address, Heap, Loc, Obj }
import kr.ac.kaist.safe.cfg_builder.AddressManager
import kr.ac.kaist.safe.nodes.{ CFG, FunctionId }

import scala.collection.immutable.HashSet

import scala.util.{ Try, Failure, Success }

abstract class CallContext {
  def newCallContext(h: Heap, cfg: CFG, calleeFid: FunctionId, scopeLoc: Loc, thisLocSet: Set[Loc],
    newPureLocalObj: Obj, l2: Option[Address] = None): Set[(CallContext, Obj)] =
    newCallContext(h, cfg, calleeFid, scopeLoc, thisLocSet, newPureLocalObj)
  def newCallContext(h: Heap, cfg: CFG, calleeFid: FunctionId, scopeLoc: Loc,
    thisLocSet: Set[Loc], newPureLocalObj: Obj): Set[(CallContext, Obj)]
  def compare(that: CallContext): Try[Int]
}

/* Interface */
case class CallContextManager(addrManager: AddressManager, callsiteDepth: Int = 0) {
  val globalCallContext: CallContext = KCallsite(callsiteDepth, List[Address]())

  private case class KCallsite(depth: Int, callsiteList: List[Address]) extends CallContext {
    def newCallContext(h: Heap, cfg: CFG, calleeFid: FunctionId, scopeLoc: Loc,
      thisLocSet: Set[Loc], newPureLocalObj: Obj): Set[(CallContext, Obj)] = {
      val k: Int =
        cfg.funMap.get(calleeFid) match {
          case Some(fun) if fun.isUser => depth
          case _ => depth + 1 // additional depth for built-in calls.
        }
      val newCallsiteList = (addrManager.locToAddr(scopeLoc) :: this.callsiteList).take(k)
      HashSet((KCallsite(depth, newCallsiteList), newPureLocalObj))
    }

    private def compareList(x: List[Address], y: List[Address]): Int =
      (x, y) match {
        case (Nil, Nil) => 0
        case (Nil, _) => -1
        case (_, Nil) => 1
        case (x1 :: xs, y1 :: ys) if x1 == y1 => compareList(xs, ys)
        case (x1 :: xs, y1 :: ys) => x1 - y1
      }

    def compare(other: CallContext): Try[Int] =
      other match {
        case that: KCallsite => Success(compareList(this.callsiteList, that.callsiteList))
        case _ => Failure(new InternalError("compare must be called on same CallContext kinds"))
      }

    override def toString: String = callsiteList.toString
  }
}