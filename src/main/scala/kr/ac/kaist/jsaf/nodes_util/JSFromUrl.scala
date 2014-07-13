/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.io.BufferedReader
import _root_.java.io.File
import _root_.java.io.InputStreamReader
import _root_.java.io.IOException
import _root_.java.io.PrintStream
import _root_.java.net.URL
import _root_.java.util.{Set => JSet}
import edu.rice.cs.plt.tuple.{Option => JOption}
import com.ibm.wala.cast.js.html.IdentityUrlResolver
import com.ibm.wala.cast.js.html.IHtmlParser
import com.ibm.wala.cast.js.html.IUrlResolver
import com.ibm.wala.cast.js.html.SourceRegion
import com.ibm.wala.cast.js.html.WebUtil
import com.ibm.wala.cast.js.html.jericho.JerichoHtmlParser
import kr.ac.kaist.jsaf.nodes_util.DomLessSourceExtractor.HtmlCallback
import kr.ac.kaist.jsaf.nodes_util.DomLessSourceExtractor.IGeneratorCallback
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

class JSFromUrl(urlstring: String, outFile: String) extends Walker {
  def doit() = {
    val entrypointUrl = new URL(urlstring)
    val htmlCallback: IGeneratorCallback = new HtmlCallback(entrypointUrl, new IdentityUrlResolver)
    (new JerichoHtmlParser).parse(entrypointUrl, WebUtil.getStream(entrypointUrl), htmlCallback, entrypointUrl.getFile)
    val finalRegion = new SourceRegion
    htmlCallback.writeToFinalRegion(finalRegion)
    // writing the final region into one SourceFileModule.
    val fileName: String = if(outFile != null) outFile else getScriptName(entrypointUrl.getHost)
    finalRegion.writeToFile(new PrintStream(new File(fileName)))
    System.out.println("Wrote the extracted JavaScript source code, if any, to " + fileName + ".")
  }

  def getScriptName(file: String) = {
    val lastIdxOfSlash = file.lastIndexOf('/')
    if (lastIdxOfSlash == (-1)) file else file.substring(lastIdxOfSlash + 1)
  }
}
