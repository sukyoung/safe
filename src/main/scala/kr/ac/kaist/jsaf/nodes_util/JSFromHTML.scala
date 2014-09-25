/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import java.io._
import java.net._
import java.nio.charset.CodingErrorAction
import java.util.{ List => JList, HashMap }
import java.lang.{ Integer => JInteger }
import scala.io.Codec
import edu.rice.cs.plt.tuple.{ Option => JOption }
import kr.ac.kaist.jsaf.scala_src.nodes._
import net.htmlparser.jericho._
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.useful.{ Useful, Triple, Pair }
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.ProjectProperties
import org.cyberneko.html.parsers._
import org.apache.html.dom.HTMLDocumentImpl
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.html.HTMLDocument
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHelper
import org.xml.sax.InputSource
import kr.ac.kaist.jsaf.Shell
import java.util.ArrayList
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import org.apache.commons.io.FileUtils
import kr.ac.kaist.jsaf.ShellParameters

class JSFromHTML(filename: String) {
  val file = new File(filename)
  val filepath = file.getParent
  val source: Source = new Source(file)
  source.fullSequentialParse
  val scriptelements: JList[Element] = source.getAllElements(HTMLElementName.SCRIPT)
  var dump = ""
  DOMHelper.setDocumentURI(file.toURI.getScheme + "://" + file.toURI.getPath)
  DOMHelper.setProtocol(file.toURI.getScheme)
  def getSource(): Source = source

  // use of Neko HTML parser for the DOM tree
  val document = { val parser : DOMParser = new DOMParser
                   val inputStream : InputStream = new FileInputStream(filename)
                   parser.setFeature("http://xml.org/sax/features/namespaces", false)
                   parser.parse(new InputSource(inputStream))
                   parser.getDocument }
  def getDocument(): Document = document

  /*
   * Parse all code in the <script> tags, and return an AST
   */
  def parseNoSrcEventScripts(): Program =
    parseScripts(true, List())
  def parseScripts(): Program =
    parseScripts(false, List())
  def parseScripts(files: JList[String]): Program =
    parseScripts(false, files)
  def parseScripts(noSrcEvent: Boolean, files: JList[String]): Program = {
    //System.out.println(source);
    // filter out script elements that have non-JavaScript code
    val filtered_scriptelements = toList(scriptelements).filter(x =>
      {
        val scripttype = x.getAttributeValue("type")
        if (scripttype == null
           || scripttype.toLowerCase == "text/javascript")
          true
        else false
      })

    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // get a list of JavaScript code in script elements
    val codecontents: JList[Triple[String, JInteger, String]] = filtered_scriptelements.foldLeft[List[Triple[String, JInteger, String]]](List())((li, x) =>
      {
        var srcname: String = x.getAttributeValue("src")
        // embedded script code
        if (srcname == null) {
          val s:Segment = x.getContent
          if (Shell.params.opt_ScriptDump) {
            if (Shell.params.opt_OutFileName == null)
              System.out.println(s.getRowColumnVector().getRow() + " : " + s.toString)
            dump = dump + s.toString + "\n\n"
          }
          li :+ (new Triple(filename, new JInteger(s.getRowColumnVector().getRow()), s.toString()))
        } // code from external source
        else {
          if (Shell.params.opt_ScriptDump || Shell.params.opt_skipExternal || Shell.params.command == ShellParameters.CMD_CLONE_DETECTOR) {
            li
          } else {
            // extract a JavaScript file name from a string such as "main.js?135895164373817"
            /*
            if (!srcname.endsWith(".js") && srcname.containsSlice(".js"))
              srcname = srcname.take(srcname.replaceAll(".js", "<>").indexOf('<')).concat(".js")
            */
            val filenameRegexp = (".*/[^/\\:*?\"<>|]+").r // \ : * ? " < > | // file:///c:/~/~/main.js?121424324
            srcname = filenameRegexp.findFirstIn(srcname).getOrElse(srcname).trim
            if(srcname.toLowerCase.startsWith("file:")) srcname = srcname.drop(5)
            if(srcname.length > 0) {
              val srcsource = new File(srcname)
              val path = if (srcsource.isAbsolute()) srcname
              else {
                if (filepath == null) srcname
                else filepath + "/" + srcname
              }
              val pathf = new File(path)
              if (pathf.exists()) {
                val source = scala.io.Source.fromFile(path)
                val result = 
                  // jQuery modeling 
                  if (Config.jqMode && NodeUtil.isModeledLibrary(srcname)) {
                    // JavaScript stub model for jQuery
                    val SEP = File.separator
                    val base = ProjectProperties.BASEDIR + SEP
                    val newpath = base + "bin/models/jquery/__jquery__.js"
                    val newfile = new File(newpath)
                    val newsource = scala.io.Source.fromFile(newpath)
                    if(file.exists) {
                      Config.setModeledFiles(Config.getModeledFiles ++ List(newpath))
                      new Triple(newpath, new JInteger(1), newsource.mkString)
                      
                    }
                    else
                      new Triple(path, new JInteger(1), "")
                  }
                  else 
                    new Triple(path, new JInteger(1), source.mkString)
                source.close
                li :+ result
              } else {
                val external = new File("downloads/" + srcname.substring(srcname.lastIndexOf('/')+1))
                if (srcname.endsWith(".js") || srcname.containsSlice(".js")) {
                  try {
                    val domain = if (Shell.params.url != null && !Shell.params.url.isEmpty) Shell.params.url else ""
                    val url = srcname match {
                      case srcname if (srcname.startsWith("//")) => "http:" + srcname
                      case srcname if (srcname.startsWith("/")) => domain + srcname
                      case srcname if (srcname.startsWith("file://")) => srcname
                      case srcname if (srcname.startsWith("http")) => srcname
                      case _ => if (domain.startsWith("http")) domain + "/" + srcname else domain.substring(0, domain.lastIndexOf('/')+1) + srcname
                    }
                    FileUtils.copyURLToFile(new URL(url), external)
                  } catch {
                    case e: Exception =>
                      li
                  }
                }
                
                if (external.exists()) {
                  val source = scala.io.Source.fromFile(external)
                  val result = 
                  // jQuery modeling 
                  if (Config.jqMode && NodeUtil.isModeledLibrary(srcname)) {
                    // JavaScript stub model for jQuery
                    val SEP = File.separator
                    val base = ProjectProperties.BASEDIR + SEP
                    val newpath = base + "bin/models/jquery/__jquery__.js"
                    val newfile = new File(newpath)
                    val newsource = scala.io.Source.fromFile(newpath)
                    if(file.exists) {
                      Config.setModeledFiles(Config.getModeledFiles ++ List(newpath))
                      new Triple(newpath, new JInteger(1), newsource.mkString)
                      
                    }
                    else
                      new Triple(external.toString, new JInteger(1), "")
                  }
                  else 
                    new Triple(external.toString, new JInteger(1), source.mkString)
                  source.close
                  // external.delete
                  li :+ result
                } else {                
                //if (!Shell.params.opt_ScriptDump)
                  System.out.println("WARNING: Cannot find " + srcname)
                  li
                }
              }
            }
            else li
          }
        }
      })

    codecontents.addAll( // append given files
      toList(files).map(x => {
        val file: File = new File(x)
        val in: BufferedReader = Useful.utf8BufferedFileReader(file)
        var code: String = ""
        var line: String = in.readLine
        while (line != null){
          code = code + line + "\n"
          line = in.readLine
        }
        in.close()
        new Triple(x, new JInteger(1), code)
    }))

    // dynamically loaded source
    val loadingpath = if(filepath==null) "loading"
    else filepath + "/loading"
    val loading_dir = new File(loadingpath)
    val codecontents2: JList[Triple[String, JInteger, String]] =  if(!loading_dir.exists) codecontents
    else {
      toList(loading_dir.listFiles.toList).foldLeft(toList(codecontents))((li, ff) => {
        val source = scala.io.Source.fromFile(ff.getPath)
        val name = ff.getName
        val ss =
          if (Config.jqMode && NodeUtil.isModeledLibrary(name))
            new Triple("#loading#" + name, new JInteger(1), "")
          else
            new Triple("#loading#" + name, new JInteger(1), source.mkString)
        source.close
        li :+ ss
      })
    }


    if (Shell.params.opt_ScriptDump) {
      if (Shell.params.opt_OutFileName != null) {
        try {
          val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(Shell.params.opt_OutFileName)
          val (fw, writer) = (pair.first, pair.second)
          writer.write(dump)
          writer.close
          fw.close
        } catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + Shell.params.opt_OutFileName)
          }
        }
      }
    }

    // Collect event handler code on the event attributes
    var loadevent_count = 1
    var unloadevent_count = 1
    var keyboardevent_count = 1
    var mouseevent_count = 1
    var messageevent_count = 1
    var otherevent_count = 1
    val elementsList = toList(source.getAllElements)
    val eventsources: JList[Triple[String, JInteger, String]] =
      elementsList.foldLeft(List[Triple[String, JInteger, String]]())((event_list, e) => {
        val attributes = e.getAttributes
        if (attributes != null) {
          val attrIterator = e.getAttributes.iterator()
          var e_list: JList[Triple[String, JInteger, String]] = List()
          // Search all specified attributes
          while (attrIterator.hasNext) {
            val attr = attrIterator.next
            val name = attr.getKey
            val value = attr.getValue
            // load event attribute
            if (DOMHelper.isLoadEventAttribute(name) && value != null) {
              val eventsource = "function __LOADEvent__" + loadevent_count + "(event) { " + value + "}\n"
              loadevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            } // unload event attribute
            else if (DOMHelper.isUnloadEventAttribute(name) && value != null) {
              val eventsource = "function __UNLOADEvent__" + unloadevent_count + "(event) { " + value + "}\n"
              unloadevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            } // keyboard event attribute
            else if (DOMHelper.isKeyboardEventAttribute(name) && value != null) {
              val eventsource = "function __KEYBOARDEvent__" + keyboardevent_count + "(event) { " + value + "}\n"
              keyboardevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            } // mouse event attribute
            else if (DOMHelper.isMouseEventAttribute(name) && value != null) {
              val eventsource = "function __MOUSEEvent__" + mouseevent_count + "(event) { " + value + "}\n"
              mouseevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            } // message event attribute
            else if (DOMHelper.isMessageEventAttribute(name) && value != null) {
              val eventsource = "function __MESSAGEEvent__" + messageevent_count + "(event) { " + value + "}\n"
              messageevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            } // other event attribute
            else if (DOMHelper.isOtherEventAttribute(name) && value != null) {
              val eventsource = "function __OTHEREvent__" + otherevent_count + "(event) { " + value + "}\n"
              otherevent_count += 1
              e_list.add(new Triple(filename, new JInteger(attr.getRowColumnVector().getRow()), eventsource))
            }
          }
          event_list ::: toList(e_list)
        } else event_list
      })
    codecontents2.addAll(eventsources)
    Parser.scriptToAST(codecontents2)
  }
}
