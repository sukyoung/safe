/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.{Node => ASTRootNode, _}

import scala.collection.mutable.{HashMap => MHashMap, LinkedHashSet => MLinkedHashSet, HashSet => MHashSet}


object NodeRelation {

  ////////////////////////////////////////////////////////////////////////////////
  // Reset & Set
  ////////////////////////////////////////////////////////////////////////////////
  var isSet                                     = false

  // Reset
  def reset(): Unit = {
    isSet = false
  }

  // Set
  def set(ast: Program, ir: IRRoot, cfg: CFG, quiet: Boolean): Unit = {

    // Start time
    val startTime = System.nanoTime;

    // Set flag
    isSet = true

    // Elapsed time
    if(!quiet) {
      val elapsedTime = (System.nanoTime - startTime) / 1000000000.0;
      System.out.format("# Time for node relation computation(s): %.2f\n", new java.lang.Double(elapsedTime))
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Dump
  ////////////////////////////////////////////////////////////////////////////////

    // AST's parent
    /*println("*** AST's parent ***")
    for(keyValue <- astParentMap) {
      val (child, parent) = keyValue
      if(parent == null) println("AST no parent. (Root)")
      else println("AST" + parent.getClass().getSimpleName() + '[' + getUID(parent) + "] : " + astToString(parent))
      println("    AST" + child.getClass().getSimpleName() + '[' + getUID(child) + "] : " + astToString(child))
    }
    println*/

    // AST's children
    /*println("*** AST's children ***")
    for(keyValue <- astChildMap) {
      val (parent, childList) = keyValue
      if(parent == null) println("AST no parent. (Root)")
      else println("AST" + parent.getClass().getSimpleName() + '[' + getUID(parent) + "] : " + astToString(parent))
      for(child <- childList) println("    AST" + child.getClass().getSimpleName() + '[' + getUID(child) + "] : " + astToString(child))
    }
    println*/

    // AST's parent & children
    /*{
      println("*** AST's parent & children ***")
      var indent = 0
      def printAST(ast: ASTRootNode): Unit = {
        for(i <- 0 until indent) print(' ')
        println("AST" + ast.getClass.getSimpleName + '[' + getUID(ast) + ']')
        astChildMap.get(ast) match {
          case Some(children) => indent+= 2; for(child <- children) printAST(child); indent-= 2
          case None =>
        }
      }
      printAST(astRoot)
      println
    }*/

    // IR's parent
    /*println("*** IR's parent ***")
    for(keyValue <- irParentMap) {
      val (child, parent) = keyValue
      if(parent == null) println("IR no parent. (Root)")
      else println(parent.getClass().getSimpleName() + '[' + getUID(parent) + "] : " + irToString(parent))
      println("    " + child.getClass().getSimpleName() + '[' + getUID(child) + "] : " + irToString(child))
    }
    println*/

    // IR's children
    /*println("*** IR's children ***")
    for(keyValue <- irChildMap) {
      val (parent, childList) = keyValue
      if(parent == null) println("IR no parent. (Root)")
      else println(parent.getClass().getSimpleName() + '[' + getUID(parent) + "] : " + irToString(parent))
      for(child <- childList) println("    " + child.getClass().getSimpleName() + '[' + getUID(child) + "] : " + irToString(child))
    }
    println*/

    // CFG's parent
    /*println("*** CFG's parent ***")
    for(keyValue <- cfgParentMap) {
      val (child, parent) = keyValue
      if(parent == null) println("CFG no parent. (Root)")
      else println(parent.getClass().getSimpleName() + " : " + cfgToString(parent))
      println("    " + child.getClass().getSimpleName() + " : " + cfgToString(child))
    }
    println*/

    // CFG's children
    /*println("*** CFG's children ***")
    for(keyValue <- cfgChildMap) {
      val (parent, childList) = keyValue
      if(parent == null) println("CFG no parent. (Root)")
      else println(parent.getClass().getSimpleName() + " : " + cfgToString(parent))
      for(child <- childList) println("    " + child.getClass().getSimpleName() + " : " + cfgToString(child))
    }
    println*/

    // AST -> IR
    /*println("*** AST -> IR ***")
    for(keyValue <- ast2irMap) {
      val (ast, irList) = keyValue
      println("AST" + ast.getClass().getSimpleName() + '[' + getUID(ast) + "] : " + astToString(ast))
      for(ir <- irList) println("    " + ir.getClass().getSimpleName() + '[' + getUID(ir) + "] : " + irToString(ir))
    }
    println*/

    // IR -> AST
    /*println("*** IR -> AST ***")
    for(keyValue <- ir2astMap) {
      val (ir, ast) = keyValue
      println(ir.getClass().getSimpleName() + '[' + getUID(ir) + "] : " + irToString(ir))
      println("    AST" + ast.getClass().getSimpleName() + '[' + getUID(ast) + "] : " + astToString(ast))
    }
    println*/

    // AST -> CFG
    /*println("*** AST -> CFG ***")
    for(value <- ast2cfgMap) {
      val (ast, cfgList) = value
      println("AST" + ast.getClass().getSimpleName() + '[' + getUID(ast) + "] : " + astToString(ast))
      for(cfg <- cfgList) println("    " + cfg.getClass().getSimpleName() + " : " + cfgToString(cfg))
    }
    println*/

    // CFG -> AST
    /*println("*** CFG -> AST ***")
    for(value <- cfg2astMap) {
      val (cfg, ast) = value
      println(cfg.getClass().getSimpleName() + " : " + cfgToString(cfg))
      println("    AST" + ast.getClass().getSimpleName() + '[' + getUID(ast) + "] : " + astToString(ast))
    }
    println*/
}
