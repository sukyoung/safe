/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.tests

import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import scala.collection.immutable.HashMap
import junit.framework.Assert.fail
import junit.framework.TestCase
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.CFGBuilder
import kr.ac.kaist.jsaf.analysis.cfg.LExit
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.{ModelManager, BuiltinModel}
import kr.ac.kaist.jsaf.compiler.Disambiguator
import kr.ac.kaist.jsaf.compiler.Hoister
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.compiler.Translator
import kr.ac.kaist.jsaf.compiler.WithRewriter
import kr.ac.kaist.jsaf.nodes.IRRoot
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.nodes_util.NodeRelation
import kr.ac.kaist.jsaf.scala_src.useful.Options._

class SemanticsTest(dir: String, tc: String, typing_mode: String) extends TestCase(tc) {
  // "pre", "dense", "sparse", "dsparse"

  // "no", "1callsite", "1obj", "1tajs"
  val CONTEXT_SENSITIVITY = "1callsite" 

  private class NullOutputStream extends OutputStream {
    override def write(x: Int) = () 
  }

  override def runTest() = {
    // silence stdout, stderr
    val oldOut = System.out
    val oldErr = System.err
    val nullStream = new PrintStream(new NullOutputStream())
    System.setErr(nullStream)
    System.setOut(nullStream)

    try {
      // Initialize AddressManager
      AddressManager.reset()
      val typing = analyze(new File(dir, tc))
      SemanticsTest.checkResult(typing);
    } finally {
      // recover stdout, stderr
      System.setErr(oldErr)
      System.setOut(oldOut)
    }
  }
  
  def analyze(file: File): TypingInterface = {
    // setup testing options
    Config.setTestMode(true)
    Config.setAssertMode(true)

    // parse
    var program: Program = Parser.parseFileConvertExn(file)

    // hoist
    val hoister = new Hoister(program);
    program = hoister.doit().asInstanceOf[Program]

    // disambiguate
    val disambiguator = new Disambiguator(program, false)
    program = disambiguator.doit().asInstanceOf[Program];

    // with rewrite
    val withRewriter = new WithRewriter(program, false);
    program = withRewriter.doit().asInstanceOf[Program]

    // translate to IR
    val translator = new Translator(program, toJavaOption(None));
    val ir: IRRoot = translator.doit().asInstanceOf[IRRoot];

    // build CFG
    val builder = new CFGBuilder(ir);
    val cfg: CFG = builder.build();

    NodeRelation.set(program, ir, cfg, true)

    // initialize heap
    //val model = new BuiltinModel(cfg)
    //model.initialize()
    val init = new InitHeap(cfg)
    init.initialize()

    // typing
    val typing: TypingInterface =
      typing_mode match {
        case "pre"     => cfg.computeReachableNodes(); new PreTyping(cfg, false, true)
        case "dense"   => new Typing(cfg, false, false)
        case "sparse"  => cfg.computeReachableNodes(); new SparseTyping(cfg, false, false)
        case "dsparse" => cfg.computeReachableNodes(); new DSparseTyping(cfg, false, false)
      }
    
    CONTEXT_SENSITIVITY match {
      case "no"        => Config.setContextSensitivityMode(Config.Context_Insensitive)
      case "1callsite" => Config.setContextSensitivityMode(Config.Context_OneCallsite)
      case "1obj"      => Config.setContextSensitivityMode(Config.Context_OneObject)
      case "1tajs"     => Config.setContextSensitivityMode(Config.Context_OneObjectTAJS)
    }
    
    Config.setTypingInterface(typing)
    Config.setAssertMode(true)
    //typing.analyze(init_heap)

    typing_mode match {
      case "pre" |"dense" =>
        typing.analyze(init)
      case "sparse" | "dsparse" =>
        // pre analysis
        val preTyping = new PreTyping(cfg, false, false);
        preTyping.analyze(init);
        val pre_result = preTyping.getMergedState
        // computes def/use set
        val duanalysis = new Access(cfg, preTyping.computeCallGraph(), pre_result);
        duanalysis.process();
        // computes def/use graph
        typing.env.drawDDG(preTyping.computeCallGraph(), duanalysis.result)
        // Analyze
        typing.analyze(init, duanalysis.result);
    }

    // return resulting Typing instance
    typing
  }
}

object SemanticsTest {
  val RESULT = "__result"
  val EXPECT = "__expect"

  def checkResult(typing: TypingInterface) = {
    // find global object at program exit node
    val state = 
      if (typing.isInstanceOf[PreTyping])
        typing.getMergedState
      else
        typing.readTable(((typing.cfg.getGlobalFId, LExit), CallContext.globalCallContext))
    val heap = state._1
    val map: Map[String, (PropValue, Absent)] =
      try {
        heap(GlobalLoc).asInstanceOf[Obj].map.toMap
      } catch {
        case _ =>
          fail("Global object is not found at program exit node")
          HashMap()
      }

    // collect result/expect values
    var resultMap: Map[Int, Value] = HashMap()
    var expectMap: Map[Int, Value] = HashMap()

    for ((prop, pvalue) <- map) {
      try {
        if (prop.startsWith(RESULT)) {
          val index = prop.substring(RESULT.length).toInt
          resultMap += (index -> pvalue._1._1._1)
        } else if (prop.startsWith(EXPECT)) {
          val index = prop.substring(EXPECT.length).toInt
          expectMap += (index -> pvalue._1._1._1)
        }
      } catch {
        case _ => fail("Invalid result/expect variable found: " + prop.toString)
      }
    }

    // invalid number of result/expect entries
    if (resultMap.size == 0) {
        fail("map.size: "+map.size+", resultMap : " +resultMap.toString)
    //  fail("No result/expect variable is detected " + map.size)
    }
    if (resultMap.size != expectMap.size)
      fail("Unmatched result/expect variable")

    // check expect <= result
    for ((index, result) <- resultMap.toSeq.sortBy(_._1)) {
      expectMap.get(index) match {
        case None =>
          fail("No corresponding expect variable is detected for " +
               RESULT +
               index.toString)
        case Some(expect) =>
          val success = expect <= result
          if (!success) {
            val sb = new StringBuilder
            sb.append(RESULT)
            sb.append(index.toString)
            sb.append(" = {")
            sb.append(DomainPrinter.printValue(result))
            sb.append("} >= {")
            sb.append(DomainPrinter.printValue(expect))
            sb.append("}")
            fail(sb.toString)
          }
      }
    }
  }
}
