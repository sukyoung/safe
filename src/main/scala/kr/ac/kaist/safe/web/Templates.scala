/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

package kr.ac.kaist.safe.web

import java.io.File
import kr.ac.kaist.safe.util.Useful
import kr.ac.kaist.safe.{ SEP, LINE_SEP }
import scala.io.Source

object Templates {
  def getBaseTemplate: String = {
    // copy libraries
    val baseHtmlPath = new File(Useful.path("src", "main", "resources", "templates", "base.html"))
    Source.fromFile(baseHtmlPath).getLines.mkString(LINE_SEP)
  }
}
