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

import scala.util.parsing.combinator._

trait SimpleParser extends RegexParsers {
  lazy val any = ".+".r
  lazy val nat = "[0-9]+".r ^^ { n => n.toInt }
  lazy val num = "-?[0-9]+".r ^^ { n => n.toInt }
  lazy val alpha = "[a-zA-Z]+".r
  lazy val alphaNum = "[0-9a-zA-Z]+".r
}
