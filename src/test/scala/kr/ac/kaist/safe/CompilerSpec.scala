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
import kr.ac.kaist.safe.compiler.Compiler
import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.safe_util.JSIRUnparser
import kr.ac.kaist.safe.useful.Useful

class CompilerSpec extends FlatSpec {
  val SEP = File.separator
  val dir = Config.basedir + SEP + "tests/compiler_tests" + SEP
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def assertSameResult(filename: String): Unit = {
    val jsName: String = filename + ".js"
    val testName: String = filename + ".test"

    val config = Config(List("compile", jsName))
    val (ir, rc, errors) = Compiler.compile(config)
    val dump: String = new JSIRUnparser(ir).doit

    assert(errors.isEmpty)
    assert(new File(testName).exists)
    assert(Source.fromFile(testName).getLines.mkString("\n") == dump)
  }

  // Permute filenames for randomness
  for (filename <- Useful.shuffle(new File(dir).list(jsFilter))) {
    val fname = dir + filename
    val file = new File(fname)
    val name = fname.substring(0, fname.length - 3)
    registerTest(file.getName) {
      assertSameResult(name)
    }
  }
}
