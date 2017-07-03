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

package kr.ac.kaist.safe.concolic

import java.io.{ BufferedWriter, FileWriter }
import kr.ac.kaist.safe.analyzer.domain._

import scala.collection.immutable.{ HashMap, HashSet }
import scala.util._
import edu.rice.cs.plt.tuple.{ Option => JOption }

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.compiler.IRSimplifier
import kr.ac.kaist.safe.interpreter.Interpreter
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Concrete, Coverage, NodeRelation }

////////////////////////////////////////////////////////////////////////////////
// Concolic Test
////////////////////////////////////////////////////////////////////////////////
object ConcolicMain {

  private def init(): Unit = {
    Utils.register(
      DefaultUndef,
      DefaultNull,
      DefaultBool,
      DefaultNumber,
      StringSet(0),
      DefaultLoc,
      RecencyAAddr
    )
  }

  /**
   * Working on a very simple concolic testing...
   */
  def concolic(ir: IRRoot, cfg: CFG): Try[Int] = {

    init()

    val return_code = 0
    IRGenerator.ignoreId = 0

    // Initialize AbsString cache
    // TODO MV Removed
    // kr.ac.kaist.safe.analyzer.domain.AbsString.initCache
    val initHeap = AbsHeap(HashMap(
      BuiltinGlobal.loc -> AbsObject.Bot
    // TODO If delete, not working because not allowed update to bottom heap
    ), HashSet[Concrete]())
    BuiltinGlobal.initHeap(initHeap, cfg)

    val sens = CallSiteSensitivity(1) * LoopSensitivity(1)
    val initTP = sens.initTP
    val entryCP = ControlPoint(cfg.globalFunc.entry, initTP)

    val worklist = Worklist(cfg)
    worklist.add(entryCP)

    //    val semantics = new Semantics(cfg, worklist)
    // TODO MV cfg == cfg2 ?
    val tryAnalysisResult = Analyze.analyze(cfg, AnalyzeConfig())
    tryAnalysisResult.flatMap({
      case (cg2, iters, tp, semantics) =>
        // TODO MV Removed: val stateManager = new StateManager(cfg, semantics)
        //    val coverage = new Coverage(cfg, semantics, stateManager)
        val coverage = new Coverage(cfg, semantics)
        // coverage.typing = new Typing(cfg, true, false)
        // Store function information using the result of the analysis
        coverage.updateFunction(cfg)

        // Filtering to ignore original body
        var fir = IRFilter.doit(ir)

        fir = IRSimplifier.doit(fir)
        //System.out.println(new JSIRUnparser(fir).doit)

        val instrumentor = new Instrumentor(fir, coverage)
        fir = instrumentor.doit
        //instrumentor.debugOn

        //coverage.debug = true;
        //coverage.timing = true;

        val interpreter = new Interpreter(InterpretConfig(InterpreterModes.OTHER))
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
                  System.out.println("Generating test input takes " + (System.nanoTime - startTime) / 1000000 + " times.")
                startTime = System.nanoTime

                interpreter.doit(fir, Some(coverage), true)

                if (coverage.timing)
                  System.out.println("Interpreting the input takes " + (System.nanoTime - startTime) / 1000000 + " times.")

              case None =>
                coverage.inputIR = None
                interpreter.doit(ir, Some(coverage), true)
            }

            if (coverage.isFirst) {
              extractor.initialize()
            }
            extractor.modify(coverage.report)
            coverage.constraints = extractor.constraints
            coverage.necessaries = extractor.necessaries

          } while (coverage.continue)
          coverage.removeTarget()
          //System.out.println("Total number of inputs: " + inputNumber)
        } while (coverage.existCandidate)
        // System.out.println("Total statements: " + coverage.total)
        // System.out.println("Executed statements: " + coverage.executed)
        Success(return_code)
    })
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
