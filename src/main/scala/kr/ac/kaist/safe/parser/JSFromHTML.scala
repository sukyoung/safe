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

package kr.ac.kaist.safe.parser

import net.htmlparser.jericho.Element
import net.htmlparser.jericho.HTMLElementName
import net.htmlparser.jericho.Segment
import net.htmlparser.jericho.Source
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.nio.charset.CodingErrorAction
import java.util.{ List => JList }
import scala.collection.JavaConverters
import scala.io.Codec
import scala.util.Try
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.nodes.ast.SourceElements

class JSFromHTML(fileName: String) {
  val file = new File(fileName)
  val filePath = file.getParent
  val source: Source = new Source(file)
  source.fullSequentialParse
  def toList[T](jList: JList[T]): List[T] =
    JavaConverters.asScalaBuffer(jList).toList
  val scriptElements: List[Element] =
    toList(source.getAllElements(HTMLElementName.SCRIPT))
  val bogus = (1, 1)

  /*
   * Parse all code in the <script> tags, and return an AST
   */
  def parseScripts: Try[(SourceElements, ExcLog)] = {
    // filter out script elements that have non-JavaScript code
    val filteredScriptElements = scriptElements.filter(x => {
      val scriptType = x.getAttributeValue("type")
      if (scriptType == null || scriptType.toLowerCase == "text/javascript")
        true
      else false
    })

    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // get a list of JavaScript code in script elements
    val codeContents: List[(String, (Int, Int), String)] =
      filteredScriptElements.foldLeft[List[(String, (Int, Int), String)]](List())((li, x) => {
        var srcName: String = x.getAttributeValue("src")
        // embedded script code
        if (srcName == null) {
          val s: Segment = x.getContent
          li :+ (fileName, (s.getRowColumnVector.getRow, s.getBegin), s.toString)
        } // code from external source
        else {
          // extract a JavaScript file name from a string such as "main.js?135895164373817"
          val fileNameRegexp = (".*/[^/\\:*?\"<>|]+").r // \ : * ? " < > | // file:///c:/~/~/main.js?121424324
          srcName = fileNameRegexp.findFirstIn(srcName).getOrElse(srcName).trim
          if (srcName.toLowerCase.startsWith("file:")) srcName = srcName.drop(5)
          if (srcName.length > 0) {
            val srcSource = new File(srcName)
            val path = if (srcSource.isAbsolute) srcName
            else {
              if (filePath == null) srcName
              else filePath + "/" + srcName
            }
            val pathf = new File(path)
            if (pathf.exists) {
              val source = scala.io.Source.fromFile(path)
              val result = (path, bogus, source.mkString.replace("\r", ""))
              source.close
              li :+ result
            } else {
              val external = new File("downloads/" + srcName.substring(srcName.lastIndexOf('/') + 1))
              if (srcName.endsWith(".js") || srcName.containsSlice(".js")) {
                val url = srcName match {
                  case srcName if (srcName.startsWith("//")) => "http:" + srcName
                  case _ => srcName
                }
                Try(FileUtils.copyURLToFile(new URL(url), external)) getOrElse li
              }

              if (external.exists) {
                val source = scala.io.Source.fromFile(external)
                val result = (external.toString, bogus, source.mkString)
                source.close
                li :+ result
              } else {
                System.out.println("WARNING: Cannot find " + srcName)
                li
              }
            }
          } else li
        }
      })

    // dynamically loaded source
    val loadingPath = if (filePath == null) "loading" else filePath + "/loading"
    val loadingDir = new File(loadingPath)
    val codeContents2: List[(String, (Int, Int), String)] =
      if (!loadingDir.exists) codeContents
      else {
        loadingDir.listFiles.foldLeft(codeContents)((li, ff) => {
          val source = scala.io.Source.fromFile(ff.getPath)
          val name = ff.getName
          val ss = ("#loading#" + name, bogus, source.mkString)
          source.close
          li :+ ss
        })
      }

    Parser.scriptToAST(codeContents2)
  }
}
