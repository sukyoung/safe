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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet
import scala.util.{ Try, Success, Failure }
import spray.json._

// concrete location type
abstract class Loc extends Value {
  def isUser: Boolean = this match {
    case Recency(loc, _) => loc.isUser
    case UserAllocSite(_) => true
    case PredAllocSite(_) => false
  }

  override def toString: String = this match {
    case Recency(loc, _) => loc.toString
    case u @ UserAllocSite(_) => throw UserAllocSiteError(u)
    case p @ PredAllocSite(_) => p.toString
  }

  def toJson: JsValue
}

object Loc {
  // predefined special concrete location
  lazy val predConSet: Set[Loc] = HashSet(
    PredAllocSite.GLOBAL_ENV,
    PredAllocSite.PURE_LOCAL
  )

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
      // otherwise
      case str => Failure(NoLoc(str))
    }
  }

  def apply(str: String): Loc = apply(PredAllocSite(str))
  def apply(asite: AllocSite): Loc = AAddrType match {
    case NormalAAddr => asite
    case RecencyAAddr => Recency(asite, Recent)
  }

  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by({
    case addrPart => addrPart.toString
  })

  def fromJson(v: JsValue): Loc = v match {
    case JsObject(m) => (
      m.get("loc").map(Loc.fromJson _),
      m.get("recency").map(RecencyTag.fromJson _)
    ) match {
        case (Some(l), Some(r)) => Recency(l, r)
        case _ => throw RecencyParseError(v)
      }
    case _ => AllocSite.fromJson(v)
  }
}
