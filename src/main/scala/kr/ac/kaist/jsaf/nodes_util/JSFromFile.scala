/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.io.File
import _root_.java.net.URL
import edu.rice.cs.plt.tuple.{Option => JOption}
import com.ibm.wala.cast.js.html.IdentityUrlResolver
import com.ibm.wala.cast.js.html.jericho.JerichoHtmlParser
import kr.ac.kaist.jsaf.scala_src.nodes._
import net.htmlparser.jericho.Config
import net.htmlparser.jericho.LoggerProvider

class JSFromFile(file: File) extends Walker {
  def doit() = {
    val entrypointUrl = file.toURI.toURL    
    val domLessScopeGenerator = new DomSimpleSourceExtractor
    val htmlParser = new JerichoHtmlParser
    val urlResolver = new IdentityUrlResolver
    Config.LoggerProvider = LoggerProvider.DISABLED
    domLessScopeGenerator.extractScripts(entrypointUrl , htmlParser , urlResolver)
  }

  def getScriptName(file: String) = {
    val lastIdxOfSlash = file.lastIndexOf('/')
    if (lastIdxOfSlash == (-1)) file else file.substring(lastIdxOfSlash + 1)
  }
}
