/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{InitHeap, Helper, Config, CallContext}
import scala.collection.immutable.TreeMap
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.interpreter.InterpreterPredefine
import kr.ac.kaist.jsaf.nodes_util.IRFactory
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import org.w3c.dom._
import org.w3c.dom.Node
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject._

class DOMBuilder(cfg: CFG, init: InitHeap, document: Node) {
  val F = BoolFalse
  val T = BoolTrue
  val dummyInfo = IRFactory.makeInfo(IRFactory.dummySpan("DOM Object"))
  var initHeap = init.getInitHeap()

  var fset_dom = Map[FunctionId, String]()
  /**
   * Code for |> operator.
   */
  case class ToPipe[A](a: A) {
    def |>[B](f: A => B) = f(a)
  }
  implicit def convert[A](s: A) = ToPipe(s)

  private var uniqueNameCounter = 0
  private def freshName(name: String) = {
    uniqueNameCounter += 1
    "<>DOM<>" + name + "<>" + uniqueNameCounter.toString
  }

  // Initilize the event target table and the event function table
  private def initEventTables(h: Heap) : Heap =  {
    val eventTTable = h(EventTargetTableLoc)
    val eventTTable_update = eventTTable.update(
      AbsString.alpha("#LOAD"), PropValue(ObjectValueBot, ValueBot, FunSetBot)).update(
      AbsString.alpha("#UNLOAD"), PropValue(ObjectValueBot, ValueBot, FunSetBot)).update(
      AbsString.alpha("#KEYBOARD"), PropValue(ObjectValueBot, ValueBot, FunSetBot)).update(
      AbsString.alpha("#MOUSE"), PropValue(ObjectValueBot, ValueBot, FunSetBot)).update(
      AbsString.alpha("#OTHER"), PropValue(ObjectValueBot, ValueBot, FunSetBot))

/*
    val eventFTable = heap(EventFunctionTableLoc)
    val eventFTable_update = eventFTable.update(
      AbsString.alpha("#LOAD"), PropValue(ObjectValueBot, ValueBot, DOMHelper.temp_eventMap("#LOAD"))).update(
      AbsString.alpha("#UNLOAD"), PropValue(ObjectValueBot, ValueBot, DOMHelper.temp_eventMap("#UNLOAD"))).update(
      AbsString.alpha("#KEYBOARD"), PropValue(ObjectValueBot, ValueBot, DOMHelper.temp_eventMap("#KEYBOARD"))).update(
      AbsString.alpha("#MOUSE"), PropValue(ObjectValueBot, ValueBot, DOMHelper.temp_eventMap("#MOUSE"))).update(
      AbsString.alpha("#OTHER"), PropValue(ObjectValueBot, ValueBot, DOMHelper.temp_eventMap("#OTHER")))
*/
    h.update(EventTargetTableLoc, eventTTable_update) // + (EventFunctionTableLoc -> eventFTable_update)
  }
  
  // Set the initial state for the DOM HTML objects depending on the HTML source
  def initHtml(h: Heap): Heap = {
    // printDom(document, "")
    val h_1 = initEventTables(h)
    // dummy arguments
    val dummycontext = ContextBot
    val dummykey = (0, 0, 0)
    val ((h_2, ctx), absloc) = DOMHelper.buildDOMTree(h_1, dummycontext, document, cfg, None, None, None, dummykey, true)
    h_2
  }

  // Print the DOM tree
  def printDom(node: Node, indent: String): Unit = {
    System.out.println(indent + node.getNodeName + node.getNodeType + node.getClass().getName())
    var child : Node = node.getFirstChild
    while (child != null) {
      printDom(child, indent+" ")
      child = child.getNextSibling()
    }
  }

  def initialize(quiet: Boolean): Unit = {
    val s = System.nanoTime
    //val domModel = new DOMModel(cfg)
    // check start address of DOM Tree
    //cfg.setHtmlStartAddr
    // put DOM prototype and constructor objects in the initial heap
    val newheap = init.getInitHeap
    val h = initHtml(newheap) 
    // check end address of DOM Tree
    //cfg.setHtmlEndAddr
    if(!quiet) System.out.println("# Time for initial heap with DOM modeling(ms): "+(System.nanoTime - s) / 1000000.0)
    //builtinmodel.initHeap = Heap(m)
    init.setInitHeap(h)
    //fset_dom = domModel.fset_dom
  }
}
