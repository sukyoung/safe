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
package kr.ac.kaist.safe.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.ast_rewriter._
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.LINE_SEP
import java.nio.charset.Charset
import java.io._

// Rewrite the WALA JavaScript builtin function model to .jsmodel format
case object WALARewrite extends PhaseObj[Unit, WALARewriteConfig, Unit] {
  val name: String = "walaModelRewriter"
  val help: String = "Change WALA JavaScript built-in model to .jsmodel"

  def transJSModel(fileName: String): List[String] = {
    val lines = scala.io.Source.fromFile(fileName).mkString
    val h: StringBuilder = new StringBuilder // heap
    val f: StringBuilder = new StringBuilder // function map
    val g: StringBuilder = new StringBuilder // Glbal Location
    val todo: StringBuilder = new StringBuilder // todo file
    val S: String = "  "
    val L = LINE_SEP
    var fid = 1
    var cur = "" // current modeling object name
    var todobuffer = ""

    h.append(s"Heap: {$L")
    f.append(s"Function: {$L")
    g.append(s"$S#Global: {$L")

    val (newh, newf, newbuf, newflag) = lines.split("\n").foldLeft((h, f, "", 0)) {
      case ((h, f, buf, flag), str) => {
        flag match {
          // Object
          case 0 => {
            if (str.contains("$proto$__WALA__") && str.contains("{")) {
              val name = str.substring(0, str.indexOf("$proto$__WALA__"))
              g.append(s"""$S$S"$name": <#$name, T, F, T>,$L""")
              h.append(s"$S#$name: {$L")
              h.append(s"""$S$S"prototype": <#$name.prototype, F, F, F> $L$S},$L""")
              h.append(s"$S#$name.prototype: {$L")
              todo.append(str + "\n")
              cur = name
              (h, f, "", 1)
            } else {
              todo.append(str + "\n")
              (h, f, "", 0)
            }
          }
          // Prototype
          case 1 => {
            if (str.contains(" function ")) {
              val name = str.substring(0, str.indexOf(":")).trim
              val funname = str.substring(str.indexOf("function ") + 9, str.indexOf("(")).trim
              val param = str.substring(str.indexOf("("), str.indexOf(")") + 1).trim
              todo.append(str)
              h.append(s"""$S$S"$name": <#$cur.prototype.$name, T, F, T>,$L""")
              g.insert(0, s"$S#$cur.prototype.$name: { $L$S$S[[Call]]: fun($fid)$L$S},$L")
              f.append(s"""$S$fid: [\\\\$L""")
              f.append(s"  function $funname $param {")
              fid += 1
              (h, f, "", 2)
            } else if (str.contains("};")) {
              h.delete(h.length - 2, h.length)
              h.append(s"$L$S},$L")
              todo.append(str + "\n")
              (h, f, "", 0)
            } else {
              todo.append(str + "\n")
              (h, f, "", 1)
            }
          }
          // function body of Property
          case 2 => {
            if (str.contains("primitive")) {
              f.append(s"$L$S$S// TODO prologue.js use primitive object see todo file")
              f.append(s"""$L$S}$L$S\\\\],$L""")
              todo.append(buf + "\n")
              todo.append(str + "\n")
              if (str.contains("return")) {
                (h, f, "todo", 4)
              } else {
                (h, f, "todo", 3)
              }
            } else if (str.indexOf("  }") == 0) {
              val buffer = buf + "\n"
              todo.append(s"$S$S// transformed to .jsmodel$L")
              todo.append(str + "\n")
              f.append(buffer)
              f.append(s"$S}$L")
              f.append(s"""$S\\\\],$L""")
              (h, f, "", 1)
            } else {
              val buffer = buf + "\n" + str
              (h, f, buffer, 2)
            }
          }
          case 3 => {
            if (str.contains("return")) {
              todo.append(str + "\n")
              (h, f, "todo", 4)
            } else {
              todo.append(str + "\n")
              (h, f, "todo", 3)
            }
          }
          case 4 => {
            todo.append(str + "\n")
            (h, f, "", 1)
          }
        }
      }
    }

    g.delete(g.length - 2, g.length)
    g.append(s"$L$S}")
    newh.append(g.toString)
    newh.append(s"$L}$L$L")
    newf.delete(newf.length - 2, newf.length)
    newf.append(s"$L}$L")
    newh.append(newf.toString)
    List(newh.toString, todo.toString)
  }

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: WALARewriteConfig
  ): Try[Unit] = {
    register(aaddrType = NormalAAddr)
    val data = transJSModel(safeConfig.fileNames.head)
    val outFileName = config.outFile match {
      case Some(out) => {
        val pw = new PrintWriter(new File(out))
        pw.write(data(0))
        pw.close
        val pw2 = new PrintWriter(new File(out + ".todo"))
        pw2.write(data(1))
        pw2.close
        println("Dumped rewritten model to " + out)
      }
      case None => {
        println("You must give outfile")
        Success(unit)
      }
    }
    Success(unit)
  }

  def defaultConfig: WALARewriteConfig = WALARewriteConfig()
  val options: List[PhaseOption[WALARewriteConfig]] = List(
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the results of rewriting will be written to the outfile.jsmodel.")
  )
}

// WALARewrite phase config
case class WALARewriteConfig(
  var outFile: Option[String] = None
) extends Config
