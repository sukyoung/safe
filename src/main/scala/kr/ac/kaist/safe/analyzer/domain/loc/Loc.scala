/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.TracePartition
import kr.ac.kaist.safe.nodes.cfg.{ CFG, Call }
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.PipeOps._
import scala.collection.immutable.HashSet
import scala.util.{ Try, Success, Failure }

// concrete location type
abstract class Loc extends Value {
  def isUser: Boolean = this match {
    case Recency(loc, _) => loc.isUser
    case TraceSensLoc(loc, _) => loc.isUser
    case AllocCallSite(loc, _) => loc.isUser
    case UserAllocSite(_) => true
    case PredAllocSite(_) => false
  }

  def getACS: Option[AllocCallSite] = this match {
    case Recency(loc, _) => loc.getACS
    case acs @ AllocCallSite(_, _) => Some(acs)
    case _ => None
  }

  override def toString: String = this match {
    case Recency(loc, _) => loc.toString
    case u @ UserAllocSite(_) => throw UserAllocSiteError(u)
    case p @ PredAllocSite(_) => p.toString
  }
}

object Loc {
  def parse(str: String, cfgOpt: Option[CFG]): Try[Loc] = {
    val recency = "(R|O)(.+)".r
    val (recMap, name): (Loc => Loc, String) = str match {
      // recency abstraction
      case recency("R", str) => (Recency(_, Recent), str)
      case recency("O", str) => (Recency(_, Old), str)
      case _ => (x => x, str)
    }

    val userASite = "#([0-9]+)".r
    val predASite = "#([0-9a-zA-Z-.<>]+)".r
    val allocCallSite = "(.+):ACS\\[([^\\[\\]]*)\\]".r

    def getLoc(str: String): Try[Loc] = str match {
      // allocation site
      case userASite(id) => Try(UserAllocSite(id.toInt))
      case predASite(name) => Success(PredAllocSite(name))
      // TODO trace sensitive address abstraction
      // allocation call-site
      case allocCallSite(pre, cps) => {
        val loc = getLoc(pre)
        cfgOpt match {
          case Some(cfg) => loc flatMap (loc => {
            val init: Try[List[Call]] = Success(Nil)
            val tokens = if (cps == "") Nil else cps.split(",").toList
            val calls = (init /: tokens) {
              case (Success(calls), str) => cfg.findBlock(str) match {
                case Success(call: Call) => Success(call :: calls)
                case _ => Failure(IllFormedBlockStr)
              }
              case (fail, _) => fail
            }
            calls match {
              case Success(calls) => Success(AllocCallSite(loc, calls))
              case Failure(e) => Failure(e)
            }
          })
          case None => loc
        }
      }
      // otherwise
      case str => Failure(NoLoc(str))
    }

    getLoc(name) map recMap
  }
  def parse(str: String): Try[Loc] = parse(str, None)
  def parse(str: String, cfg: CFG): Try[Loc] = parse(str, Some(cfg))

  def apply(str: String): Loc = apply(PredAllocSite(str), Sensitivity.initTP)
  def apply(asite: AllocSite, tp: TracePartition): Loc = {
    asite |>
      condApply(HeapClone, TraceSensLoc(_, tp)) |>
      condApply(ACS > 0, AllocCallSite(_, Nil)) |>
      condApply(RecencyMode, Recency(_, Recent))
  }
  private def condApply(cond: Boolean, f: Loc => Loc)(input: Loc): Loc = {
    if (cond) f(input)
    else input
  }

  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by({
    case addrPart => addrPart.toString
  })
}
