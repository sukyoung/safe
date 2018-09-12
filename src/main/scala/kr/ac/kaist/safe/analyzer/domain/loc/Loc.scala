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
import kr.ac.kaist.safe.nodes.cfg.{ CFG, Call }
import kr.ac.kaist.safe.util.PipeOps._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet
import scala.util.parsing.combinator._
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

trait LocParser extends RegexParsers {
  val cfg: CFG
  lazy val any = ".+".r
  lazy val nat = "[0-9]+".r ^^ { n => n.toInt }
  lazy val num = "-?[0-9]+".r ^^ { n => n.toInt }
  lazy val alpha = "[a-zA-Z]+".r
  lazy val alphaNum = "[0-9a-zA-Z]+".r
  lazy val predName = "[0-9a-zA-Z-.<>]+".r

  // allocation site abstraction
  lazy val userASite = "#" ~> nat ^^ { id => UserAllocSite(id) }
  lazy val predASite = "#" ~> predName ^^ { name => PredAllocSite(name) }
  lazy val allocSite = userASite | predASite

  // block
  lazy val fid = num
  lazy val entry = "entry" ^^^ -1
  lazy val exit = "exit" ^^^ -2
  lazy val exitExc = "exit-exc" ^^^ -3
  lazy val bid = nat | entry | exit | exitExc
  lazy val block = (fid <~ ":") ~ bid ^^ { case f ~ b => cfg.getBlock(f, b) }

  // allocation callsite abstraction
  lazy val call = block ^? { case Some(call: Call) => call }
  def acs(parser: Parser[Loc]): Parser[Loc] = (parser <~ ":ACS[") ~ repsep(call, ",") <~ "]" ^? {
    case loc ~ bs => AllocCallSite(loc, bs)
  }

  // recency
  lazy val recent = "R" ^^^ Recent
  lazy val old = "O" ^^^ Old
  lazy val recencyTag = recent | old
  def recency(parser: Parser[Loc]): Parser[Loc] = recencyTag ~ parser ^^ {
    case tag ~ loc => Recency(loc, tag)
  }

  // abstract location
  lazy val loc = allocSite |>
    condApply(ACS > 0, acs) |>
    condApply(RecencyMode, recency)

  def apply(str: String): Try[Loc] = Try(parse(loc, str).getOrElse(throw LocParseError(str)))
}

object Loc {
  def parse(str: String, cfgIn: CFG): Try[Loc] = (new LocParser { val cfg = cfgIn })(str)
  def apply(str: String): Loc = apply(PredAllocSite(str), Sensitivity.initTP)
  def apply(asite: AllocSite, tp: TracePartition): Loc = {
    asite |>
      condApply[Loc](HeapClone, TraceSensLoc(_, tp)) |>
      condApply[Loc](ACS > 0, AllocCallSite(_, Nil)) |>
      condApply[Loc](RecencyMode, Recency(_, Recent))
  }

  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by({
    case addrPart => addrPart.toString
  })
}
