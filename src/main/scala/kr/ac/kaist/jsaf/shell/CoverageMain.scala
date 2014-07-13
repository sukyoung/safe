/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import scala.collection.JavaConversions
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes_util.Coverage
import kr.ac.kaist.jsaf.nodes.IRRoot
import kr.ac.kaist.jsaf.interpreter.Interpreter
import kr.ac.kaist.jsaf.Shell
import edu.rice.cs.plt.tuple.{Option => JOption}

////////////////////////////////////////////////////////////////////////////////
// Code Coverage
////////////////////////////////////////////////////////////////////////////////
object CoverageMain {
  /**
   * Calculates a very simple statement coverage.
   */
  def coverage: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The coverage command needs a file to calculate code coverage.")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    var return_code = 0
    val coverage = new Coverage
    val irOpt: JOption[IRRoot] = Shell.fileToIR(fileNames, JOption.none[String], JOption.some[Coverage](coverage))
    if (irOpt.isSome) {
      val ir: IRRoot = irOpt.unwrap
      new Interpreter().doit(ir, JOption.some[Coverage](coverage), true)
      System.out.println("Total statements: " + coverage.total)
      System.out.println("Executed statements: " + coverage.executed)
    }
    else return_code = -2

    return_code
  }
}
