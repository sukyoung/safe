/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.cfg

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, IRFactory}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.ErrorLog
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.useful.HasAt
import scala.collection.immutable.HashMap
import scala.collection.mutable.{HashSet => MHashSet}
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.analysis.asserts.{ASSERTHelper => AH}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.{ModelManager, DOMHelper}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class CFGBuilder (ir: IRRoot) {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg:String, hasAt:HasAt) = errors.signal(msg, hasAt)
  def signal(hasAt:HasAt, msg:String) = errors.signal(msg, hasAt)
  def signal(error: StaticError) = errors.signal(error)
  def getErrors(): JList[StaticError] = toJavaList(errors.errors)

  val captured = new CapturedVariableCollector().collect(ir)
  val isCatchVar = MHashSet[String]()

  /* root rule : IRRoot -> CFG  */
  def build(): CFG = {
    ir match {
      case SIRRoot(info, fds, vds, stmts) =>
        val cfg = new CFG()
        val local_vars = namesOfFunDecls(fds) ++ namesOfVars(vds)
        val fid_global = cfg.newFunction("", Nil, local_vars, "top-level", info)
        cfg.setGlobalFId(fid_global)
        val node_start = cfg.newBlock(fid_global)
        cfg.addEdge((fid_global, LEntry), node_start)
        val ns1 = translateFunDecls(fds,cfg,List(node_start),fid_global)
        val lmap: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
        val (ns2, lmap1) = translateStmts(stmts, cfg, ns1, lmap, fid_global)

        cfg.addEdge(ns2,(fid_global,LExit))
        cfg.addExcEdge(lmap1("#throw").toList,(fid_global,LExitExc))
        cfg.addEdge(lmap1("#throw_end").toList,(fid_global,LExitExc))
        cfg.addEdge(lmap1("#after_catch").toList,(fid_global,LExitExc))

        // add top function
        if (Config.libMode)
          addTopFunction(cfg)

        cfg.setUserFuncCount()
        cfg
    }
  }

  /* fdvars rule : IRFunDecl list -> LocalVars
   * collects variable names from sequence of IRFunDecl, function "name" ... */
  private def namesOfFunDecls(fds: List[IRFunDecl]): List[CFGId] = {
    fds.foldLeft(List[CFGId]())((vars,fd) => vars ++ List(id2cfgId(fd.getFtn.getName)))
  }

  /* vd* rule : IRVar list -> LocalVars
   * collects variable names from sequence of IRVarStmt, var "name" */
  private def namesOfVars(vds: List[IRVarStmt]): List[CFGId] = {
    vds.foldLeft(List[CFGId]())((vars,vd) => vars ++ List(id2cfgId(vd.getLhs)))
  }

  // flatten IRSeq
  private def flatten(stmts: List[IRStmt]): List[IRStmt] =
    stmts.foldRight(List[IRStmt]())((s, l) =>
                                    if (s.isInstanceOf[IRSeq])
                                      toList(s.asInstanceOf[IRSeq].getStmts) ++ l
                                    else List(s) ++ l)

  /* arg* rule : IRStmt list -> ArgVars
   * collects variable names from sequence of IRLoad, "name" := arguments[n] */
  private def namesOfArgs(loads: List[IRStmt]): List[CFGId] = {
    // for Concolic testing
    // arguments may not be a list of IRExprStmts
    // because concolic testing uses compiler.IRSimplifier
    // to move IRBin, IRUn, and IRLoad out of IRExpr
    /*
    loads.asInstanceOf[List[IRExprStmt]].foldLeft(List[CFGId]())((args,load) => args ++ List(id2cfgId(load.getLhs)))
     */
    flatten(loads).foldLeft(List[CFGId]())((args,load) =>
                                           if (load.isInstanceOf[IRExprStmt]) {
                                             val name = load.asInstanceOf[IRExprStmt].getLhs
                                             if (name.isInstanceOf[IRUserId] ||
                                                 name.getOriginalName.startsWith("<>arguments"))
                                               args ++ List(id2cfgId(load.asInstanceOf[IRExprStmt].getLhs))
                                             else args
                                           } else args)
  }

  /* fd* rule : IRFunDecl list x CFG x Node list x FunctionId -> Node list */
  private def translateFunDecls(fds: List[IRFunDecl], cfg: CFG, nodes: List[Node], fid: FunctionId): List[Node] = {
    fds.foldLeft(nodes){case (tails,fd) => translateFunDecl(fd,cfg,tails,fid)}
  }

  // Collect event functions ids and store them in the temporary event map
  private def getEventFunId(name: String, fid: FunctionId): Unit = {
    // load event
    if(name=="__LOADEvent__") {
      val fid_set = DOMHelper.temp_eventMap("#LOAD")
      DOMHelper.temp_eventMap+=("#LOAD" -> (fid_set + fid))
    }
    // unload event
    else if(name=="__UNLOADEvent__") {
      val fid_set = DOMHelper.temp_eventMap("#UNLOAD")
      DOMHelper.temp_eventMap+=("#UNLOAD" -> (fid_set + fid))
    }
    // keyboard event
    else if(name=="__KEYBOARDEvent__") {
      val fid_set = DOMHelper.temp_eventMap("#KEYBOARD")
      DOMHelper.temp_eventMap+=("#KEYBOARD" -> (fid_set + fid))
    }
    // mouse event
    else if(name=="__MOUSEEvent__") {
      val fid_set = DOMHelper.temp_eventMap("#MOUSE")
      DOMHelper.temp_eventMap+=("#MOUSE" -> (fid_set + fid))
    }
    // other event
    else if(name=="__OTHEREvent__") {
      val fid_set = DOMHelper.temp_eventMap("#OTHER")
      DOMHelper.temp_eventMap+=("#OTHER" -> (fid_set + fid))
    }
    ()
  }

  /* fd rule : IRFunDecl x CFG x Node list x FunctionId -> Node list */
  private def translateFunDecl(fd: IRFunDecl, cfg: CFG, nodes: List[Node], fid: FunctionId): List[Node] = {
    fd match {
      case SIRFunDecl(irinfo, SIRFunctional(_,name,params,args,fds,vds,body)) =>
        val arg_vars = namesOfArgs(args)
        val local_vars = (namesOfFunDecls(fds) ++ namesOfVars(vds)).filterNot(arg_vars.contains)
        val fid_new = cfg.newFunction(id2cfgId(params(1)).toString, arg_vars, local_vars, name.getOriginalName, irinfo)

        // collect event function ids
        //if (Config.domMode)
        //  getEventFunId(name.getOriginalName, fid_new)

        val node_start = cfg.newBlock(fid_new)
        cfg.addEdge((fid_new, LEntry), node_start)
        val lmap: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
        val ns1 = translateFunDecls(fds,cfg,List(node_start),fid_new)
        val (ns2,lmap1) = translateStmts(body, cfg, ns1, lmap, fid_new)
        cfg.addEdge(ns2,(fid_new,LExit))
        if(Config.loopSensitive)
          lmap1("#return").toList.foreach(l=> cfg.addLoopReturnEdge(l, (fid_new, LExit)))
        else
          cfg.addEdge(lmap1("#return").toList,(fid_new,LExit))
        cfg.addExcEdge(lmap1("#throw").toList,(fid_new,LExitExc))
        cfg.addEdge(lmap1("#throw_end").toList,(fid_new,LExitExc))
        cfg.addEdge(lmap1("#after_catch").toList,(fid_new,LExitExc))
        val node_tail = getTail(cfg,nodes,fid)
        cfg.addInst(node_tail,
          CFGFunExpr(cfg.newInstId, irinfo, id2cfgId(name), None, fid_new,
            newProgramAddr(), newProgramAddr(), None))
        List(node_tail)
    }
  }

  /* stmt* rule : IRStmt list x CFG x Node list x LabelMap x FunctionId -> Node list x LabelMap */
  private def translateStmts(stmts: List[IRStmt], cfg: CFG, nodes: List[Node], lmap: Map[String, Set[Node]], fid: FunctionId, loop_node: Option[Node] = None, parentloops: Set[Node] = Set()) = {
    stmts.foldLeft((nodes,lmap)){case ((tails,lmap1),stmt) => translateStmt(stmt,cfg,tails,lmap1,fid, loop_node, parentloops)}
  }

  /* stmt rule : IRStmt x CFG x Node list x LabelMap x FunctionId -> Node list x LabelMap */
  private def translateStmt(stmt: IRStmt, cfg: CFG, nodes: List[Node], lmap: Map[String, Set[Node]],
                            fid: FunctionId, loop_node: Option[Node] = None, parentloops: Set[Node] = Set()): (List[Node], Map[String, Set[Node]]) = {
    stmt match {
      case SIRNoOp(irinfo, desc) =>
        val n = getTail(cfg, nodes, fid)
        val filename = irinfo.getSpan().getEnd().getFileName()
        if(desc == "StartOfFile") {
          // dynamic loading
          if(filename.contains("#loading#")){
            // entry
            val n_entry = cfg.newBlock(fid)
            cfg.addScriptStartNode(n_entry)
            cfg.addEdge(n, n_entry)
            val noop = CFGNoOp(cfg.newInstId, irinfo, desc)
            cfg.addInst(n_entry, noop)
            (List(n_entry), lmap)

          }
          else {            
            val noop = CFGNoOp(cfg.newInstId, irinfo, desc)
            cfg.addInst(n, noop)
            cfg.addFileNoOp(filename, noop)
            (List(n), lmap)
          }
        }
        else if(desc == "EndOfFile") {
          // dynamic loading
          if(filename.contains("#loading#")){
            val noop = CFGNoOp(cfg.newInstId, irinfo, desc)
            cfg.addInst(n, noop)
            cfg.addFileNoOp(filename, noop)
            cfg.addScriptEndNode(n)
            (List(n), lmap)
          }
          else {
            val noop = CFGNoOp(cfg.newInstId, irinfo, desc)
            cfg.addInst(n, noop)
            cfg.addFileNoOp(filename, noop)
            (List(n), lmap)
          }
        }
        else {
          val noop = CFGNoOp(cfg.newInstId, irinfo, desc)
          cfg.addInst(n, noop)
          cfg.addFileNoOp(filename, noop)
          (List(n), lmap)
        }
      case SIRStmtUnit(irinfo, stmts) =>
        translateStmts(stmts, cfg, nodes, lmap, fid, loop_node, parentloops)
      case SIRSeq(irinfo, stmts) =>
        translateStmts(stmts, cfg, nodes, lmap, fid, loop_node, parentloops)
      case vd:IRVarStmt =>
        signal("IRVarStmt should have been hoisted.", vd)
        (nodes, lmap)
      case fd:IRFunDecl =>
        signal("IRFunDecl should have been hoisted.", fd)
        (nodes, lmap)
      case SIRFunExpr(irinfo, lhs, SIRFunctional(_,name,params,args,fds,vds,body)) =>
        val arg_vars = namesOfArgs(args)
        val local_vars = (namesOfFunDecls(fds) ++ namesOfVars(vds)).filterNot(arg_vars.contains)
        val fid_new = cfg.newFunction(id2cfgId(params(1)).toString, arg_vars, local_vars, name.getOriginalName, irinfo)
        val node_start = cfg.newBlock(fid_new)
        cfg.addEdge((fid_new, LEntry), node_start)
        val lmap_new: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
        val ns1 = translateFunDecls(fds,cfg,List(node_start),fid_new)
        val (ns2,lmap1) = translateStmts(body, cfg, ns1, lmap_new, fid_new, None, parentloops)
        cfg.addEdge(ns2,(fid_new,LExit))
        if(Config.loopSensitive)
          lmap1("#return").toList.foreach(l=> cfg.addLoopReturnEdge(l, (fid_new, LExit)))
        else
          cfg.addEdge(lmap1("#return").toList,(fid_new,LExit))
        cfg.addExcEdge(lmap1("#throw").toList,(fid_new,LExitExc))
        cfg.addEdge(lmap1("#throw_end").toList,(fid_new,LExitExc))
        cfg.addEdge(lmap1("#after_catch").toList,(fid_new,LExitExc))
        val node_tail = getTail(cfg,nodes,fid)
        val nameCFGId = id2cfgId(name)
        if (nameCFGId.getVarKind == CapturedVar) {
          cfg.addInst(node_tail,
            CFGFunExpr(cfg.newInstId, irinfo, id2cfgId(lhs), Some(nameCFGId), fid_new,
              newProgramAddr(), newProgramAddr(), Some(newProgramAddr())))
        } else {
          cfg.addInst(node_tail,
            CFGFunExpr(cfg.newInstId, irinfo, id2cfgId(lhs), None, fid_new,
              newProgramAddr(), newProgramAddr(), None))
        }
        (List(node_tail), lmap)
      /* PEI : when proto is not object*/
      case SIRObject(irinfo, lhs, members, proto) =>
        val node_tail = getTail(cfg,nodes,fid)
        proto match {
          case None =>
            cfg.addInst(node_tail,
              CFGAlloc(cfg.newInstId, irinfo, id2cfgId(lhs), None, newProgramAddr()))
          case Some(p) =>
            cfg.addInst(node_tail,
              CFGAlloc(cfg.newInstId, irinfo,
                id2cfgId(lhs), Some(id2cfgExpr(p)), newProgramAddr()))
        }
        members.foreach((m) => translateMember(m, cfg, node_tail, lhs))
        (List(node_tail), lmap.updated("#throw", lmap("#throw") + node_tail))
      case SIRTry(irinfo, body, name, catchblock, finblock) =>
        (name, catchblock, finblock) match {
          case (Some(x), Some(catb), None) =>
            isCatchVar.add(x.getUniqueName)
            /* try block */
            val node1 = cfg.newBlock(fid)
            cfg.addEdge(nodes, node1)
            /* catch block */
            val node2 = cfg.newBlock(fid)
            cfg.addInst(node2, CFGCatch(cfg.newInstId, irinfo, id2cfgId(x)))
            /* initial label map */
            val lmap_try: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
            /* try body */
            val (ns1, lmap1) = translateStmt(body, cfg, List(node1), lmap_try, fid, loop_node, parentloops)

            cfg.addExcEdge(lmap1("#throw").toList, node2)
            cfg.addEdge(lmap1("#throw_end").toList, node2)
            cfg.addEdge(lmap1("#after_catch").toList, node2)
            /* catch body */
            val (ns2, lmap2) = translateStmt(catb, cfg, List(node2), lmap1.updated("#throw", Set()).updated("#throw_end", Set()).updated("#after_catch", Set()), fid, loop_node, parentloops)
            val lmap3 = lmap2.foldLeft(lmap)((m, kv) => {
              if (m.contains(kv._1))
                m.updated(kv._1, m(kv._1)++ kv._2)
              else
                m.updated(kv._1, kv._2)})
            /* tail nodes */
            val ns_tail =
              if (ns1.size == 1 && lmap1("#throw").contains(ns1.head)) {
                /* new tail node */
                val node_new = cfg.newBlock(fid)
                cfg.addEdge(ns1, node_new)
                cfg.addEdge(ns2, node_new)
                List(node_new)
              }
              else
                ns1 ++ ns2
            (ns_tail, lmap3)
          case (None, None, Some(finb)) =>
            /* try block */
            val node1 = cfg.newBlock(fid)
            cfg.addEdge(nodes, node1)
            /* finally block */
            val node2 = cfg.newBlock(fid)
            /* initial label map */
            val lmap_try: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
            /* build try block */
            val (ns1, lmap1) = translateStmt(body, cfg, List(node1), lmap_try, fid, loop_node, parentloops)
            /* build finally block */
            val (ns2, lmap2) = translateStmt(finb, cfg, List(node2), lmap, fid, loop_node, parentloops)
            /* edge : try -> finally */
            cfg.addEdge(ns1, node2)
            val lmap3 = (lmap1-"#after_catch").foldLeft(lmap2)((map, kv) => {
              if (!(kv._2.isEmpty)){
                val node_dup = cfg.newBlock(fid)
                val (ns, lm) = translateStmt(finb, cfg, List(node_dup), map, fid, loop_node, parentloops)
                if (kv._1 == "#throw"){
                  cfg.addEdge(lmap1("#after_catch").toList, node_dup)
                  cfg.addExcEdge(kv._2.toList, node_dup)
                  lm.updated("#throw_end", lm("#throw_end") ++ ns)
                }
                else {
                  cfg.addEdge(kv._2.toList, node_dup)
                  lm.updated(kv._1, lm(kv._1) ++ ns)
                }
              }
              else map})
            (ns2, lmap3)
          case (Some(x), Some(catb), Some(finb)) =>
            isCatchVar.add(x.getUniqueName)
            /* try block */
            val node1 = cfg.newBlock(fid)
            cfg.addEdge(nodes, node1)
            /* catch block */
            val node2 = cfg.newBlock(fid)
            cfg.addInst(node2, CFGCatch(cfg.newInstId, irinfo, id2cfgId(x)))
            /* finally block */
            val node3 = cfg.newBlock(fid)
            /* initial label map */
            val lmap_try: Map[String,Set[Node]]= HashMap("#return" -> Set(), "#throw" -> Set(), "#throw_end" -> Set(), "#after_catch" -> Set())
            /* build try block */
            val (ns1, lmap1) = translateStmt(body, cfg, List(node1), lmap_try, fid, loop_node, parentloops)
            /* exc edge : try -> catch */
            cfg.addExcEdge(lmap1("#throw").toList, node2)
            cfg.addEdge(lmap1("#throw_end").toList, node2)
            cfg.addEdge(lmap1("#after_catch").toList, node2)
            /* build catch block */
            val (ns2, lmap2) = translateStmt(catb, cfg, List(node2), lmap1.updated("#throw", Set()).updated("#throw_end", Set()).updated("#after_catch", Set()), fid, loop_node, parentloops)
            /* build finally block */
            val (ns3, lmap3) = translateStmt(finb, cfg, List(node3), lmap, fid, loop_node, parentloops)
            /* edge : try+catch -> finally */
            cfg.addEdge(ns1 ++ ns2, node3)
            val lmap4 = (lmap2-"#after_catch").foldLeft(lmap3)((map, kv) => {
              if (!(kv._2.isEmpty)){
                val node_dup = cfg.newBlock(fid)
                val (ns, lm) = translateStmt(finb, cfg, List(node_dup), map, fid, loop_node, parentloops)
                if (kv._1 == "#throw"){
                  cfg.addEdge(lmap2("#after_catch").toList, node_dup)
                  cfg.addExcEdge(kv._2.toList, node_dup)
                  lm.updated("#throw_end", lm("#throw_end") ++ ns)
                }
                else {
                  cfg.addEdge(kv._2.toList, node_dup)
                  lm.updated(kv._1, lm(kv._1) ++ ns)
                }
              }
              else map})
            (ns3, lmap4)
          case _ =>
            signal("Wrong IRTryStmt.", stmt)
            (nodes, lmap)
        }
      //      case SIRArgs(irinfo, lhs, elements) =>
      //        translateStmt(SIRArray(irinfo, lhs, elements), cfg, nodes, lmap, fid)
      /* PEI : element assign */
      case SIRArgs(irinfo, lhs, elements) =>
        val node_tail = getTail(cfg, nodes, fid)
        cfg.addInst(node_tail,
          CFGAllocArg(cfg.newInstId, irinfo,
            id2cfgId(lhs), elements.length, newProgramAddr()))
        val _ = elements.foldLeft(0){case (k, e) =>
          e match {
            case None => k+1
            case Some(e1) => translateElement(irinfo, e1, cfg, node_tail, lhs, k)
          }}
        (List(node_tail), lmap.updated("#throw", lmap("#throw") + node_tail))
      /* PEI : element assign */
      case SIRArray(irinfo, lhs, elements) =>
        val node_tail = getTail(cfg, nodes, fid)
        cfg.addInst(node_tail,
          CFGAllocArray(cfg.newInstId, irinfo,
            id2cfgId(lhs), elements.length, newProgramAddr()))
        val _ = elements.foldLeft(0){case (k, e) =>
          e match {
            case None => k+1
            case Some(e1) => translateElement(irinfo, e1, cfg, node_tail, lhs, k)
          }}
        (List(node_tail), lmap.updated("#throw", lmap("#throw") + node_tail))
      /* PEI : element assign */
      case SIRArrayNumber(irinfo, lhs, elements) =>
        val node_tail = getTail(cfg, nodes, fid)
        cfg.addInst(node_tail,
          CFGAllocArray(cfg.newInstId, irinfo,
            id2cfgId(lhs), elements.length, newProgramAddr()))
        val _ = elements.foldLeft(0){case (k, e) =>
          translateDoubleElement(irinfo, e, cfg, node_tail, lhs, k)
        }
        (List(node_tail), lmap.updated("#throw", lmap("#throw") + node_tail))
      case SIRBreak(irinfo, label) =>
        val key: String = if(Config.loopSensitive) {
          if(label.getOriginalName.contains("<>do<>")){
            label.getUniqueName + "<>do<>"
          }
          else label.getUniqueName
        }
          else label.getUniqueName
        val ns = lmap.get(key) match {
          case None    => nodes.toSet
          case Some(n) => n ++ nodes.toSet
        }
       loop_node match {
          case Some(l) =>
            nodes.toSet.foreach((n: Node) => cfg.addLoopCondEdge(l, n))
          case None => Unit
        }
   
        (Nil, lmap.updated(key, ns))
      /* PEI : fun == "<>toObject" */
      case SIRInternalCall(irinfo, lhs, fun@(SIRTmpId(_, originalName, uniqueName, _)), arg1, arg2) =>
        val n1 = getTail(cfg, nodes, fid)
        val (addr,lm) = if (uniqueName.equals("<>Global<>toObject") || uniqueName.equals("<>Global<>iteratorInit")) (Some(newProgramAddr()), lmap.updated("#throw", lmap("#throw")+n1)) else (None,lmap)
        val argslist = arg2 match {
          case None => List(ir2cfgExpr(arg1))
          case Some(arg) => List(ir2cfgExpr(arg1), id2cfgExpr(arg))
        }
        cfg.addInst(n1,
          CFGInternalCall(cfg.newInstId, irinfo,
            id2cfgId(lhs), id2cfgId(fun), argslist, addr))
        (List(n1), lm)
      /* PEI : call, after-call */
      case SIRCall(irinfo, lhs, fun, thisB, args) =>
        val n1 = getTail(cfg, nodes, fid)
        val addr1 = newProgramAddr()
        val addr2 = newProgramAddr()
        cfg.addInst(n1,
          CFGCall(cfg.newInstId, irinfo,
            id2cfgExpr(fun), id2cfgExpr(thisB), id2cfgExpr(args), addr1, addr2))
        val n2 = cfg.newAfterCallBlock(fid, id2cfgId(lhs))
        // after-catch
        val n3 = cfg.newAfterCatchBlock(fid)
        loop_node match {
          case Some(l) =>
            cfg.addLoopCondEdge(l, n1)
            cfg.addLoopCondEdge(l, n2)
          case None => Unit
        }
        cfg.addCall(n1, n2, n3)

        (List(n2), lmap.updated("#throw", lmap("#throw")+n1).updated("#after_catch", lmap("#after_catch")+n3))
      /* PEI : construct, after-call */
      case SIRNew(irinfo, lhs, cons, args) if (args.length == 2) =>
        val n1 = getTail(cfg, nodes, fid)
        val addr1 = newProgramAddr()
        val addr2 = newProgramAddr()
        cfg.addInst(n1,
          CFGConstruct(cfg.newInstId, irinfo,
            id2cfgExpr(cons), id2cfgExpr(args(0)), id2cfgExpr(args(1)), addr1, addr2))
        val n2 = cfg.newAfterCallBlock(fid, id2cfgId(lhs))
        // after-catch
        val n3 = cfg.newAfterCatchBlock(fid)
        cfg.addCall(n1, n2, n3)

        (List(n2), lmap.updated("#throw", lmap("#throw")+n1).updated("#after_catch", lmap("#after_catch")+n3))
      case c@SIRNew(irinfo, lhs, fun, args) =>
        signal("IRNew should have two elements in args.", c)
        (Nil, lmap)
      /* PEI : id lookup */
      case SIRDelete(irinfo, lhs, id) =>
        val n = getTail(cfg, nodes, fid)
        cfg.addInst(n, CFGDelete(cfg.newInstId, irinfo, id2cfgId(lhs), id2cfgExpr(id)))
        (List(n), lmap.updated("#throw", lmap("#throw") + n))
      /* PEI : id lookup */
      case SIRDeleteProp(irinfo, lhs, obj, index) =>
        val n = getTail(cfg, nodes, fid)
        cfg.addInst(n,
          CFGDeleteProp(cfg.newInstId, irinfo,
            id2cfgId(lhs), id2cfgExpr(obj), ir2cfgExpr(index)))
        (List(n), lmap.updated("#throw", lmap("#throw") + n))
      /* PEI : expr == IRId */
      case SIRExprStmt(irinfo, lhs, expr, _) =>
        val n = getTail(cfg, nodes, fid)
        cfg.addInst(n, CFGExprStmt(cfg.newInstId, irinfo, id2cfgId(lhs), ir2cfgExpr(expr)))
        /*        expr match {
                  case _:IRId => (List(n), lmap.updated("#throw", lmap("#throw") + n))
                  case _ => (List(n), lmap)
                } */
        /* XXX: temporal code for exception. */
        (List(n), lmap.updated("#throw", lmap("#throw") + n))

      case SIRIf(irinfo, cond, trueblock, falseblock) =>
        /* true block */
        val n1 = cfg.newBlock(fid)
        cfg.addEdge(nodes, n1)
        /* false block */
        val n2 = cfg.newBlock(fid)
        cfg.addEdge(nodes, n2)
        /* Insert assert instructions */
        val condinfo = cond.getInfo
        cfg.addInst(n1, CFGAssert(cfg.newInstId, condinfo, ir2cfgExpr(cond), true))
        cond match {
          case SIRBin(_, first, op, second) if AH.isAssertOperator(op) =>
            cfg.addInst(n2,
              CFGAssert(cfg.newInstId, condinfo,
                CFGBin(condinfo,
                  ir2cfgExpr(first), AH.transIROp(op), ir2cfgExpr(second)), false))
          case _ =>
            cfg.addInst(n2,
              CFGAssert(cfg.newInstId, condinfo,
                CFGUn(condinfo, IRFactory.makeOp("!"), ir2cfgExpr(cond)), false))
        }
        /* true block */
        val (ns1, lmap1) = translateStmt(trueblock, cfg, List(n1), lmap, fid, loop_node, parentloops)
        
        loop_node match {
          case Some(l) =>
            /* false block */
            val n_end = cfg.newBlock(fid)
            cfg.addCondEndNodeMap(n1, n_end)
            cfg.addCondEndNodeMap(n2, n_end)
            falseblock match {
              case None =>
                cfg.addEdge(ns1++List(n2), n_end)
                cfg.addLoopCondEdge(l, n_end)
                (List(n_end), lmap1.updated("#throw", lmap1("#throw") + n1 + n2))
              case Some(stmt) =>
                val (ns2, lmap2) = translateStmt(stmt, cfg, List(n2), lmap1, fid, loop_node, parentloops)
                cfg.addEdge(ns1++ns2, n_end)
                cfg.addLoopCondEdge(l, n_end)
                (List(n_end), lmap2.updated("#throw", lmap2("#throw") + n1 + n2))
          }

          case None =>
            /* false block */
            val n_end = cfg.newBlock(fid)
            cfg.addCondEndNodeMap(n1, n_end)
            cfg.addCondEndNodeMap(n2, n_end)
            falseblock match {
              case None =>
                cfg.addEdge(ns1++List(n2), n_end)
                //(ns1++List(n2), lmap1.updated("#throw", lmap1("#throw") + n1 + n2))
                (List(n_end), lmap1.updated("#throw", lmap1("#throw") + n1 + n2))
              case Some(stmt) =>
                val (ns2, lmap2) = translateStmt(stmt, cfg, List(n2), lmap1, fid, loop_node, parentloops)
                cfg.addEdge(ns1++ns2, n_end)
                //(ns1++ns2, lmap2.updated("#throw", lmap2("#throw") + n1 + n2))
                (List(n_end), lmap2.updated("#throw", lmap2("#throw") + n1 + n2))
            }

        }
      case SIRLabelStmt(irinfo, label, stmt) =>
        val n = cfg.newBlock(fid)
        val (ns1, lmap1) = translateStmt(stmt, cfg, nodes, lmap.updated(label.getUniqueName, Set()), fid, loop_node, parentloops)
        cfg.addEdge(ns1, n)
        if(Config.loopSensitive) {
          if(label.getOriginalName.contains("<>break<>")) {
            lmap1(label.getUniqueName).toList.foreach( l => cfg.addLoopBreakEdge(l, n))
            val dobreak = lmap1.get(label.getUniqueName + "<>do<>")
            if(!dobreak.isEmpty) {
              cfg.addEdge(dobreak.get.toList, n)  
            }
          }
          else if(label.getOriginalName.contains("<>continue<>"))
            lmap1(label.getUniqueName).toList.foreach( l => cfg.addLoopIterEdge(l, n))
          else 
            cfg.addEdge(lmap1(label.getUniqueName).toList, n)
        }
        else
          cfg.addEdge(lmap1(label.getUniqueName).toList, n)
        val lmap2 = lmap1 - label.getUniqueName

        (List(n), lmap2)
      /* PEI : expr lookup */
      case SIRReturn(irinfo, expr) =>
        val n = getTail(cfg, nodes, fid)
        expr match {
          case None => cfg.addInst(n, CFGReturn(cfg.newInstId, irinfo, None))
          case Some(x) => cfg.addInst(n, CFGReturn(cfg.newInstId, irinfo, Some(ir2cfgExpr(x))))
        }
        loop_node match {
          case Some(l) =>
            cfg.addLoopCondEdge(l, n)
          case None => Unit
        }

        (Nil, lmap.updated("#return", lmap("#return") + n).updated("#throw", lmap("#throw") + n))
      /* PEI : id lookup */
      case SIRStore(irinfo, obj, index, rhs) =>
        val n = getTail(cfg, nodes, fid)
        cfg.addInst(n,
          CFGStore(cfg.newInstId, irinfo,
            id2cfgExpr(obj), ir2cfgExpr(index), ir2cfgExpr(rhs)))
        (List(n), lmap.updated("#throw", lmap("#throw") + n))
      case SIRThrow(irinfo, expr) =>
        val n = getTail(cfg, nodes, fid)
        cfg.addInst(n, CFGThrow(cfg.newInstId, irinfo, ir2cfgExpr(expr)))
        (Nil, lmap.updated("#throw", lmap("#throw") + n))
      case SIRWhile(irinfo, cond, body) =>
        // Checks whether this while loop is originated from for-in or not.
        // TODO Need to find more graceful way.
        val bForin = body match {
          case SIRSeq(_, stmts) if stmts.size > 0 => stmts(0) match {
            case SIRInternalCall(_, _, fun@(SIRTmpId(_, _, "<>Global<>iteratorNext", _)), _, _) => true
            case _ => false
          }
          case _ => false
        }

        //if(bForin)
        // System.err.println("*Warning. For-in Loop")

        val unrollingCount =
          if (bForin) Config.defaultForinUnrollingCount
          else Config.defaultUnrollingCount

        if(Config.loopMode || Config.loopSensitive) {
          /* tail node */
          val n1 = getTail(cfg, nodes, fid)
          /* cond node */
          val n_cond = cfg.newBlock(fid)
          /* while loop head */
          val n_head = cfg.newBlock(fid)
          /* while loop end */
          val n_end = cfg.newBlock(fid)
          /* loop body */
          val n2 = cfg.newBlock(fid)
          /* loop out */
          val n3 = cfg.newBlock(fid)
          cfg.addCondEndNodeMap(n2, n3)
          /* Insert assert instruction */
          val condinfo = cond.getInfo
          cfg.addInst(n2, CFGAssert(cfg.newInstId, condinfo, ir2cfgExpr(cond), true))
          cond match {
            case SIRBin(_, first, op, second) if AH.isAssertOperator(op) =>
              cfg.addInst(n3,
                CFGAssert(cfg.newInstId, condinfo,
                  CFGBin(condinfo,
                    ir2cfgExpr(first), AH.transIROp(op), ir2cfgExpr(second)), false))
            case _ =>
              cfg.addInst(n3,
                CFGAssert(cfg.newInstId, condinfo,
                  CFGUn(condinfo, IRFactory.makeOp("!"), ir2cfgExpr(cond)), false))
          }
          /* add edge from tail to cond */
          cfg.addEdge(n1, n_head)
          cfg.addLoopEdge(n_head, n_cond)
          /* add edge from loop head to loop body */
          cfg.addEdge(n_cond, n2)
          /* add edge from loop head to out*/
          if(Config.loopMode)
            cfg.addEdge(n_cond, n3)
          else 
            cfg.addLoopOutEdge(n_cond, n3)
          /* build loop body */
          val (ns1, lmap1) = translateStmt(body, cfg, List(n2), lmap, fid, Some(n_cond), parentloops + n_cond)
          /* add edge from tails of loop body to loop head */
          // cfg.addEdge(ns1, n_head)
          // cfg.addEdge(ns1, n_cond)
          cfg.addEdge(ns1, n_end)
          cfg.addLoopIterEdge(n_end, n_cond)
          val lmap2 = lmap1.updated("#throw", lmap1("#throw") + n2 + n3 + n_cond)
          cfg.addInst(n_cond, CFGCond(cfg.newInstId, condinfo, ir2cfgExpr(cond), false))
          // (List(n_cond, n3), lmap2)
          cfg.addParentLoopNode(n_cond, parentloops)
          (List(n3), lmap2)

        }
        else {
        if(unrollingCount == 0) {
          /* tail node */
          val n1 = getTail(cfg, nodes, fid)
          /* while loop head */
          val n_head = cfg.newBlock(fid)
          /* loop body */
          val n2 = cfg.newBlock(fid)
          /* loop out */
          val n3 = cfg.newBlock(fid)
          /* Insert assert instruction */
          val condinfo = cond.getInfo
          cfg.addInst(n2, CFGAssert(cfg.newInstId, condinfo, ir2cfgExpr(cond), true))
          cond match {
            case SIRBin(_, first, op, second) if AH.isAssertOperator(op) =>
              cfg.addInst(n3,
                CFGAssert(cfg.newInstId, condinfo,
                  CFGBin(condinfo,
                    ir2cfgExpr(first), AH.transIROp(op), ir2cfgExpr(second)), false))
            case _ =>
              cfg.addInst(n3,
                CFGAssert(cfg.newInstId, condinfo,
                  CFGUn(condinfo, IRFactory.makeOp("!"), ir2cfgExpr(cond)), false))
          }
          /* add edge from tail to loop head */
          cfg.addEdge(n1, n_head)
          /* add edge from loop head to loop body */
          cfg.addEdge(n_head, n2)
          /* add edge from loop head to out*/
          cfg.addEdge(n_head, n3)
          /* build loop body */
          val (ns1, lmap1) = translateStmt(body, cfg, List(n2), lmap, fid)
          /* add edge from tails of loop body to loop head */
          cfg.addEdge(ns1, n_head)
          (List(n3), lmap1.updated("#throw", lmap1("#throw") + n2 + n3))
        }
        else {
          var updatedlmap = lmap
          def newBranchBlocks(headNode: Node): (BlockNode, BlockNode, List[Node]) = {
            val trueNode = cfg.newBlock(fid) // loop body
            val falseNode = cfg.newBlock(fid) // loop out

            /* Insert assert instruction */
            val condinfo = cond.getInfo
            cfg.addInst(trueNode, CFGAssert(cfg.newInstId, condinfo, ir2cfgExpr(cond), true))
            cond match {
              case SIRBin(_, first, op, second) if AH.isAssertOperator(op) =>
                cfg.addInst(falseNode,
                  CFGAssert(cfg.newInstId, condinfo,
                    CFGBin(condinfo,
                      ir2cfgExpr(first), AH.transIROp(op), ir2cfgExpr(second)), false))
              case _ =>
                cfg.addInst(falseNode,
                  CFGAssert(cfg.newInstId, condinfo,
                    CFGUn(condinfo, IRFactory.makeOp("!"), ir2cfgExpr(cond)), false))
            }

            /* build loop body */
            val (leafNodes, newlmap) = translateStmt(body, cfg, List(trueNode), updatedlmap, fid)
            updatedlmap = newlmap.updated("#throw", newlmap("#throw") + trueNode + falseNode)

            /* add edge from loop head to loop body */
            cfg.addEdge(headNode, trueNode)
            /* add edge from loop head to out*/
            cfg.addEdge(headNode, falseNode)

            (trueNode, falseNode, leafNodes)
          }

          /* while loop head */
          val headNode = cfg.newBlock(fid)
          /* (loop body, loop out, loop body's leaf nodes) */
          var (lastBodyNode, lastOutNode, lastLeafNodes) = newBranchBlocks(headNode)
          /* add edge from tails of loop body to loop head */
          cfg.addEdge(lastLeafNodes, headNode)

          /* tail node */
          var tailNode: Node = getTail(cfg, nodes, fid)
          /* unrolling */
          for(i <- 0 until unrollingCount) {
            /* (loop body, loop out, loop body's leaf nodes) */
            val (bodyNode, outNode, leafNodes) = newBranchBlocks(tailNode)
            /* add edge from unrolling out to last out*/
            cfg.addEdge(outNode, lastOutNode)
            if(leafNodes.length > 1) {
              tailNode = cfg.newBlock(fid)
              cfg.addEdge(leafNodes, tailNode)
            }
            else tailNode = leafNodes.head
          }
          /* add edge from unrolled tail to loop head */
          cfg.addEdge(tailNode, headNode)

          (List(lastOutNode), updatedlmap)
        }}
      case _ => {
        System.err.println("* Warning: following IR statement is ignored: "+ stmt)
        (nodes, lmap)
      }
    }
    /* statements */
    //case SIREval(irinfo, lhs, _, arg) => (Nil, label_map)
    //case SIRWith(irinfo, expr, stmt) => (Nil, label_map)
    //case SIRGetProp(irinfo, fun) => (Nil, label_map)
    //case SIRSetProp(irinfo, fun) => (Nil, label_map)
  }

  /* mem rule : IRField x CFG x Node x IRId -> Unit */
  private def translateMember(mem: IRMember, cfg: CFG, node: BlockNode, lhs: IRId): Unit = {
    mem match {
      case SIRField(irinfo, prop, expr) =>
        val lhs_expr = CFGVarRef(irinfo, id2cfgId(lhs))
        val index_expr = CFGString(prop.getUniqueName)
        cfg.addInst(node, CFGStore(cfg.newInstId, irinfo, lhs_expr, index_expr, ir2cfgExpr(expr)))
      case getOrSet =>
        signal("IRGetProp, IRSetProp is not supported.", getOrSet)
        Unit
    }
  }

  /* elem rule : IRSpanInfo x IRExpr x CFG x Node x IRId x Int-> Int */
  private def translateElement(irinfo: IRSpanInfo, elem: IRExpr, cfg: CFG, node: BlockNode, lhs: IRId, index: Int): Int  = {
    val lhs_expr = CFGVarRef(irinfo, id2cfgId(lhs))
    cfg.addInst(node,
      CFGStore(cfg.newInstId, irinfo,
        lhs_expr, CFGString(index.toString), ir2cfgExpr(elem)))
    (index + 1)
  }
  private def translateDoubleElement(irinfo: IRSpanInfo, elem: Double, cfg: CFG, node: BlockNode, lhs: IRId, index: Int): Int  = {
    val lhs_expr = CFGVarRef(irinfo, id2cfgId(lhs))
    cfg.addInst(node,
      CFGStore(cfg.newInstId, irinfo,
        lhs_expr, CFGString(index.toString),
        CFGNumber(elem.toString, javaToScalaDouble(elem))))
    (index + 1)
  }

  implicit def javaToScalaDouble(d: java.lang.Double) = d.doubleValue
  implicit def javaToScalaLong(l: java.lang.Long) = l.longValue
  private def isInternalCall(fname: String): Boolean = NU.isGlobalName(fname)
  private def ir2cfgExpr(expr: IRExpr): CFGExpr =
    expr match {
      /* PEI : id lookup */
      case SIRLoad(info, obj, index) =>
        CFGLoad(info, id2cfgExpr(obj), ir2cfgExpr(index))
      /* PEI : op \in {instanceof, in}, id lookup */
      case SIRBin(info, first, op, second) =>
        CFGBin(info, ir2cfgExpr(first), op, ir2cfgExpr(second))
      /* PEI : id lookup */
      case SIRUn(info, op, expr) =>
        CFGUn(info, op, ir2cfgExpr(expr))
      case id:IRId => CFGVarRef(id.getInfo, id2cfgId(id))
      case SIRThis(info) => CFGThis(info)
      case SIRNumber(_, text, num) => CFGNumber(text, javaToScalaDouble(num))
      case SIRString(_, str) => CFGString(str)
      case SIRBool(_, bool) => CFGBool(bool)
      case _:IRNull => CFGNull()
    }

  private def id2cfgExpr(id: IRId): CFGExpr = CFGVarRef(id.getInfo, id2cfgId(id))
  private def idList2cfgIdList(id: List[IRId]): List[CFGId] = id.map(id2cfgId)

  private var nameEnv: Map[String, String] = HashMap()
  private var uniqueNameCounter = 0
  private def id2cfgId(id: IRId): CFGId = {
    val text = id.getUniqueName
    nameEnv.get(text) match {
      case Some(s) =>
        // previously mapped name
        id match {
          case id:IRUserId =>
            if (id.isGlobal)
              CFGUserId(id.getInfo, s, GlobalVar, id.getOriginalName, id.isWith)
            else if (captured(text)) {
              if (isCatchVar(text))
                CFGUserId(id.getInfo, s, CapturedCatchVar, id.getOriginalName, id.isWith)
              else CFGUserId(id.getInfo, s, CapturedVar, id.getOriginalName, id.isWith)
            }
            else CFGUserId(id.getInfo, s, PureLocalVar, id.getOriginalName, id.isWith)
          case id:IRTmpId =>
            if (id.isGlobal) CFGTempId(s, GlobalVar)
            else CFGTempId(s, PureLocalVar)
        }
      case None =>
        val name =
          if (!NU.isInternal(text) || NU.isGlobalName(text)) {
            text
          } else {
            val text1 = text.dropRight(NU.significantBits)
            uniqueNameCounter += 1
            text1 + uniqueNameCounter.toString
          }
        nameEnv += (text -> name)
        id match {
          case id:IRUserId =>
            if (id.isGlobal)
              CFGUserId(id.getInfo, name, GlobalVar, id.getOriginalName, id.isWith)
            else if (captured(text)) {
              if (isCatchVar(text))
                CFGUserId(id.getInfo, name, CapturedCatchVar, id.getOriginalName, id.isWith)
              else CFGUserId(id.getInfo, name, CapturedVar, id.getOriginalName, id.isWith)
            }
            else CFGUserId(id.getInfo, name, PureLocalVar, id.getOriginalName, id.isWith)
          case id:IRTmpId =>
            if (id.isGlobal) CFGTempId(name, GlobalVar)
            else CFGTempId(name, PureLocalVar)
        }
    }
  }

  /* getTail : CFG x Node list x FunctionId -> BlockNode */
  private def getTail(cfg:CFG, nodes: List[Node], fid: FunctionId): BlockNode = {
    nodes match {
      case Nil =>
        val node_new = cfg.newBlock(fid)
        node_new
      case node::Nil =>
        nodes.head.asInstanceOf[BlockNode]
      case _ =>
        val node_new = cfg.newBlock(fid)
        nodes.foreach((node) => cfg.addEdge(node, node_new))
        node_new
    }
  }

  def addTopFunction(cfg: CFG): Unit = {
    val dummy_info = IRFactory.makeInfo(IRFactory.dummySpan("TopFunction"))
    cfg.addTopFunction("", Nil, Nil, "top-function", dummy_info)
    val node = cfg.newBlock(FIdTop)
    cfg.addInst(node, CFGReturn(cfg.newInstId, dummy_info, Option(CFGVarRef(dummy_info, CFGTempId("<>TopVal<>", GlobalVar)))))
    cfg.addEdge((FIdTop, LEntry), node)
    cfg.addEdge(node, (FIdTop, LExit))
    cfg.addExcEdge(node, (FIdTop, LExitExc))
  }
}

