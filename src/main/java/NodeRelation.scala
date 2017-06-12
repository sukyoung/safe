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
  // Root nodes
  ////////////////////////////////////////////////////////////////////////////////
  var astRoot:                                  Program = null
  var irRoot:                                   IRRoot = null
  var cfgRoot:                                  CFGNode = null

  ////////////////////////////////////////////////////////////////////////////////
  // Parent & Children relation
  ////////////////////////////////////////////////////////////////////////////////
  // AST's parent & children
  type ASTParentMap =                           MHashMap[ASTRootNode, ASTRootNode]
  type ASTChildMap =                            MHashMap[ASTRootNode, MLinkedHashSet[ASTRootNode]]
  var astParentMap:                             ASTParentMap = null
  var astChildMap:                              ASTChildMap = null

  // IR's parent & children
  type IRParentMap =                            MHashMap[IRNode, IRNode]
  type IRChildMap =                             MHashMap[IRNode, MLinkedHashSet[IRNode]]
  var irParentMap:                              IRParentMap = null
  var irChildMap:                               IRChildMap = null

  // CFG's parent & children
  type CFGParentMap =                           MHashMap[CFGNode, CFGNode]
  type CFGChildMap =                            MHashMap[CFGNode, MLinkedHashSet[CFGNode]]
  var cfgParentMap:                             CFGParentMap = null
  var cfgChildMap:                              CFGChildMap = null

  ////////////////////////////////////////////////////////////////////////////////
  // Sibling relation
  ////////////////////////////////////////////////////////////////////////////////
  type ASTSiblingMap =                          MHashMap[ASTRootNode, MLinkedHashSet[ASTRootNode]]
  var astSiblingMap:                            ASTSiblingMap = null

  ////////////////////////////////////////////////////////////////////////////////
  // AST <-> IR <-> CFG relation
  ////////////////////////////////////////////////////////////////////////////////
  // For AST -> (Set[IR], Set[CFG])
  type AST2IRMap =                              MHashMap[ASTRootNode, MLinkedHashSet[IRNode]]
  type AST2CFGMap =                             MHashMap[ASTRootNode, MLinkedHashSet[CFGNode]]
  var ast2irMap:                                AST2IRMap = null
  var ast2cfgMap:                               AST2CFGMap = null

  // For IR -> (AST, Set[CFG])
  type IR2ASTMap =                              MHashMap[IRNode, ASTRootNode]
  type IR2CFGMap =                              MHashMap[IRNode, MLinkedHashSet[CFGNode]]
  var ir2astMap:                                IR2ASTMap = null
  var ir2cfgMap:                                IR2CFGMap = null

  // For CFG -> (AST, IR)
  type CFG2ASTMap =                             MHashMap[CFGNode, ASTRootNode]
  type CFG2IRMap =                              MHashMap[CFGNode, IRNode]
  var cfg2astMap:                               CFG2ASTMap = null
  var cfg2irMap:                                CFG2IRMap = null

  ////////////////////////////////////////////////////////////////////////////////
  // IR nodes created
  ////////////////////////////////////////////////////////////////////////////////
  type IRSet =                                  MHashSet[IRNode]
  var irSet:                                    IRSet = null

  ////////////////////////////////////////////////////////////////////////////////
  // Reset & Set
  ////////////////////////////////////////////////////////////////////////////////
  var isSet                                     = false

  // Reset
  def reset(): Unit = {
    isSet = false

    // Root node
    astRoot = null
    irRoot = null
    cfgRoot = null

    // Parent & Child
    astParentMap = null
    astChildMap = null
    irParentMap = null
    irChildMap = null
    cfgParentMap = null
    cfgChildMap = null

    // Sibling
    astSiblingMap = null

    // AST <-> IR <-> CFG
    ast2irMap = null
    ast2cfgMap = null
    ir2astMap = null
    ir2cfgMap = null
    cfg2astMap = null
    cfg2irMap = null

    // IR nodes created
    irSet = null
  }

  // Set
  def set(ast: Program, ir: IRRoot, cfg: CFG, quiet: Boolean): Unit = {
    // Root node
    astRoot = ast
    irRoot = ir
    // cfgRoot = ?

    // Parent & Children
    astParentMap = new ASTParentMap
    astChildMap = new ASTChildMap
    irParentMap = new IRParentMap
    irChildMap = new IRChildMap
    cfgParentMap = new CFGParentMap
    cfgChildMap = new CFGChildMap

    // Sibling
    astSiblingMap = new ASTSiblingMap

    // AST <-> IR <-> CFG
    ast2irMap = new AST2IRMap
    ast2cfgMap = new AST2CFGMap
    ir2astMap = new IR2ASTMap
    ir2cfgMap = new IR2CFGMap
    cfg2astMap = new CFG2ASTMap
    cfg2irMap = new CFG2IRMap

    // IR nodes created
    //TODO MV newly added to eventually replace original maps
    irSet = new IRSet

    // Put AST's parent & children
    def putAST_AST(parent: Any, child: Any): Unit = {
      if(parent != null && !parent.isInstanceOf[ASTRootNode] || !child.isInstanceOf[ASTRootNode]) return
      val parentNode = if(parent.isInstanceOf[ASTRootNode]) parent.asInstanceOf[ASTRootNode] else null
      val childNode = child.asInstanceOf[ASTRootNode]
      astParentMap.put(childNode, parentNode)
      astChildMap.getOrElseUpdate(parentNode, new MLinkedHashSet).add(childNode)
    }

    // Put IR's parent & children
    def putIR_IR(parent: Any, child: Any): Unit = {
      if(parent != null && !parent.isInstanceOf[IRNode] || !child.isInstanceOf[IRNode]) return
      val parentNode = if(parent.isInstanceOf[IRNode]) parent.asInstanceOf[IRNode] else null
      val childNode = child.asInstanceOf[IRNode]
      irParentMap.put(childNode, parentNode)
      irChildMap.getOrElseUpdate(parentNode, new MLinkedHashSet).add(childNode)
    }

    // Put CFG's parent & children
    def putCFG_CFG(parent: Any, child: Any): Unit = {
      if(parent != null && !parent.isInstanceOf[CFGNode] || !child.isInstanceOf[CFGNode]) return
      val parentNode = if(parent.isInstanceOf[CFGNode]) parent.asInstanceOf[CFGNode] else null
      val childNode = child.asInstanceOf[CFGNode]
      cfgParentMap.put(childNode, parentNode)
      cfgChildMap.get(parentNode) match {
        case Some(childSet) => childSet.add(childNode)
        case None =>
          val childSet = new MLinkedHashSet[CFGNode]
          childSet.add(childNode)
          cfgChildMap.put(parentNode, childSet)
      }
    }

    // Put AST sibling
    def putASTSibling(siblings: List[Any]): Unit = {
      val siblingSet = new MLinkedHashSet[ASTRootNode]
      for(sibling <- siblings) {
        sibling match {
          case Some(sibling) => siblingSet.add(sibling.asInstanceOf[ASTRootNode])
          case None =>
          case _ => siblingSet.add(sibling.asInstanceOf[ASTRootNode])
        }
      }
      for(siblingNode <- siblingSet) astSiblingMap.put(siblingNode, siblingSet)
    }

    // Put AST <-> IR
    def putAST_IR(ir: Any): Unit = {
      if(!ir.isInstanceOf[IRNode]) return
      val irNode = ir.asInstanceOf[IRNode]
      NodeFactory.ir2ast(irNode) match {
        case Some(ast) =>
          val irSet = ast2irMap.getOrElseUpdate(ast, new MLinkedHashSet)
          if(!irSet.contains(irNode)) irSet.add(irNode)
          ir2astMap.get(irNode) match {
            case Some(_) => //throw new RuntimeException("Error!")
            case None => ir2astMap.put(irNode, ast)
          }
        case None =>
      }
    }

    // Put AST <-> CFG
    def putAST_CFG(cfg: Any, info: IRInfoNode): Unit = {
      if(!cfg.isInstanceOf[CFGNode]) return
      val cfgNode = cfg.asInstanceOf[CFGNode]
      NodeFactory.irinfo2ir(info) match {
        case Some(irNode) =>
          putIR_CFG(irNode, cfgNode)
          NodeFactory.ir2ast(irNode) match {
            case Some(ast) =>
              val cfgSet = ast2cfgMap.getOrElseUpdate(ast, new MLinkedHashSet)
              if(!cfgSet.contains(cfgNode)) cfgSet.add(cfgNode)
              cfg2astMap.get(cfgNode) match {
                case Some(_) => //throw new RuntimeException("Error!")
                case None => cfg2astMap.put(cfgNode, ast)
              }
            case None =>
          }
        case None =>
      }
    }

    // Put IR <-> CFG
    def putIR_CFG(irNode: IRNode, cfgNode: CFGNode): Unit = {
      val cfgSet = ir2cfgMap.getOrElseUpdate(irNode, new MLinkedHashSet)
      if(!cfgSet.contains(cfgNode)) cfgSet.add(cfgNode)
      cfg2irMap.get(cfgNode) match {
        case Some(_) => //throw new RuntimeException("Error!")
        case None => cfg2irMap.put(cfgNode, irNode)
      }
    }

    // Start time
    val startTime = System.nanoTime;

    // Walker object
    object NRWalker extends Walkers {
      // AST walk
      override def walkAST(parent: Any, node: Any): Unit = {
        // AST's parent & children
        putAST_AST(parent, node)
        // AST siblings
        node match {
          case list: List[_] => putASTSibling(list)
          case _ =>
        }

        super.walkAST(parent, node)
      }

      // IR walk
      override def walkIR(parent: Any, node: Any): Unit = {
        // IR's parent & children
        putIR_IR(parent, node)
        // AST <-> IR
        putAST_IR(node)

        super.walkIR(parent, node)
      }

      // CFG walk
      override def walkCFG(parent: Any, node: Any): Unit = {
        // CFG's parent & children
        putCFG_CFG(parent, node)
        // AST <-> CFG, IR <-> CFG
        node match {
          case CFGAlloc(iid, info, lhs, proto, addr) => putAST_CFG(node, info)
          case CFGAllocArray(iid, info, lhs, length, addr) => putAST_CFG(node, info)
          case CFGAllocArg(iid, info, lhs, length, addr) => putAST_CFG(node, info)
          case CFGExprStmt(iid, info, lhs, expr) => putAST_CFG(node, info)
          case CFGDelete(iid, info, lhs, expr) => putAST_CFG(node, info)
          case CFGDeleteProp(iid, info, lhs, obj, index) => putAST_CFG(node, info)
          case CFGStore(iid, info, obj, index, rhs) => putAST_CFG(node, info)
          case CFGFunExpr(iid, info, lhs, name, fid, addr1, addr2, addr3) => putAST_CFG(node, info)
          case CFGConstruct(iid, info, cons, thisArg, arguments, addr1, addr2) => putAST_CFG(node, info)
          case CFGCall(iid, info, fun, thisArg, arguments, addr1, addr2) => putAST_CFG(node, info)
          case CFGInternalCall(iid, info, lhs, fun, arguments, addr) => putAST_CFG(node, info)
          case CFGAssert(iid, info, expr, flag) => putAST_CFG(node, info)
          case CFGCatch(iid, info, name) => putAST_CFG(node, info)
          case CFGReturn(iid, info, expr) => putAST_CFG(node, info)
          case CFGThrow(iid, info, expr) => putAST_CFG(node, info)
          case CFGNoOp(iid, info, desc) => putAST_CFG(node, info)
          case CFGAsyncCall(iid, info, modelType, callType, addr1, addr2, addr3) => putAST_CFG(node, info)
          case CFGVarRef(info, id) => putAST_CFG(node, info)
          case CFGBin(info, first, op, second) => putAST_CFG(node, info)
          case CFGUn(info, op, expr) => putAST_CFG(node, info)
          case CFGLoad(info, obj, index) => putAST_CFG(node, info)
          case CFGThis(info) => putAST_CFG(node, info)
          case CFGUserId(info, text, kind, originalName, fromWith) => putAST_CFG(node, info)
          case _ =>
        }

        super.walkCFG(parent, node)
      }
    }

    // AST Walk
    NRWalker.walkAST(null, ast)
    // IR Walk
    NRWalker.walkIR(null, ir)
    // CFG Walk
    for(node <- cfg.getNodes.filter(n => cfg.isUserFunction(n._1))) {
      cfg.getCmd(node) match {
        case CFGCmdBlock(insts) => NRWalker.walkCFG(null, insts)
        case _ =>
      }
    }

    // Set flag
    isSet = true

    // Elapsed time
    if(!quiet) {
      val elapsedTime = (System.nanoTime - startTime) / 1000000000.0;
      System.out.format("# Time for node relation computation(s): %.2f\n", new java.lang.Double(elapsedTime))
    }

   //dump
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////////////////////////
  // Get UID
  def getUID(node: Any): Long = {
    node match {
      case ast: AbstractNode => ast.getUID
      case ast: ScopeBody => ast.getUID
      case ir: IRAbstractNode => ir.getUID
      case ir: IRExpr => ir.getUID
      case ir: IROp => ir.getUID
      case ir: IRInfoNode => ir.getUID
      case _ => 0
    }
  }

  // AST to String
  def astToString(ast: ASTRootNode): String = {
    ast match {
      case ast: ASTNode => JSAstToConcrete.doit(ast).replace('\n', ' ')
      case ast: ScopeBody => ""
    }
  }

  // IR to String
  def irToString(ir: IRNode): String = new JSIRUnparser(ir).doit.replace('\n', ' ')

  // CFG to String
  def cfgToString(cfg: CFGNode): String = cfg match {
    case inst: CFGInst => "[" + inst.getInstId + "] " + inst
    case _ => cfg.toString
  }

  // Used in BugStorage
  def getParentASTStmtOrCond(ast: ASTRootNode): ASTNode = {
    var node = ast
    while(true) {
      if(node == null) return null
      if(node.isInstanceOf[Stmt] || node.isInstanceOf[Cond]) return node.asInstanceOf[ASTNode]
      //println("AST" + node.getClass().getSimpleName() + '[' + getUID(node) + "] : " + astToString(node))
      node = astParentMap.getOrElse(node, null)
    }
    null
  }

  // Used in StrictModeChecker
  def getParentCFGInst(cfg: CFGNode): CFGInst = {
    var node = cfg
    while(true) {
      if(node == null) return null
      if(node.isInstanceOf[CFGInst]) return node.asInstanceOf[CFGInst]
      node = cfgParentMap.getOrElse(node, null)
    }
    null
  }

  // Is ancestor
  def isAncestor(ancestor: ASTRootNode, child: ASTRootNode): Boolean = {
    var node = child
    while(true) {
      if(node == null) return false
      astParentMap.get(node) match {
        case Some(parent) => if(parent == ancestor) return true else node = parent
        case None => return false
      }
    }
    false
  }
  def isAncestor(ancestor: IRNode, child: IRNode): Boolean = {
    var node = child
    while(true) {
      if(node == null) return false
      irParentMap.get(node) match {
        case Some(parent) => if(parent == ancestor) return true else node = parent
        case None => return false
      }
    }
    false
  }
  def isAncestor(ancestor: CFGNode, child: CFGNode): Boolean = {
    var node = child
    while(true) {
      if(node == null) return false
      cfgParentMap.get(node) match {
        case Some(parent) => if(parent == ancestor) return true else node = parent
        case None => return false
      }
    }
    false
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Dump
  ////////////////////////////////////////////////////////////////////////////////
  def dump(): Unit = {
    // Parent & Children
    println("astParentMap.size = " + astParentMap.size)
    println("astChildMap.size = " + astChildMap.size)
    println("irParentMap.size = " + irParentMap.size)
    println("irChildMap.size = " + irChildMap.size)
    println("cfgParentMap.size = " + cfgParentMap.size)
    println("cfgChildMap.size = " + cfgChildMap.size)
    println

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

    // AST <-> IR <-> CFG
    println("ast2irMap.size = " + ast2irMap.size)
    println("ast2cfgMap.size = " + ast2cfgMap.size)
    println("ir2astMap.size = " + ir2astMap.size)
    println("ir2cfgMap.size = " + ir2cfgMap.size)
    println("cfg2astMap.size = " + cfg2astMap.size)
    println("cfg2irMap.size = " + cfg2irMap.size)
    println

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

    // IR -> CFG
    println("*** IR -> CFG ***")
    for(keyValue <- ir2cfgMap) {
      val (ir, cfgList) = keyValue
      println(ir.getClass().getSimpleName() + '[' + getUID(ir) + "] : " + irToString(ir))
      for(cfg <- cfgList) println("    " + cfg.getClass().getSimpleName() + " : " + cfgToString(cfg))
    }
    println

    // CFG -> IR
    println("*** CFG -> IR ***")
    for(keyValue <- cfg2irMap) {
      val (cfg, ir) = keyValue
      println(cfg.getClass().getSimpleName() + " : " + cfgToString(cfg))
      println("    " + ir.getClass().getSimpleName() + '[' + getUID(ir) + "] : " + irToString(ir))
    }
    println
  }
}
