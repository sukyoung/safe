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

import org.scalatest._
import org.scalatest.Assertions._
import scala.io.Source
import java.io.File
import java.io.FilenameFilter
import kr.ac.kaist.safe.compiler.{ Compiler, Parser }
import kr.ac.kaist.safe.exceptions.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.safe_util.{ AddressManager, JSAstToConcrete }
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.shell.ParseMain
import kr.ac.kaist.safe.shell.UnparseMain
import kr.ac.kaist.safe.useful.Useful

class UnparserSpec extends FlatSpec {
  val SEP = File.separator
  val dir = Config.basedir + SEP + "tests/unparser_tests"
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def assertSameResult(jsName: String): Unit = {
    val temp_js = "__temp.js"
    val temp2_js = "__temp2.js"
    val temp_tjs = "__temp.tjs"
    val temp2_tjs = "__temp2.tjs"

    val file1 = new File(temp_js)
    val file2 = new File(temp2_js)
    val file3 = new File(temp_tjs)
    val file4 = new File(temp2_tjs)

    //first parse
    ParseMain.parse(Config(List("parse", "-out=" + temp_tjs, jsName)))
    //first unparse
    UnparseMain.unparse(Config(List("unparse", "-out=" + temp_js, temp_tjs)))
    //second parse
    ParseMain.parse(Config(List("parse", "-out=" + temp2_tjs, temp_js)))
    //second unparse
    UnparseMain.unparse(Config(List("unparse", "-out=" + temp2_js, temp2_tjs)))
    //compare temp.js, temp2.js
    assert(Source.fromFile(temp_js).getLines.mkString("\n") == Source.fromFile(temp2_js).getLines.mkString("\n"))

    file1.delete
    file2.delete
    file3.delete
    file4.delete
  }

  // Permute filenames for randomness
  for (filename <- Useful.shuffle(new File(dir).list(jsFilter))) {
    val fname = dir + SEP + filename
    val file = new File(fname)
    registerTest(file.getName) {
      assertSameResult(fname)
    }
  }
}
