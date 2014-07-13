/******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io._
import java.nio.charset.Charset
import java.util.{ArrayList, HashMap}
import java.util.{List => JList}
import kr.ac.kaist.jsaf.analysis.cfg.{DotWriter, CFG, CFGBuilder}
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.analysis.typing.InitHeap
import kr.ac.kaist.jsaf.analysis.typing.PreTyping
import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.analysis.typing.Typing
import kr.ac.kaist.jsaf.analysis.typing.TypingInterface
import kr.ac.kaist.jsaf.analysis.typing.Worklist
import kr.ac.kaist.jsaf.analysis.typing.AddressManager
import kr.ac.kaist.jsaf.analysis.typing.models.DOMBuilder
import kr.ac.kaist.jsaf.analysis.visualization.Visualization
import kr.ac.kaist.jsaf.compiler.{Predefined, Parser}
import kr.ac.kaist.jsaf.bug_detector.{StateManager, BugDetector}
import kr.ac.kaist.jsaf.exceptions.{ParserError, UserError}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.useful.{Useful, MemoryMeasurer, Pair}
import kr.ac.kaist.jsaf.{Shell, ShellParameters}
import kr.ac.kaist.jsaf.useful.{Files, Pair}
import kr.ac.kaist.jsaf.tests.SemanticsTest
import kr.ac.kaist.jsaf.parser.TS
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.ts.{TSToString, TSTypeMap}
/*
import kr.ac.kaist.jsaf.ts.{TSToString, TSToDB}
*/
import edu.rice.cs.plt.tuple.{Option => JOption}
import xtc.parser.{ParseError, SemanticValue}
import org.w3c.dom.Node
import org.cyberneko.html.parsers.DOMParser

////////////////////////////////////////////////////////////////////////////////
// TypeScript
////////////////////////////////////////////////////////////////////////////////
object TSMain {
  ////////////////////////////////////////////////////////////////////////////////
  // Parse
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Parse a TypeScript file.
   */
  def tsparse: Int = {
    val return_code = 0
    if (Shell.params.FileNames.length == 0)
      throw new UserError("The tsparse command needs a file to parse.")
    val name = Shell.params.FileNames(0)
    val ts: Program = if (name.endsWith("d.ts")) parseTs(new File(name))
                      else throw new UserError("The tsparse command needs a TypeScript declaration file.")
    if (Shell.params.opt_OutFileName != null) ASTIO.writeJavaAst(ts, Shell.params.opt_OutFileName)
    else System.out.println(TSToString.doit(ts))
    return_code
  }

  def parseTs(str: String, fileName: String): Program = {
    val is = new ByteArrayInputStream(str.getBytes("UTF-8"))
    val ir = new InputStreamReader(is)
    val in = new BufferedReader(ir)
    val parser = new TS(in, fileName)
    val parseResult = parser.pSourceFile(0)
    in.close; ir.close; is.close
    if (parseResult.hasValue) {
      parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
    }
    else {
      System.out.println("TypeScript parsing failed.")
      throw new ParserError(parseResult.asInstanceOf[ParseError], parser, 0)
    }
  }

  def parseTs(file: File): Program = {
    try {
      val fs = new FileInputStream(file)
      val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
      val in = new BufferedReader(sr)
      val parser = new TS(in, file.getName)
      val parseResult = parser.pSourceFile(0)
      in.close; sr.close; fs.close
      if (parseResult.hasValue) {
        parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
      }
      else {
        System.out.println("TypeScript parsing failed.")
        throw new ParserError(parseResult.asInstanceOf[ParseError], parser, 0)
      }
    }
    catch {
      case f: FileNotFoundException => {
        throw new UserError(file.getName + " not found")
      }
    } finally {
      try {
        Files.rm(file.getName+".log")
      } catch { case ioe:IOException => }
    }
  }

  def tsparse(fileName: String): Int = {
    Shell.params.Clear
    Shell.params.FileNames = new Array[String](1)
    Shell.params.FileNames(0) = fileName
    tsparse
  }

  val SEP = File.separator
  def tscheck: Int = {
    val return_code  = 0
    val quiet = true
    val locclone = Shell.params.opt_LocClone

    // Initialize AddressManager
    AddressManager.reset()

    if (Shell.params.opt_JS.size == 0 && Shell.params.opt_HTML == null)
      throw new UserError("The tscheck command needs a file to check.")
    if (Shell.params.opt_JS.size > 0 && Shell.params.opt_HTML != null)
      throw new UserError("The tscheck command should not take both -js and -html options.")

    if (Shell.params.opt_Test) {
      Config.setTestMode(Shell.params.opt_Test)
      System.out.println("Test mode enabled.")
    }

    if (Shell.params.opt_Library) {
      Config.setLibMode(Shell.params.opt_Library)
      System.out.println("Library mode enabled.")
    }

    Config.setDomMode
    Config.setTSMode

    // Unrolling count
    Config.setDefaultUnrollingCount(Shell.params.opt_unrollingCount)

    // Unsound mode
    if (Shell.params.opt_Unsound) {
      Config.setUnsoundMode(Shell.params.opt_Unsound)
      System.out.println("Unsound mode enabled.")
    }

    // for Tizen
    if (Shell.params.opt_Tizen) {
      Config.setTizenMode
      System.out.println("Tizen mode enabled.")
    }

    if (!quiet) System.out.println("Context-sensitivity mode is \"" + kr.ac.kaist.jsaf.analysis.typing.CallContext.getModeName + "\".")

    // Turn on '-max-loc-count' option
    Shell.params.opt_MaxLocCount = 100

    // Initialize
    val analyzeStartTime = System.nanoTime
    if (!quiet) System.out.println("\n* Initialize *")

    // Read a JavaScript file and translate to IR
    var start = System.nanoTime

    var program: Program = null
    var htmlDoc: Node = null
    if (Shell.params.opt_HTML != null) {
      val jshtml: JSFromHTML = new JSFromHTML(Shell.params.opt_HTML)
      program = jshtml.parseScripts()
      htmlDoc = jshtml.getDocument()
    } else {
      program = Parser.fileToAST(Shell.params.opt_JS)
      val domParser = new DOMParser
      domParser.parse(new org.xml.sax.InputSource(new java.io.StringReader("<HTML></HTML>")))
      htmlDoc = domParser.getDocument
    }

    val fileName =
      if (Shell.params.opt_HTML != null) Shell.params.opt_HTML
      else Shell.params.opt_JS.get(0)

    //dumpAST(program)

    Shell.pred = new Predefined(Shell.params)
    if(Shell.params.opt_Domprop) Config.setDOMPropMode

    //program = rewriteWebapisConstructors(program)
    val irErrors = Shell.ASTtoIR(fileName, program, JOption.none[String], JOption.none[Coverage])
    val irOpt = irErrors.first
    val ast_n = irErrors.third // Disambiguated and hoisted and with written

    if(irOpt.isNone) return -2
    val ir = irOpt.unwrap

    // Build a CFG
    if (!quiet) System.out.println("rebuild cfg...")
    val builder = new CFGBuilder(ir)
    val cfg = builder.build
    NodeRelation.set(ast_n, ir, cfg, quiet)

    val errors = builder.getErrors
    if (!(errors.isEmpty)) {
      Shell.reportErrors(NodeUtil.getFileName(ir),
                         Shell.flattenErrors(errors),
                         JOption.none[Pair[FileWriter,BufferedWriter]])
    }

    // compare mode to test the html pre-analysis
    if(Shell.params.opt_Compare)
      Config.setCompareMode

    // Initialize AbsString cache
    kr.ac.kaist.jsaf.analysis.typing.domain.AbsString.initCache
    // Initialize Web IDL Libraries(Database) for InitHeap
    val DBFileNames = toList(Shell.params.opt_DB)
    if(DBFileNames.length>0)
        TSTypeMap.setLibrary(DBFileNames(0))

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
      System.out.println("# Basic block(#): " + previousBasicBlocks + "(source) + " + (presentBasicBlocks - previousBasicBlocks) + "(bulit-in) = " + presentBasicBlocks)
      printf("# Time for initial heap(s): %.2f\n", builtinModelInitializationTime)
    }

    // Set the initial state with DOM objects
    (new DOMBuilder(cfg, init, htmlDoc).initialize(true))

    /*
      // sparse
    cfg.computeReachableNodes(quiet)
    */

    // Check global variables in initial heap against list of predefined variables.
    //init.checkPredefined

    val cfgdump: Boolean = false
    if (cfgdump) cfg.dump

    // Analyze
    //val typingInterface: TypingInterface = new DSparseTyping(cfg, quiet, locclone) //sparse
    val typingInterface: TypingInterface = new Typing(cfg, quiet, locclone) //dense
    typingInterface.analyze(init) // dense

    /* sparse
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

    // computes def/use graph
    if (typingInterface.env != null) typingInterface.env.drawDDG(preTyping.computeCallGraph, duanalysis.result, quiet)

    // Analyze
    typingInterface.analyze(init, duanalysis.result)
    */

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
    }
    if (Shell.params.opt_Visual && typingInterface.isInstanceOf[Typing]) {
      System.out.println("\n* Visualization *")
      val vs: Visualization = new Visualization(typingInterface.asInstanceOf[Typing], fileName, Shell.toOption(Shell.params.opt_OutFileName))
      vs.run(true)
    }

    if (!quiet) {
      System.out.println("\n* Statistics *")
      System.out.println("# Total state count: " + typingInterface.getStateCount)
      typingInterface.statistics(Shell.params.opt_StatDump)
    }
    if (Shell.params.opt_CheckResult) {
      SemanticsTest.checkResult(typingInterface)
      System.out.println("Test pass")
    }

    // Print Coverages
    if(Shell.params.opt_FunctionCoverage) {
      val fcovFileName = ".fcov"
      val coverage = new kr.ac.kaist.jsaf.analysis.typing.Coverage
      val tmppair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(fcovFileName)
      val fw: FileWriter = tmppair.first()
      val writer: BufferedWriter = tmppair.second()
      coverage.set(typingInterface)
      writer.write(coverage.coveredFIDSetToString)
      writer.write(coverage.notCoveredFIDSetToString)
      System.out.println("Dumped function coverage to " + fcovFileName)
      writer.close()
      fw.close()
    }

    // Execute Bug Detector
    System.out.println("\n* Bug Detector *")
    NodeRelation.set(ast_n, ir, cfg, quiet)
    val detector = new BugDetector(ast_n, cfg, typingInterface, quiet, irErrors.second)
    detector.detectBug

    if (!quiet) printf("\nAnalysis took %.2fs\n", (System.nanoTime - analyzeStartTime) / 1000000000.0)

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
