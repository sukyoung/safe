/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.safe.concolic

import java.io.{BufferedWriter, FileWriter}
import scala.collection.JavaConversions
import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.safe.interpreter.Interpreter
import kr.ac.kaist.safe.nodes.{NodeFactory => NF}
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{IRFactory => IF}
import kr.ac.kaist.safe.util.Coverage

////////////////////////////////////////////////////////////////////////////////
// Concolic Test
////////////////////////////////////////////////////////////////////////////////
object ConcolicMain {
  /**
   * Working on a very simple concolic testing...
   */
  def concolic: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The concolic command needs a file to perform concolic testing.")
    val fileName: String = Shell.params.FileNames(0)
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    var return_code = 0
    IRGenerator.ignoreId = 0
    val coverage = new Coverage
    
    val program: Program = Parser.fileToAST(fileNames)
    val irErrors = Shell.ASTtoIR(fileName, program, JOption.none[String], JOption.some[Coverage](coverage))
    val irOpt: JOption[IRRoot] = irErrors.first
    val program2: Program = irErrors.third
    //val irOpt: JOption[IRRoot] = Shell.fileToIR(fileNames, JOption.none[String], JOption.some[Coverage](coverage)).first
    if (irOpt.isSome) {
      // Initialize AddressManager
      AddressManager.reset()

      val ir: IRRoot = irOpt.unwrap

      val builder = new CFGBuilder(ir)
      val cfg = builder.build
      val errors = builder.getErrors
      if (!(errors.isEmpty))
        Shell.reportErrors(NodeUtil.getFileName(ir), Shell.flattenErrors(errors), JOption.none[Pair[FileWriter, BufferedWriter]])
      NodeRelation.set(program2, ir, cfg, true)
      // Initialize AbsString cache
      kr.ac.kaist.jsaf.analysis.typing.domain.AbsString.initCache
      val initHeap = new InitHeap(cfg)
      initHeap.initialize

      coverage.cfg = cfg
      coverage.typing = new Typing(cfg, true, false)
      coverage.typing.analyze(initHeap)
      coverage.semantics = new Semantics(cfg, Worklist.computes(cfg, true), false)
      coverage.stateManager = new StateManager(cfg, coverage.typing, coverage.semantics) 
      // Store function information using the result of the analysis  
      coverage.updateFunction
      
      // Filtering to ignore original body
      var fir = IRFilter.doit(ir) 

      fir = IRSimplifier.doit(fir)
      //System.out.println(new JSIRUnparser(fir).doit)

      val instrumentor = new Instrumentor(fir, coverage)
      fir = instrumentor.doit
      //instrumentor.debugOn
      
      //coverage.debug = true;
      //coverage.timing = true;

      val interpreter = new Interpreter
      val extractor = new ConstraintExtractor
      val solver = new ConcolicSolver(coverage)
      if (coverage.debug) {
        solver.debug = true
        extractor.debug = true
      }

      do {
        var inputNumber = 0
        do {
          System.out.println
          var startTime = System.nanoTime
          val result = coverage.functions.get(coverage.target) match {
            case Some(f) => 
              val temp = solver.solve(coverage.getConstraints, coverage.inum, f)
              if (temp.nonEmpty)
                Some(temp)
              else 
                None
            case None => None
          }
          //solveConstraints

          coverage.setInput(result)
          coverage.setupCall match { 
            case Some(ir) => 
              val inputIR = instrumentor.walk(ir, IRFactory.dummyIRId(coverage.target)).asInstanceOf[IRStmt]
              /*System.out.println("Input IR!!!!!!!!")
              System.out.println(new JSIRUnparser(ir).doit)
              System.out.println*/
              // Print instrumented IR
              /*if (Shell.params.opt_OutFileName != null) {
                val outfile = Shell.params.opt_OutFileName
                try {
                  val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(outfile)
                  val (fw, writer) = (pair.first, pair.second)
                  ASTIO.writeJavaAst(ir, outfile)
                  writer.close
                  fw.close
                  System.out.println("Dumped IR to " + outfile)
                }
                catch {
                  case e: IOException => {
                    throw new IOException("IOException " + e + "while writing " + outfile)
                  }
                }
              }*/
              inputNumber = inputNumber + 1
              coverage.inputIR = Some(inputIR) 

              if (coverage.timing)
                System.out.println("Generating test input takes "+(System.nanoTime - startTime)/1000000 + " times.") 
              startTime = System.nanoTime

              interpreter.doit(fir, JOption.some[Coverage](coverage), true)

              if (coverage.timing)
                System.out.println("Interpreting the input takes "+(System.nanoTime - startTime)/1000000 + " times.") 

            case None => 
              coverage.inputIR = None
              interpreter.doit(ir, JOption.some[Coverage](coverage), true)
          }

          if (coverage.isFirst)
            extractor.initialize
          extractor.modify(coverage.report)
          coverage.constraints = extractor.constraints
          coverage.necessaries = extractor.necessaries

        } while (coverage.continue)
        coverage.removeTarget
        //System.out.println("Total number of inputs: " + inputNumber)
      } while (coverage.existCandidate)
   // System.out.println("Total statements: " + coverage.total)
   // System.out.println("Executed statements: " + coverage.executed)
    }
    else return_code = -2

    return_code
  }

  /*def solveConstraints(): Option[Map[String, (Id, List[Stmt])]] = {
    try {
      val temp = solver.solve(coverage.getConstraints, coverage.inum, f)
      if (temp.nonEmpty) Some(temp)
      else None
    } catch {
      case e: SolverError => 
        extractor.extract 
        coverage.constraints = extractor.constraints
        solve
    }
  }*/
}
