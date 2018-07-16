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
  def parse(str: String): Try[Loc] = {
    val recency = "(R|O)(.+)".r
    val userASite = "#([0-9]+)".r
    val predASite = "#([0-9a-zA-Z-.<>]+)".r
    str match {
      // allocation site
      case userASite(id) => Try(UserAllocSite(id.toInt))
      case predASite(name) => Success(PredAllocSite(name))
      // recency abstraction
      case recency("R", str) => parse(str).map(Recency(_, Recent))
      case recency("O", str) => parse(str).map(Recency(_, Old))
      // TODO trace sensitive address abstraction
      // otherwise
      case str => Failure(NoLoc(str))
    }
  }

  def apply(str: String, tp: TracePartition = Sensitivity.initTP): Loc = apply(PredAllocSite(str), tp)
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
