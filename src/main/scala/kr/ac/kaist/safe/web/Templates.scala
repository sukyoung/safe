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
import kr.ac.kaist.safe.BASE_DIR
import scala.io.Source

object Templates {
  def getBaseTemplate: String = {
    // copy libraries
    val SEP = File.separator
    val base = BASE_DIR + SEP
    val baseHtmlPath = new File(Array[String](base + "src", "main", "resources", "templates", "base.html").mkString(SEP))
    Source.fromFile(baseHtmlPath).getLines.mkString
  }
}
