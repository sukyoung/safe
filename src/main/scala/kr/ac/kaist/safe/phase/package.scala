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

package kr.ac.kaist.safe

package object phase {
  // Option regular expression map
  type Regex = scala.util.matching.Regex
  type OptRegexMap = Map[String, List[(Regex, Regex, String => Option[Unit])]]
}
