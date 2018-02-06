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
import scala.collection.JavaConverters._
import scala.io.Codec
import scala.util.Try
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.nodes.ast.SourceElements
import kr.ac.kaist.safe.util.NodeUtil._

object JSFromHTML {
  private def toList[T](jList: JList[T]): List[T] = jList.asScala.toList
  // extract a JavaScript file name from a string such as "main.js?135895164373817"
  val fileNameRegexp = (".*/[^/\\:*?\"<>|]+").r // \ : * ? " < > | // file:///c:/~/~/main.js?121424324
  val bogus = (1, 1)
  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  /*
   * Parse all code in the <script> tags, and return an AST
   */
  def parseScripts(fileName: String): Try[(SourceElements, ExcLog)] = {
    val file = new File(fileName)
    val filePath = file.getParent
    val source: Source = new Source(file)
    source.fullSequentialParse

    // filter out script elements that have non-JavaScript code
    val filtered = toList(source.getAllElements(HTMLElementName.SCRIPT)).filter(x => {
      val scriptType = x.getAttributeValue("type")
      scriptType == null || scriptType.toLowerCase == "text/javascript"
    })

    // get a list of JavaScript code in script elements
    val codeContents: List[(String, (Int, Int), String)] =
      filtered.foldLeft[List[(String, (Int, Int), String)]](List())((li, x) => {
        val srcName = x.getAttributeValue("src")
        if (srcName == null) { // embedded script code
          val s: Segment = x.getContent
          li :+ (fileName, (s.getRowColumnVector.getRow, s.getBegin), s.toString)
        } else // code from external source
          externalSource(srcName, filePath) map (r => li :+ r) getOrElse li
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
          val ss = ("#loading#" + name, bogus, source.mkString.replace("\r", ""))
          source.close
          li :+ ss
        })
      }

    // add event function call
    val codeContents3: List[(String, (Int, Int), String)] = {
      val evt = INTERNAL_EVENT_FUNC
      val ss = ("#event#loop", bogus,
        s"while($INTERNAL_BOOL_TOP) { $INTERNAL_CALL($evt.func, $evt.elem, []); }")
      codeContents2 :+ ss
    }

    Parser.scriptToAST(codeContents3)
  }

  private def externalSource(name: String, filePath: String): Option[(String, (Int, Int), String)] = {
    var srcName: String = fileNameRegexp.findFirstIn(name).getOrElse(name).trim
    if (srcName.toLowerCase.startsWith("file:")) srcName = srcName.drop(5)
    if (srcName.length > 0) {
      val path =
        if (new File(srcName).isAbsolute) srcName
        else {
          if (filePath == null) srcName
          else filePath + "/" + srcName
        }
      val external = new File("downloads/" + srcName.substring(srcName.lastIndexOf('/') + 1))
      val file = new File(path)
      val pathf: File =
        if (file.exists) file
        else {
          if (srcName.endsWith(".js") || srcName.containsSlice(".js")) {
            val url = srcName match {
              case srcName if (srcName.startsWith("//")) => "http:" + srcName
              case _ => srcName
            }
            FileUtils.copyURLToFile(new URL(url), external)
          }
          external
        }
      if (pathf.exists) {
        val source = scala.io.Source.fromFile(pathf)
        val result = Some(path.toString, bogus, source.mkString.replace("\r", ""))
        source.close
        result
      } else {
        println("WARNING: Cannot find " + srcName)
        None
      }
    } else None
  }
}
