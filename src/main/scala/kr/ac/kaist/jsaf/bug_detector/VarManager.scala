/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.mutable.{HashSet => MHashSet}
import scala.collection.mutable.Queue
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.SemanticsExpr
import kr.ac.kaist.jsaf.nodes.IRSpanInfo
import kr.ac.kaist.jsaf.nodes_util.IRFactory
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.nodes_util.SourceLocRats
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class VarManager(bugDetector: BugDetector) {
  ////////////////////////////////////////////////////////////////////////////////
  // From BugDetector
  ////////////////////////////////////////////////////////////////////////////////
  val cfg                                       = bugDetector.cfg
  val semantics                                 = bugDetector.semantics

  ////////////////////////////////////////////////////////////////////////////////
  // Dummy source locations
  ////////////////////////////////////////////////////////////////////////////////
  // Use only when we cannot get any meaningful source locations
  val sourceLoc                                 = new SourceLocRats(NodeUtil.freshFile("bug_detector"), 0, 0, 0)
  val defSpan                                   = new Span(sourceLoc, sourceLoc)
  val defInfo                                   = IRFactory.makeSpanInfo(false, defSpan)
  val defId                                     = IRFactory.dummyIRId("bug_detector")

  ////////////////////////////////////////////////////////////////////////////////
  // Locations & Variables
  ////////////////////////////////////////////////////////////////////////////////
  val locInfoMap                                = new MHashMap[Loc, LocInfo]()
  val varAssignStack                            = new MHashMap[BugVar0, BugVar0]
  val varAssignStackR                           = new MHashMap[BugVar0, MHashSet[BugVar0]] // Reverse

  def insertInfo(node: Node, inst: CFGInst, state: State): Unit = {
    inst match {
      case CFGAlloc(_, _, lhs, _, addr) =>
        insertLocInfo(addr, inst, inst, lhs)
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGAllocArray(_, _, lhs, _, addr) =>
        insertLocInfo(addr, inst, inst, lhs)
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGAllocArg(_, _, lhs, _, addr) =>
        insertLocInfo(addr, inst, inst, lhs)
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGExprStmt(_, _, lhs, right) =>
        val objLocSet = SemanticsExpr.V(right, state.heap, state.context)._1.locset
        objLocSet.foreach(objLoc => {
          insertLocInfo(locToAddr(objLoc), null, inst, lhs)
        })
        insertVarInfo(BugVar0(lhs, null), CFGExprToBugVar0(right))
      case CFGDelete(_, _, lhs, expr) =>
        insertVarInfo(BugVar0(lhs, null), CFGExprToBugVar0(expr))
      case CFGDeleteProp(_, _, lhs, obj, index) =>
        if(obj.isInstanceOf[CFGVarRef]) insertVarInfo(BugVar0(lhs, null), BugVar0(obj.asInstanceOf[CFGVarRef].id, index))
      case CFGStore(_, _, obj, index, right) =>
        if(obj.isInstanceOf[CFGVarRef]) {
          insertVarInfo(BugVar0(obj.asInstanceOf[CFGVarRef].id, index), CFGExprToBugVar0(right))
        }
      case CFGFunExpr(_, _, lhs, _, _, addr1, addr2, addr3) =>
        insertLocInfo(addr1, inst, inst, lhs) // function object
        val lhsInfo: IRSpanInfo = lhs match {
          case id: CFGUserId => id.info
          case _ => defInfo
        }
        insertLocInfo(addr2, inst, inst, null, CFGVarRef(lhsInfo, lhs), CFGString("prototype")) // function.prototype object
        /*if(addr3.isDefined) {
          throw new RuntimeException("TODO: Captured variable(?) is found.")
          //insertLocInfo(addr3.get, inst)
        }*/
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGConstruct(_, _, _, _, _, addr1, addr2) =>
        val lhs: CFGId = cfg.getReturnVar(cfg.getAftercallFromCall(node)).get
        insertLocInfo(addr1, inst, inst, lhs)
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGCall(_, _, _, _, _, addr1, addr2) =>
        val lhs: CFGId = cfg.getReturnVar(cfg.getAftercallFromCall(node)).get
        insertLocInfo(addr1, inst, inst, lhs)
        insertVarInfo(BugVar0(lhs, null), null)
      case CFGInternalCall(_, _, lhs, funcName, args, addr) =>
        if(addr.isDefined) {
          insertLocInfo(addr.get, inst, inst, lhs)
          funcName.getText match {
            case "<>Global<>toObject" | "<>Global<>toNumber" =>
              if(args.length > 0) insertVarInfo(BugVar0(lhs, null), CFGExprToBugVar0(args.head))
            case _ => insertVarInfo(BugVar0(lhs, null), null)  // TODO temporal code for <>iteratorInit.
          }
        }
        else insertVarInfo(BugVar0(lhs, null), null)
      case CFGAPICall(_, model, fun, args) =>
        //throw new RuntimeException("TODO: CFGAPICall(?)")
        //insertLocInfo(addr1, inst)
        //insertLocInfo(addr2, inst)
        //insertLocInfo(addr3, inst)
        //insertLocInfo(addr4, inst)
      case CFGCatch(_, _, name) =>
        insertVarInfo(BugVar0(name, null), null)
      case CFGAsyncCall(_, _, _, _, addr1, addr2, addr3) =>
        //throw new RuntimeException("TODO: CFGAsyncCall(?)")
        //insertLocInfo(addr1, inst)
        //insertLocInfo(addr2, inst)
        //insertLocInfo(addr3, inst)
      case _ =>
        // CFGAssert, CFGReturn, CFGThrow, CFGNoOp
    }
  }

  private def insertLocInfo(loc: Loc, createdInst: CFGInst, assignedInst: CFGInst, assignedId: CFGId = null, assignedObjExpr: CFGExpr = null, assignedIndexExpr: CFGExpr = null): Unit = {
    // Get or create a new LocInfo
    val locInfo: LocInfo = locInfoMap.get(loc) match {
      case Some(locInfo) => locInfo 
      case None =>
        val locInfo = new LocInfo(loc)
        locInfoMap.put(loc, locInfo)
        locInfo
    }

    // Set the instruction point that create new location
    if(createdInst != null) {
      if(locInfo.createdInst != null && locInfo.createdInst != createdInst) {
        val oldCreatedInstStr = "#" + locInfo.createdInst.getInstId + " " + locInfo.createdInst.toString()
        val newCreatedInstStr = "#" + createdInst.getInstId + " " + createdInst.toString()
        throw new RuntimeException("Same location number cannot be created at two or more instructions.\nold : " + oldCreatedInstStr + "\nnew : " + newCreatedInstStr)
      }
      locInfo.createdInst = createdInst
    }

    if(assignedInst != null) {
      // id = ...;
      if(assignedId != null) {
        locInfo.containingVar.add((assignedInst, BugVar0(assignedId, null)))
      }
      // obj[index] = ...;
      else if(assignedObjExpr != null && assignedObjExpr.isInstanceOf[CFGVarRef] && assignedIndexExpr != null) {
        val assignedObjId = assignedObjExpr.asInstanceOf[CFGVarRef].id
        locInfo.containingVar.add((assignedInst, BugVar0(assignedObjId, assignedIndexExpr)))
      }
    }
  }

  private def insertVarInfo(lhsVar: BugVar0, rhsVar: BugVar0): Unit = {
    varAssignStack.get(lhsVar) match {
      case Some(prevRHSVar) =>
        if(prevRHSVar != null && (rhsVar == null || prevRHSVar.id != rhsVar.id || prevRHSVar.index != rhsVar.index)) varAssignStack.put(lhsVar, null)
      case None => varAssignStack.put(lhsVar, rhsVar)
    }
    val lhsVarSet = varAssignStackR.getOrElseUpdate(rhsVar, new MHashSet)
    lhsVarSet.add(lhsVar)
  }

  def getUserVarAssign(id: CFGId): BugVar0 = getUserVarAssign(CFGVarRef(null, id))
  def getUserVarAssign(obj: CFGExpr, index: CFGExpr): BugVar0 = getUserVarAssign(CFGLoad(null, obj, index))
  def getUserVarAssign(expr: CFGExpr): BugVar0 = {
    var name: BugVar0 = CFGExprToBugVar0(expr)
    while(true) {
      if(name == null) return null
      name.id match {
        case _: CFGUserId => return name
        case CFGTempId(text, kind) if text.startsWith("<>arguments<>") => return BugVar0(CFGUserId(null, text, GlobalVar, "arguments", false), null)
        case _ =>
      }
      if(name.index != null) {
        val objName = getUserVarAssign(name.id)
        if(objName != null) {
          if(objName.index != null) {
            val index: String = objName.index.toString.replaceAll("\"", "")
            return BugVar0(CFGUserId(null, index, GlobalVar, index, false), name.index)
          }
          else return BugVar0(objName.id, name.index)
        }
      }
      varAssignStack.get(name) match {
        case Some(n) => name = n
        case None => return null
      }
    }
    null
  }

  def getUserVarAssignR(id: CFGId): MHashSet[BugVar0] = getUserVarAssignR(CFGVarRef(null, id))
  def getUserVarAssignR(expr: CFGExpr): MHashSet[BugVar0] = {
    val varSet = new MHashSet[BugVar0]
    val checkedSet = new MHashSet[BugVar0]
    val checkList = new Queue[BugVar0]
    checkList.enqueue(CFGExprToBugVar0(expr))
    while(!checkList.isEmpty) {
      val name: BugVar0 = checkList.dequeue
      val objName: BugVar0 = if(!name.id.isInstanceOf[CFGUserId] && name.index != null) getUserVarAssign(name.id) else null
      name match {
        case _ if name.id.isInstanceOf[CFGUserId] => varSet.add(name)
        case _ if objName != null =>
          if(objName.index != null) {
            val index: String = objName.index.toString.replaceAll("\"", "")
            varSet.add(BugVar0(CFGUserId(null, index, GlobalVar, index, false), name.index))
          }
          else varSet.add(BugVar0(objName.id, name.index))
        case _ =>
          varAssignStackR.get(name) match {
            case Some(lhsVarSet) =>
              for(lhsVar <- lhsVarSet) {
                if(lhsVar != null && !checkedSet.contains(lhsVar)) {
                  checkedSet.add(lhsVar)
                  checkList.enqueue(lhsVar)
                }
              }
            case None =>
          }
      }
    }
    varSet
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Dump
  ////////////////////////////////////////////////////////////////////////////////
  def dump(): Unit = {
    // LocInfo dump
    println("********************************************")
    println("* (Location -> Assigned variable set) Dump *");
    println("********************************************")
    // Sort by location number
    for((loc, locInfo) <- locInfoMap.toSeq.sortBy(_._1)) {
      print("#" + loc + " = {")
      var isFirst = true
      for((inst, bugVar) <- locInfo.containingVar) {
        if(isFirst) isFirst = false
        else print(", ")
        print(bugVar)
        if(inst == locInfo.createdInst) print("*")
      }
      println("}")
    }
    println

    // Variable assignment stack dump
    println("**********************************")
    println("* Variable assignment stack Dump *");
    println("**********************************")
    for((lhsVar, rhsVar) <- varAssignStack) {
      print(lhsVar + " <= ")
      if(rhsVar == null) println("?")
      else println(rhsVar)
    }
    println

    // Variable assignment stack reverse dump
    println("******************************************")
    println("* Variable assignment stack reverse Dump *");
    println("******************************************")
    for((rhsVar, lhsVarSet) <- varAssignStackR) {
      if(rhsVar == null) print("? => ")
      else print(rhsVar + " => ")

      var isFirst = true
      for(lhsVar <- lhsVarSet) {
        if(isFirst) isFirst = false else print(", ")
        println(lhsVar)
      }
    }
    println
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Type conversion
  ////////////////////////////////////////////////////////////////////////////////
  implicit def addressToLoc(addr: Address): Unit = addrToLoc(addr, Recent)
  implicit def locToAddress(loc: Loc): Unit = locToAddr(loc)
  def CFGExprToBugVar0(expr: CFGExpr): BugVar0 = {
    expr match {
      case CFGVarRef(_, id) => BugVar0(id, null)
      case CFGLoad(_, CFGThis(_), expr) => BugVar0(CFGUserId(null, "this", GlobalVar, "this", false), expr)
      case CFGLoad(_, obj, expr) => BugVar0(obj.asInstanceOf[CFGVarRef].id, expr)
      case CFGThis(_) => BugVar0(CFGUserId(null, "this", GlobalVar, "this", false), null)
      case _ => null
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Constructor
  ////////////////////////////////////////////////////////////////////////////////
  {
  }
}

case class BugVar0(id: CFGId, index: CFGExpr) {
  override def toString(): String = {
    val idToString = id match {
      case CFGUserId(_, _, _, orgName, _) => orgName
      case CFGTempId(text, _) => text
    }
    if(index == null) idToString
    else {
      index match {
        case CFGString(str) => idToString + '.' + str
        case _ => idToString + "[" + index.toString() + "]"
      }
    }
  }
}

case class BugVar1(inst: CFGInst, id: CFGId, index: CFGExpr) {
  override def toString(): String = {
    val idToString = id match {
      case CFGUserId(_, _, _, orgName, _) => orgName
      case CFGTempId(text, _) => text
    }
    if(index == null) idToString
    else {
      index match {
        case CFGString(str) => idToString + '.' + str
        case _ => idToString + "[" + index.toString() + "]"
      }
    }
  }
}
