/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.{File, IOException, BufferedWriter, FileWriter}
import java.util.HashMap
import scala.collection.JavaConversions
import kr.ac.kaist.jsaf.analysis.cfg.{DotWriter, CFG, CFGBuilder}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMBuilder
import kr.ac.kaist.jsaf.analysis.visualization.Visualization
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.bug_detector.{BugDetector, StrictModeChecker}
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.{IRRoot, Program}
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.{Shell, ShellParameters, ProjectProperties}
import kr.ac.kaist.jsaf.useful.{MemoryMeasurer, Pair, Useful}
import kr.ac.kaist.jsaf.tests.SemanticsTest
import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter

////////////////////////////////////////////////////////////////////////////////
// Analyze
//   - PreAnalyze
//   - Sparse
//   - HTML
//   - BugDetector
////////////////////////////////////////////////////////////////////////////////
object AnalyzeMain {
  /**
   * Analyze a JavaScript file. (Work in progress)
   */
  def analyze: Int = {
    val quiet = (Shell.params.command == ShellParameters.CMD_BUG_DETECTOR) ||
                (Shell.params.command ==ShellParameters.CMD_WEBAPP_BUG_DETECTOR)
    if (quiet) Config.setQuietMode
    var locclone = Shell.params.opt_LocClone

    // Initialize AddressManager
    AddressManager.reset()

    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to analyze")
    val fileName: String = Shell.params.FileNames(0)
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)
    Config.setFileName(fileName)

    // Analysis Config
    if(Shell.params.opt_loop) Config.setLoopMode
    if (Shell.params.opt_Verbose1) Config.setVerbose(1)
    if (Shell.params.opt_Verbose2) Config.setVerbose(2)
    if (Shell.params.opt_Verbose3) Config.setVerbose(3)

    if (Shell.params.opt_Test) {
      Config.setTestMode(Shell.params.opt_Test)
      System.out.println("Test mode enabled.")
    }

    if (Shell.params.opt_Library) {
      Config.setLibMode(Shell.params.opt_Library)
      System.out.println("Library mode enabled.")
    }

    if (Shell.params.opt_NoAssert) {
      Config.setAssertMode(!Shell.params.opt_NoAssert)
      System.out.println("Assert mode disabled.")
    }

    if (Shell.params.opt_Compare) Config.setCompareMode

    // Context-sensitivity for main analysis
    var context: Int = -1
    context = Config.contextSensitivityMode

    // Temporary parameter setting for html and bug-detector
    if (Shell.params.command == ShellParameters.CMD_BUG_DETECTOR) {
        context = Config.Context_OneCallsiteAndObject
    }
    if (Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_BUG_DETECTOR) {
      //Shell.params.opt_MultiThread = true
      //Shell.params.opt_ReturnStateOn = true
    }

    // Context-sensitivity mode
    if (Shell.params.opt_ContextInsensitive) context = Config.Context_Insensitive
    else if (Shell.params.opt_ContextLoop) {
      Config.setLoopSensitiveMode(true)
      context = Config.Context_Loop
    }
    else if (Shell.params.opt_Context1Callsite) context = Config.Context_OneCallsite
    else if (Shell.params.opt_Context2Callsite) context = Config.Context_KCallsite
    else if (Shell.params.opt_Context3Callsite) context = Config.Context_KCallsite
    else if (Shell.params.opt_Context4Callsite) context = Config.Context_KCallsite
    else if (Shell.params.opt_Context5Callsite) context = Config.Context_KCallsite
    else if (Shell.params.opt_ContextCallsiteSet) context = Config.Context_CallsiteSet
    else if (Shell.params.opt_Context1Object) context = Config.Context_OneObject
    else if (Shell.params.opt_ContextTAJS) context = Config.Context_OneObjectTAJS
    else if (Shell.params.opt_Context1CallsiteAndObject) context = Config.Context_OneCallsiteAndObject
    else if (Shell.params.opt_Context2CallsiteAndObject) context = Config.Context_KCallsiteAndObject
    else if (Shell.params.opt_Context3CallsiteAndObject) context = Config.Context_KCallsiteAndObject
    else if (Shell.params.opt_Context4CallsiteAndObject) context = Config.Context_KCallsiteAndObject
    else if (Shell.params.opt_Context5CallsiteAndObject) context = Config.Context_KCallsiteAndObject
    else if (Shell.params.opt_Context1CallsiteAndIdentity) context = Config.Context_KCallsiteAndIdentity
    else if (Shell.params.opt_Context2CallsiteAndIdentity) context = Config.Context_KCallsiteAndIdentity
    else if (Shell.params.opt_Context3CallsiteAndIdentity) context = Config.Context_KCallsiteAndIdentity
    else if (Shell.params.opt_Context4CallsiteAndIdentity) context = Config.Context_KCallsiteAndIdentity
    else if (Shell.params.opt_Context5CallsiteAndIdentity) context = Config.Context_KCallsiteAndIdentity
    else if (Shell.params.opt_Context1CallsiteOrObject) context = Config.Context_OneCallsiteOrObject
    else if (Shell.params.opt_ContextIdentity) context = Config.Context_Identity

    Config.setContextSensitivityMode(context)

    // Context-sensitivity depth for k-callsite sensitivity
    if (Shell.params.opt_Context2Callsite) Config.setContextSensitivityDepth(2)
    else if (Shell.params.opt_Context3Callsite) Config.setContextSensitivityDepth(3)
    else if (Shell.params.opt_Context4Callsite) Config.setContextSensitivityDepth(4)
    else if (Shell.params.opt_Context5Callsite) Config.setContextSensitivityDepth(5)
    else if (Shell.params.opt_Context2CallsiteAndObject) Config.setContextSensitivityDepth(2)
    else if (Shell.params.opt_Context3CallsiteAndObject) Config.setContextSensitivityDepth(3)
    else if (Shell.params.opt_Context4CallsiteAndObject) Config.setContextSensitivityDepth(4)
    else if (Shell.params.opt_Context5CallsiteAndObject) Config.setContextSensitivityDepth(5)
    else if (Shell.params.opt_Context1CallsiteAndIdentity) Config.setContextSensitivityDepth(1)
    else if (Shell.params.opt_Context2CallsiteAndIdentity) Config.setContextSensitivityDepth(2)
    else if (Shell.params.opt_Context3CallsiteAndIdentity) Config.setContextSensitivityDepth(3)
    else if (Shell.params.opt_Context4CallsiteAndIdentity) Config.setContextSensitivityDepth(4)
    else if (Shell.params.opt_Context5CallsiteAndIdentity) Config.setContextSensitivityDepth(5)

    // Context-sensitivity for pre-analysis
    if (Shell.params.opt_PreContextSensitive || Shell.params.command == ShellParameters.CMD_PREANALYZE || Shell.params.command == ShellParameters.CMD_HTML_PRE) {
      if (!quiet) System.out.println("Context-sensitivity is turned on for pre-analysis.")
      Config.setPreContextSensitiveMode(true)
    }

    // Unrolling count
    Config.setDefaultUnrollingCount(Shell.params.opt_unrollingCount)
    Config.setDefaultForinUnrollingCount(Shell.params.opt_forinunrollingCount)

    // Unsound mode
    if (Shell.params.opt_Unsound) {
      Config.setUnsoundMode(Shell.params.opt_Unsound)
      System.out.println("Unsound mode enabled.")
    }

    // noStop mode
    if (Shell.params.opt_noStop) {
      Config.setNoStopMode(Shell.params.opt_noStop)
      System.out.println("noStop mode enabled.")
    }

    // for HTML
    if (Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_HTML_PRE ||
        Shell.params.command == ShellParameters.CMD_HTML_SPARSE ||
        Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
      if (Shell.params.FileNames.length > 1) throw new UserError("Only one HTML file supported at a time.")
      val low = fileName.toLowerCase
      if (!(low.endsWith(".html") || low.endsWith(".xhtml") || low.endsWith(".htm"))) throw new UserError("Not an HTML file.")
      // DOM mode
      Config.setDomMode
      if(Shell.params.opt_jQuery) Config.setJQueryMode
      if(Shell.params.opt_Domprop) Config.setDOMPropMode
      if(Shell.params.opt_disEvent) Config.setDisableEventMode
      if(Shell.params.opt_loop) Config.setLoopMode
    }

    // for Tizen
    if (Shell.params.opt_Tizen) {
      Config.setTizenMode
      System.out.println("Tizen mode enabled.")
    }


    // default setting for webapp bug detector
    if(Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
      Config.setDefaultForinUnrollingCount(1)
      // loop sensitivity
      Config.setLoopSensitiveMode(true)
      // call context depth : 10
      Config.setContextSensitivityMode(Config.Context_Loop)
      Config.setContextSensitivityDepth(10)
      // location cloning
      Shell.params.opt_LocClone = true
      locclone = true
      // dom property like 'innerHTML' update mode
      Shell.params.opt_Domprop
      Config.setDOMPropMode
      // use set domain with 32 size
      Shell.params.opt_MaxStrSetSize = 32
      if(Shell.params.opt_disEvent) {
        Config.setDisableEventMode 
      }
      // DOM mode
      Config.setDomMode
    }


    if (!quiet) System.out.println("Context-sensitivity mode is \"" + kr.ac.kaist.jsaf.analysis.typing.CallContext.getModeName + "\".")

    // Initialize
    val return_code = 0
    val analyzeStartTime = System.nanoTime
    if (!quiet) System.out.println("\n* Initialize *")

    // Initialize AbsString cache
    kr.ac.kaist.jsaf.analysis.typing.domain.AbsString.initCache

    // Read a JavaScript file and translate to IR
    var start = System.nanoTime
    var program: Program = null

    // for HTML
    var jshtml: JSFromHTML = null
    if (Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_HTML_PRE ||
        Shell.params.command == ShellParameters.CMD_HTML_SPARSE ||
        Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
        // DOMAPI statistics
        if(Shell.params.opt_Domstat){
           DOMStatistics.setInputFile(Shell.params.opt_Domstat_in) 
           DOMStatistics.setOutputFile(Shell.params.opt_Domstat_out) 
              
        }
      jshtml = new JSFromHTML(fileName)
      // Parse JavaScript code in the target html file
      program = jshtml.parseScripts
      // Pretty print JavaScript code
      if (Shell.params.opt_Pretty) {
        val code = JSAstToConcrete.doit(program)
        if (Shell.params.opt_PrettyFileName != null) {
          try {
            val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(Shell.params.opt_PrettyFileName)
            val (fw, writer) = (pair.first, pair.second)
            writer.write(code)
            writer.close
            fw.close
          }
          catch {
            case e: IOException => {
              throw new IOException("IOException " + e + "while writing " + Shell.params.opt_PrettyFileName)
            }
          }
        }
        else System.out.println(code)
        return 0;
      } 
    }
    else program = Parser.fileToAST(fileNames)

    // concatenate modeled ASTs
    val SEP = File.separator
    val base = ProjectProperties.BASEDIR + SEP
    val modeledFiles: List[String] =
      ((List[String](base + "bin/models/builtin/__builtin__.js")) ++
      (if(Config.domMode) List(base + "bin/models/dom/__dom__.js") else List[String]())).filter(f => {
        val file = new File(f)
        file.exists()
      })
    // input files for webapp bugdetector
    val inputFiles: List[String] =
      if(Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
        (List[String](base + "bin/inputs/__input__.js")).filter(f => {
          val file =new File(f);
          file.exists();
        })
      }
      else List()
    Config.setModeledFiles(Config.getModeledFiles ++ modeledFiles ++ inputFiles)
    val modeledASTs: Program = Parser.fileToAST(toJavaList(modeledFiles++inputFiles))
    program = (modeledASTs, program) match {
      case (SProgram(info0, STopLevel(fds0, vds0, body0)), SProgram(info1, STopLevel(fds1, vds1, body1))) =>
        SProgram(info1, STopLevel(fds0 ++ fds1, vds0 ++ vds1, body0 ++ body1))
    }

    val irErrors = Shell.ASTtoIR(fileName, program, JOption.none[String], JOption.none[kr.ac.kaist.jsaf.nodes_util.Coverage])
    val irOpt: JOption[IRRoot] = irErrors.first
    val program2: Program = irErrors.third // Disambiguated and hoisted and with written

    val irTranslationTime = (System.nanoTime - start) / 1000000000.0
    if (!quiet) printf("# Time for IR translation(s): %.2f\n", irTranslationTime)

    // Check the translation result
    if (irOpt.isNone) return -2
    val ir: IRRoot = irOpt.unwrap

    // Build CFG
    start = System.nanoTime
    val builder = new CFGBuilder(ir)
    val cfg = builder.build
    val cfgBuildingTime = (System.nanoTime - start) / 1000000000.0
    if (!quiet) {
      printf("# Time for CFG building(s): %.2f\n", cfgBuildingTime)
      printf("# Time for front end(s): %.2f\n", irTranslationTime + cfgBuildingTime)
    }
    val errors = builder.getErrors
    if (!(errors.isEmpty)) {
      Shell.reportErrors(NodeUtil.getFileName(ir), Shell.flattenErrors(errors), JOption.none[Pair[FileWriter, BufferedWriter]])
    }

    // Peak memory
    if (!quiet) {
      System.out.println("\n* Analyze *")
      printf("# Initial peak memory(mb): %.2f\n", MemoryMeasurer.peakMemory)
    }

    // Initialize bulit-in models
    val previousBasicBlocks: Int = cfg.getNodes.size
    start = System.nanoTime
    //val model: BuiltinModel = new BuiltinModel(cfg);
    //model.initialize();
    val init = new InitHeap(cfg)
    init.initialize

    val builtinModelInitializationTime = (System.nanoTime - start) / 1000000000.0
    val presentBasicBlocks = cfg.getNodes.size
    if (!quiet) {
      System.out.println("# Basic block(#): " + presentBasicBlocks)
      System.out.println("  # Source: " + previousBasicBlocks)
      System.out.println("  # Built-in: " + (presentBasicBlocks - previousBasicBlocks))
      printf("# Time for initial heap(s): %.2f\n", builtinModelInitializationTime)
    }

    // Set the initial state with DOM objects
    if (Config.domMode && jshtml != null) new DOMBuilder(cfg, init, jshtml.getDocument).initialize(quiet)

    if (Shell.params.command == ShellParameters.CMD_PREANALYZE ||
        Shell.params.command == ShellParameters.CMD_SPARSE ||
        Shell.params.command == ShellParameters.CMD_NEW_SPARSE ||
        Shell.params.command == ShellParameters.CMD_HTML_PRE ||
        Shell.params.command == ShellParameters.CMD_HTML_SPARSE) {
      cfg.computeReachableNodes(quiet)
    }

    // Create Typing
    var typingInterface: TypingInterface = null
    if (Shell.params.command == ShellParameters.CMD_ANALYZE ||
        Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_BUG_DETECTOR ||
        Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) typingInterface = new Typing(cfg, quiet, locclone)
    else if (Shell.params.command == ShellParameters.CMD_PREANALYZE) typingInterface = new PreTyping(cfg, quiet, true)
    else if (Shell.params.command == ShellParameters.CMD_HTML_PRE) typingInterface = new PreTyping(cfg, quiet, true)
    else if (Shell.params.command == ShellParameters.CMD_SPARSE) typingInterface = new SparseTyping(cfg, quiet, locclone)
    else if (Shell.params.command == ShellParameters.CMD_NEW_SPARSE ||
             Shell.params.command == ShellParameters.CMD_HTML_SPARSE) typingInterface = new DSparseTyping(cfg, quiet, locclone)
    else throw new UserError("Cannot create the Typing. The command is unknown.")
    Config.setTypingInterface(typingInterface)

    // Compare with Pre Analysis
    /*
    if (Config.compare && params.command != ShellParameters.CMD_PREANALYZE) {
      Config.setContextSensitivityMode(new Integer(Config.Context_Insensitive))
      val preTyping = new PreTyping(cfg, quiet)
      preTyping.analyze(model)
      Config.setPreTyping(preTyping.state)
      preTyping.dump
    }
    */

    // Check global variables in initial heap against list of predefined variables.
    init.checkPredefined

    // Analyze
    if (Shell.params.command == ShellParameters.CMD_ANALYZE ||
        Shell.params.command == ShellParameters.CMD_PREANALYZE ||
        Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_HTML_PRE ||
        Shell.params.command == ShellParameters.CMD_BUG_DETECTOR ||
        Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
      typingInterface.analyze(init)
    }
    else if (Shell.params.command == ShellParameters.CMD_SPARSE ||
             Shell.params.command == ShellParameters.CMD_NEW_SPARSE ||
             Shell.params.command == ShellParameters.CMD_HTML_SPARSE) {
      val preTyping = new PreTyping(cfg, quiet, false)
      preTyping.analyze(init)

      // unsound because states among instructions are omitted.
      val pre_result = preTyping.getMergedState
      // computes def/use set
      val access_start = System.nanoTime
      val duanalysis = new Access(cfg, preTyping.computeCallGraph, pre_result)
      duanalysis.process(quiet)
      val accessTime = (System.nanoTime - access_start) / 1000000000.0
      if (!quiet) printf("# Time for access analysis(s): %.2f\n", accessTime)

      val cg = preTyping.computeCallGraph()
      // computes def/use graph
      if (typingInterface.env != null) typingInterface.env.drawDDG(cg, duanalysis.result, quiet)

      // Analyze
      typingInterface.analyze(init, duanalysis.result)
    }

    if(!quiet || Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) 
      printf("\nAnalysis took %.2fs\n", (System.nanoTime - analyzeStartTime) / 1000000000.0)

    if(Shell.params.opt_Domstat){
       DOMStatistics.printResult()   
    }
    // Turn off '-max-loc-count' option
    Shell.params.opt_MaxLocCount = 0

    // Report a result
    if (!quiet) {
      printf("# Peak memory(mb): %.2f\n", MemoryMeasurer.peakMemory)
      printf("# Result heap memory(mb): %.2f\n", MemoryMeasurer.measureHeap)
    }
    if (Shell.params.opt_MemDump) {
      System.out.println("\n* Dump *")
      typingInterface.dump
      if (Shell.params.command == ShellParameters.CMD_PREANALYZE) typingInterface.dump_callgraph
    }
    if (Shell.params.opt_Visual && typingInterface.isInstanceOf[Typing]) {
      System.out.println("\n* Visualization *")
      val vs: Visualization = new Visualization(typingInterface.asInstanceOf[Typing], fileName, Shell.toOption(Shell.params.opt_OutFileName))
      vs.run(true)
    }

//    if (!quiet) {
//      System.out.println("\n* Statistics *")
//      System.out.println("# Total state count: " + typingInterface.getStateCount)
//      typingInterface.statistics(Shell.params.opt_StatDump)
//    }
    if (Shell.params.opt_CheckResult) {
      SemanticsTest.checkResult(typingInterface)
      System.out.println("Test pass")
    }

    // Print Coverages
    if(Shell.params.opt_FunctionCoverage) {
      val coverage = new kr.ac.kaist.jsaf.analysis.typing.Coverage
      coverage.set(typingInterface)
      println("\n* Function Coverage *\n")
      println(coverage.coveredFIDSetToString)
      println(coverage.notCoveredFIDSetToString)
    }

    // Bug Detector
    if(
      Shell.params.command == ShellParameters.CMD_HTML ||
//      Shell.params.command == ShellParameters.CMD_HTML_PRE ||
      Shell.params.command == ShellParameters.CMD_HTML_SPARSE ||
      Shell.params.command == ShellParameters.CMD_BUG_DETECTOR ||
      Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) {
      // Node relation set
      NodeRelation.set(program2, ir, cfg, quiet)

      // Execute Bug Detector
      System.out.println("\n* Bug Detector *")
      val detector = new BugDetector(program2, cfg, typingInterface, quiet, irErrors.second)
      if(!(Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR))
        StrictModeChecker.checkAdvanced(program2, cfg, detector.varManager, detector.stateManager)
      detector.detectBug
    }

    val isGlobalSparse = false
    if (Shell.params.opt_DDGFileName != null) {
      DotWriter.ddgwrite(cfg, typingInterface.env, Shell.params.opt_DDGFileName + ".dot", Shell.params.opt_DDGFileName + ".svg", "dot", false, isGlobalSparse)
    }
    if (Shell.params.opt_DDG0FileName != null) {
      DotWriter.ddgwrite(cfg, typingInterface.env, Shell.params.opt_DDG0FileName + ".dot", Shell.params.opt_DDG0FileName + ".svg", "dot", true, isGlobalSparse)
    }
    if (Shell.params.opt_FGFileName != null) {
      DotWriter.fgwrite(cfg, typingInterface.env, Shell.params.opt_FGFileName + ".dot", Shell.params.opt_FGFileName + ".svg", "dot", isGlobalSparse)
    }
    if (!quiet) System.out.println("Ok")
    
    return_code
  }
}
