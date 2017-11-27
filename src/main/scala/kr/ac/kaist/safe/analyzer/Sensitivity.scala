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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._

// analysis sensitivity
sealed abstract class Sensitivity {
  val initTP: TracePartition
  def *(other: Sensitivity): Sensitivity = (this, other) match {
    case (NoSensitivity, _) => other
    case (_, NoSensitivity) => this
    case _ => ProductSensitivity(this, other)
  }
}

// trace partition
abstract class TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): TracePartition
}

////////////////////////////////////////////////////////////////////////////////
// no analsis sensitivity
////////////////////////////////////////////////////////////////////////////////
object NoSensitivity extends Sensitivity {
  val initTP = EmptyTP
}
case object EmptyTP extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType
  ): EmptyTP.type = EmptyTP
  override def toString: String = s"Empty"
}

////////////////////////////////////////////////////////////////////////////////
// product of analysis sensitivities
////////////////////////////////////////////////////////////////////////////////
case class ProductTP(
    ltp: TracePartition,
    rtp: TracePartition
) extends TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): ProductTP =
    ProductTP(ltp.next(from, to, edgeType), rtp.next(from, to, edgeType))
  override def toString: String = s"$ltp x $rtp"
}

case class ProductSensitivity(
    lsens: Sensitivity,
    rsens: Sensitivity
) extends Sensitivity {
  val initTP = ProductTP(lsens.initTP, rsens.initTP)
}

////////////////////////////////////////////////////////////////////////////////
// call-site sensitivity
////////////////////////////////////////////////////////
case class CallSiteContext(callsiteList: List[Call], depth: Int) extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType
  ): CallSiteContext = (from, to, edgeType) match {
    case (call: Call, _: Entry, CFGEdgeCall) =>
      CallSiteContext((call :: callsiteList).take(depth), depth)
    case _ => this
  }
  override def toString: String = callsiteList
    .map(call => s"${call.func.id}:${call.id}")
    .mkString("Call[", ", ", "]")
}

class CallSiteSensitivity(depth: Int) extends Sensitivity {
  val initTP = CallSiteContext(Nil, depth)
}
object CallSiteSensitivity {
  def apply(depth: Int): Sensitivity = depth match {
    case 0 => NoSensitivity
    case n => new CallSiteSensitivity(depth)
  }
}

////////////////////////////////////////////////////////////////////////////////
// loop sensitivity (unrolling)
////////////////////////////////////////////////////////////////////////////////
case class LoopInfo(loopHead: LoopHead, k: Int, outer: LoopContext) {
  def nextIter(depth: Int): LoopInfo = copy(k = Math.min(k + 1, depth))
}

case class LoopContext(
    infoOpt: Option[LoopInfo],
    excOuter: Option[(LoopContext, NormalBlock)],
    depth: Int
) extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType
  ): LoopContext = (from, to, edgeType) match {
    // function call
    case (_, _: Entry, CFGEdgeCall) =>
      LoopContext(None, None, depth)
    // try start
    case (_, tryB @ NormalBlock(_, TryLabel), CFGEdgeNormal) =>
      copy(excOuter = Some(this, tryB))
    // catch
    case (_, NormalBlock(_, CatchLabel), _) => excOuter match {
      case Some((e, _)) => e
      case None => this
    }
    // catch
    case (_, NormalBlock(_, FinallyLabel(tryBlock)), _) =>
      excOuter match {
        case Some((e, tb)) if tb == tryBlock => e
        case _ => this
      }
    // loop
    case (_, loopHead: LoopHead, CFGEdgeNormal) => infoOpt match {
      // loop iteration
      case Some(info @ LoopInfo(l, k, outer)) if l == loopHead =>
        LoopContext(Some(info.nextIter(depth)), excOuter, depth)
      // loop start
      case _ =>
        LoopContext(Some(LoopInfo(loopHead, 0, this)), excOuter, depth)
    }
    // loop break
    case (_, NormalBlock(_, LoopBreakLabel), _) => infoOpt match {
      case Some(LoopInfo(_, _, outer)) => outer
      case None => this
    }
    // exception / return
    case (_, ExitExc(_) | Exit(_), _) => LoopContext(None, None, depth)
    case _ => this
  }
  override def toString: String = infoOpt match {
    case Some(LoopInfo(loopHead, k, outer)) =>
      s"Loop($loopHead, $k/$depth)"
    case None => s"NoLoop"
  }
  override def equals(other: Any): Boolean = other match {
    case LoopContext(oInfoOpt, _, oDepth) if depth == oDepth =>
      (infoOpt, oInfoOpt) match {
        case (None, None) => true
        case (Some(LoopInfo(llh, lk, louter)), Some(LoopInfo(rlh, rk, router))) =>
          llh == rlh && lk == rk && louter == router
        case _ => false
      }
    case _ => false
  }
}

class LoopSensitivity(depth: Int) extends Sensitivity {
  val initTP = LoopContext(None, None, depth)
}
object LoopSensitivity {
  def apply(depth: Int): Sensitivity = depth match {
    case 0 => NoSensitivity
    case n => new LoopSensitivity(depth)
  }
}
