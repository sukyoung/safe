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

import scala.util._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.compiler.IRSimplifier
import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util.{ Coverage, EJSCompletionType, Span }

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
   * Represents a combination of a ValError and the Span where this ValError was created.
   * @param valError
   * @param span The Span where this ValError was created.
   */
  case class ValErrorSpan(valError: ValError, span: Span)

  /**
   * The values returned by exhaustively checking all constraints generated for a specific target.
   * @param nrOfIterations The number of iterations it took the concolic tester to exhaust all constraints.
   * @param jsExceptionsEncountered A set of all uncaught exceptions that were encountered during interpretation.
   */
  case class ConstraintsFinishedResult(nrOfIterations: Int, jsExceptionsEncountered: Set[ValErrorSpan]) {
    /**
     * Update the current result with a new Completion produced by running the interpreter:
     * the number of iterations is incremented and if the Completion represents an uncaught exception,
     * the set of uncaught exceptions that was encountered is updated to include this new exception.
     * @param completion The Completion produced by the interpreter.
     * @return A new, updated Result
     */
    def newCompletion(completion: Completion): ConstraintsFinishedResult = completion.Type match {
      case EJSCompletionType.THROW =>
        copy(
          nrOfIterations = nrOfIterations + 1,
          jsExceptionsEncountered = jsExceptionsEncountered + ValErrorSpan(completion.error, completion.span)
        )
      case _ =>
        copy(nrOfIterations = nrOfIterations + 1)
    }

    /**
     * Combines this ConstraintsFinishedResult with another ConstraintsFinishedResult, adding up both
     * iteration counts and taking the union of both sets of exceptions.
     * @param other The other ConstraintsFinishedResult to be combined with.
     * @return A new ConstraintsFinishedResult
     */
    def +(other: ConstraintsFinishedResult): ConstraintsFinishedResult = {
      copy(nrOfIterations + other.nrOfIterations, jsExceptionsEncountered.union(other.jsExceptionsEncountered))
    }
  }

  /**
   * Exhaustively check all constraints that can be generated for one single target.
   * @param coverage The Coverage to use.
   * @param solver The ConcolicSolver to use.
   * @param instrumentor The Instrumentor instrumenting the IR.
   * @param interpreter The Interpreter to use.
   * @param extractor The ConstraintExtractor generating new constraints.
   * @param fir The filtered IR
   * @param ir The IR to instrument and execute.
   * @return A ConstraintsFinishedResult that contains the number of iterations it took to test all constraints for
   *         this target, as well as all uncaught exceptions encountered during the testing of all constraints.
   */
  private def goOverAllConstraints(
    coverage: Coverage,
    solver: ConcolicSolver,
    instrumentor: Instrumentor,
    interpreter: Interpreter,
    extractor: ConstraintExtractor,
    fir: IRRoot,
    ir: IRRoot
  ): ConstraintsFinishedResult = {
    @scala.annotation.tailrec
    def loop(currentResult: ConstraintsFinishedResult): ConstraintsFinishedResult = {
      System.out.println("\nIterating in ConcolicMain")
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
      val setupResult = coverage.setupCall()
      val completion: Completion = setupResult match {
        case Some(ir) =>
          val inputIR = instrumentor.walk(ir, IRFactory.dummyIRId(coverage.target)).asInstanceOf[IRStmt]
          coverage.inputIR = Some(inputIR)
          if (coverage.timing) {
            System.out.println("Generating test input takes " + (System.nanoTime - startTime) / 1000000 + " times.")
          }
          startTime = System.nanoTime
          val completion: Completion = interpreter.doit(fir, Some(coverage), true)
          if (coverage.timing) {
            System.out.println("Interpreting the input takes " + (System.nanoTime - startTime) / 1000000 + " times.")
          }
          completion
        case None =>
          coverage.inputIR = None
          val completion: Completion = interpreter.doit(ir, Some(coverage), true)
          completion
      }

      if (coverage.isFirst) {
        extractor.initialize()
      }
      println(s"Interpreter gathered report ${coverage.report}")
      val ExtractedConstraintInformations(newConstraints, newNecessaries) = extractor.modify(coverage.report)
      coverage.constraints = newConstraints
      coverage.necessaries = newNecessaries
      val newResult = currentResult.newCompletion(completion)
      if (coverage.continue) {
        loop(newResult)
      } else {
        newResult
      }
    }
    val initialResult: ConstraintsFinishedResult = ConstraintsFinishedResult(0, Set())
    loop(initialResult)
  }

  /**
   * Exhaustively check all constraints that can be generated for all targets of the given IR.
   * @param coverage The Coverage to use.
   * @param solver The ConcolicSolver to use.
   * @param instrumentor The Instrumentor instrumenting the IR.
   * @param interpreter The Interpreter to use.
   * @param extractor The ConstraintExtractor generating new constraints.
   * @param fir The filtered IR
   * @param ir The IR to instrument and execute.
   * @return A ConstraintsFinishedResult that contains the number of iterations it took to test all constraints for
   *         all targets in this IR, as well as all uncaught exceptions encountered during the testing of all
   *         constraints.
   */
  private def testAllTargets(
    coverage: Coverage,
    solver: ConcolicSolver,
    instrumentor: Instrumentor,
    interpreter: Interpreter,
    extractor: ConstraintExtractor,
    fir: IRRoot,
    ir: IRRoot
  ): ConstraintsFinishedResult = {
    @scala.annotation.tailrec
    def loop(currentResult: ConstraintsFinishedResult): ConstraintsFinishedResult = {
      coverage.removeTarget()
      val newResult: ConstraintsFinishedResult =
        goOverAllConstraints(coverage, solver, instrumentor, interpreter, extractor, fir, ir)
      if (coverage.existCandidate) {
        loop(currentResult + newResult)
      } else {
        currentResult
      }
    }
    loop(ConstraintsFinishedResult(0, Set()))
  }

  private def reportErrors(uncaughtErrors: Set[ValErrorSpan]): Unit = {
    print("\n\n")
    if (uncaughtErrors.isEmpty) {
      println("No errors detected during concolic testing.")
    } else {
      val nrOfErrors: Int = uncaughtErrors.size
      val errorsWord: String = if (nrOfErrors > 1) "errors" else "error"
      println(s"$nrOfErrors $errorsWord detected during concolic testing:")
      for (ValErrorSpan(valError, span) <- uncaughtErrors) {
        println(s"\t\t$valError @ $span")
      }
    }
    print("\n\n")
  }

  /**
   * Working on a very simple concolic testing...
   */
  def concolic(ir: IRRoot, cfg: CFG): Try[Int] = {
    init()
    val return_code = 0
    IRGenerator.ignoreId = 0

    // TODO MV Removed
    // Initialize AbsString cache
    // kr.ac.kaist.safe.analyzer.domain.AbsString.initCache

    val callSiteSens = 2
    val sens = CallSiteSensitivity(callSiteSens)
    val initTP = sens.initTP
    val entryCP = ControlPoint(cfg.globalFunc.entry, initTP)

    val worklist = Worklist(cfg)
    worklist.add(entryCP)
    // TODO MV cfg == cfg2 ?
    val tryAnalysisResult = Analyze.analyze(cfg, AnalyzeConfig(callsiteSensitivity = callSiteSens))
    tryAnalysisResult.flatMap({
      case (cg2, iters, tp, semantics) =>
        val coverage = new Coverage(cfg, semantics)
        coverage.updateFunction(cfg)
        var fir = IRFilter.doit(ir)
        fir = IRSimplifier.doit(fir)
        val instrumentor = new Instrumentor(fir, coverage)
        fir = instrumentor.doit
        val interpreter = new Interpreter(InterpretConfig(InterpreterModes.OTHER))
        val extractor = new ConstraintExtractor
        val solver = new ConcolicSolver(coverage)
        if (coverage.debug) {
          solver.debug = true
          extractor.debug = true
        }
        val ConstraintsFinishedResult(_, uncaughtErrors) =
          testAllTargets(coverage, solver, instrumentor, interpreter, extractor, fir, ir)
        reportErrors(uncaughtErrors)
        Success(return_code)
    })
  }
}
