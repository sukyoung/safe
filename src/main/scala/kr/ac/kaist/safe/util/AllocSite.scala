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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer.domain.Loc
import kr.ac.kaist.safe.errors.error.AllocSiteParseError
import spray.json._

// allocation site
abstract sealed class AllocSite extends Loc
object AllocSite {
  def fromJson(v: JsValue): AllocSite = v match {
    case JsNumber(id) => UserAllocSite(id.toInt)
    case JsString(str) => PredAllocSite(str)
    case _ => throw AllocSiteParseError(v)
  }
}

// allocation site defined in user code
case class UserAllocSite(id: Int) extends AllocSite {
  override def toString: String = s"#$id"
  def toJson: JsValue = JsNumber(id)
}

// predefined allocation site
case class PredAllocSite(name: String) extends AllocSite {
  override def toString: String = s"#$name"
  def toJson: JsValue = JsString(name)
}
