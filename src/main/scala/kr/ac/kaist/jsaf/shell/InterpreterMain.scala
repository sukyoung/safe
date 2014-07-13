/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.File
import java.util.ArrayList
import scala.collection.JavaConversions
import scala.util.control.Breaks._
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.interpreter.Interpreter
import kr.ac.kaist.jsaf.nodes_util.Coverage
import kr.ac.kaist.jsaf.nodes.IRRoot
import kr.ac.kaist.jsaf.Shell
import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager

////////////////////////////////////////////////////////////////////////////////
// Interpreter
////////////////////////////////////////////////////////////////////////////////
object InterpreterMain {
  /**
   * Interpret a JavaScript file. (Work in progress)
   * If the file interprets ok it will print the result.
   */
  def interpret: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to interpret.")
    var fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    var printComp = false
    if (!Shell.params.opt_Mozilla) printComp = true
    else {
      printComp = false

      var file = new File(fileNames.get(0))
      var safe = ""
      val tmp = new ArrayList[String]
      fileNames = new ArrayList[String]
      if (file.canRead) {
        tmp.add(file.getCanonicalPath)
        file = file.getParentFile
        breakable {
          while (file != null) {
            val list: Array[String] = file.list
            var done = true
            breakable {
              for(i <- list.length - 1 to 0 by -1) {
                if (list(i) == "shell.js") {
                  done = false
                  break
                }
              }
            }
            if (done) {
              if (safe ne "") tmp.add(safe)
              break
            }
            tmp.add(file.getCanonicalPath + "/shell.js")
            safe = file.getCanonicalPath + "/safe.js"
            file = file.getParentFile
          }
        }
      }
      for(i <- tmp.size - 1 to 0 by -1) fileNames.add(tmp.get(i))
    }

    val return_code = 0
    val irOpt: JOption[IRRoot] = Shell.fileToIR(fileNames, Shell.toOption(Shell.params.opt_OutFileName))
    if (irOpt.isSome) {
      val ir: IRRoot = irOpt.unwrap

      // Initialize AddressManager
      AddressManager.reset()

      /*
       * The following 2 lines are to print IR program for debug.
       * Check getE method in nodes_util/JSIRUnparser.scala to get unsimplified name.
       */
      /*
      String ircode = new JSIRUnparser(ir).doit();
      System.out.println(ircode);
      */
      // Interpret ir...
      new Interpreter().doit(ir, JOption.none[Coverage], printComp)
      if (Shell.params.opt_Time) Shell.printTimeTitle = "Interpretation"
    }

    return_code
  }

  /*
     * for debugging IR itself using the IR parser
     *
    BufferedReader in = Useful.utf8BufferedFileReader(new File(file));
    try {
        IR parser = new IR(in, file);
        xtc.parser.Result parseResult = parser.pFile(0);
        if (parseResult.hasValue()) {
            IRRoot root = (IRRoot)((SemanticValue) parseResult).value;
            // Interpret irs...
            new Interpreter(root).doit();

            if (out.isSome()){
                String outfile = out.unwrap();
                try{
                    for (IRStmt ir : root.getIrs())
                        ASTIO.writeJavaAst(ir, outfile);
                    System.out.println("Dumped IR to " + outfile);
                } catch (IOException e){
                    throw new IOException("IOException " + e +
                                          "while writing " + outfile);
                }
            }
        } else throw new ParserError((ParseError)parseResult, parser);
    } finally {
        try {
            in.close();
        } catch (IOException e) {}
    }
  */
}
