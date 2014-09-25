/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.CFGAPICall
import kr.ac.kaist.jsaf.analysis.cfg.CFGAssert
import kr.ac.kaist.jsaf.analysis.cfg.CFGBin
import kr.ac.kaist.jsaf.analysis.cfg.CFGId
import kr.ac.kaist.jsaf.analysis.cfg.CFGInst
import kr.ac.kaist.jsaf.analysis.cfg.CFGLoad
import kr.ac.kaist.jsaf.analysis.cfg.CFGNumber
import kr.ac.kaist.jsaf.analysis.cfg.CFGReturn
import kr.ac.kaist.jsaf.analysis.cfg.CFGString
import kr.ac.kaist.jsaf.analysis.cfg.CFGTempId
import kr.ac.kaist.jsaf.analysis.cfg.CFGThis
import kr.ac.kaist.jsaf.analysis.cfg.CFGUn
import kr.ac.kaist.jsaf.analysis.cfg.CFGVarRef
import kr.ac.kaist.jsaf.analysis.cfg.FunctionId
import kr.ac.kaist.jsaf.analysis.cfg.LEntry
import kr.ac.kaist.jsaf.analysis.cfg.LExit
import kr.ac.kaist.jsaf.analysis.cfg.LExitExc
import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.analysis.cfg.PureLocalVar
import kr.ac.kaist.jsaf.analysis.typing.AddressManager.newSystemRecentLoc
import kr.ac.kaist.jsaf.analysis.typing.ControlPoint
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.analysis.typing.domain.AbsNumber
import kr.ac.kaist.jsaf.analysis.typing.domain.Address
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.FunSet
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.LPSet
import kr.ac.kaist.jsaf.analysis.typing.domain.Loc
import kr.ac.kaist.jsaf.analysis.typing.domain.NullTop
import kr.ac.kaist.jsaf.analysis.typing.domain.Obj
import kr.ac.kaist.jsaf.analysis.typing.domain.ObjectValue
import kr.ac.kaist.jsaf.analysis.typing.domain.ObjectValueBot
import kr.ac.kaist.jsaf.analysis.typing.domain.PropValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Value
import kr.ac.kaist.jsaf.nodes_util.IRFactory

abstract class Model(cfg: CFG) {
  def initialize(h: Heap): Heap
  def addAsyncCall(cfg: CFG, loop_head: Node): (List[Node],List[Node])
  def isModelFid(fid: FunctionId): Boolean
  def getSemanticMap(): Map[String, SemanticFun]
  def getPreSemanticMap(): Map[String, SemanticFun]
  def getDefMap(): Map[String, AccessFun]
  def getUseMap(): Map[String, AccessFun]
  def getFIdMap(): Map[FunctionId, String]
  def asyncSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                    name: String, list_addr: List[Address]): ((Heap, Context), (Heap, Context))
  def asyncPreSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                       name: String, list_addr: List[Address]): (Heap, Context)
  def asyncDef(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet
  def asyncUse(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet
  def asyncCallgraph(h: Heap, inst: CFGInst, map: Map[CFGInst, Set[FunctionId]],
                     name: String, list_addr: List[Address]): Map[CFGInst, Set[FunctionId]]

  /* fresh name */
  private var uniqueNameCounter = 0
  private def freshName(name: String) = {
    uniqueNameCounter += 1
    "<>API<>" + name + "<>" + uniqueNameCounter.toString
  }
  private val dummyInfo = IRFactory.makeInfo(IRFactory.dummySpan("API Object"))
  /**
   * Create cfg nodes for a dom function which is composed of ENTRY, single command body, EXIT and EXIT-EXC.
   *
   * @param funName function name
   * @return created function id
   */
  def makeAPICFG(modelName: String, funName: String) : FunctionId = {
    val nameArg = freshName("arguments")
    val fid = cfg.newFunction(nameArg, List[CFGId](), List[CFGId](), funName, dummyInfo)
    val node = cfg.newBlock(fid)

    cfg.addEdge((fid, LEntry), node)
    cfg.addEdge(node, (fid,LExit))
    cfg.addExcEdge(node, (fid,LExitExc))
    cfg.addInst(node,
      CFGAPICall(cfg.newInstId,
        modelName, funName,
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))

    (fid)
  }

  def makeAftercallAPICFG(modelName: String, funName: String) : FunctionId = {
    val nameArg = freshName("arguments")
    val rtn = freshName("temp")
    val fid = cfg.newFunction(nameArg, List[CFGId](), List[CFGId](), funName, dummyInfo)
    val call_node = cfg.newBlock(fid)
    val return_node = cfg.newAfterCallBlock(fid, CFGTempId(rtn, PureLocalVar))
    val return_exc_node = cfg.newAfterCatchBlock(fid)

    cfg.addEdge((fid, LEntry), call_node)
    cfg.addEdge(return_node, (fid,LExit))
    cfg.addCall(call_node, return_node, return_exc_node)
    cfg.addExcEdge(call_node, (fid,LExitExc))
    cfg.addEdge(return_exc_node, (fid,LExitExc))

    // []built-in-call
    cfg.addInst(call_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName,
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))

    // after-call(x)
    // return x;
    cfg.addInst(return_node,
      CFGReturn(cfg.newInstId, dummyInfo,
        Some(CFGVarRef(dummyInfo, CFGTempId(rtn, PureLocalVar)))))

    (fid)
  }

  def makeCallbackLoopAPICFG(modelName: String, funName: String) : FunctionId = {
    val nameArg = freshName("arguments")
    val rtn = "temp"
    val fid = cfg.newFunction(nameArg, List[CFGId](), List[CFGId](), funName, dummyInfo)
    val init_node = cfg.newBlock(fid)
    val call_node = cfg.newBlock(fid)
    val shortcut_node = cfg.newBlock(fid)
    val return_node = cfg.newAfterCallBlock(fid, CFGTempId(rtn, PureLocalVar))
    val return_exc_node = cfg.newAfterCatchBlock(fid)

    cfg.addEdge((fid, LEntry), init_node)
    cfg.addEdge(init_node, call_node)
    cfg.addEdge(init_node, shortcut_node)
    cfg.addEdge(shortcut_node, return_node)
    cfg.addCall(call_node, return_node, return_exc_node)
    cfg.addExcEdge(init_node, (fid,LExitExc))
    cfg.addExcEdge(call_node, (fid,LExitExc))
    cfg.addExcEdge(shortcut_node, (fid,LExitExc))
    cfg.addEdge(return_exc_node, (fid,LExitExc))
    cfg.addEdge(return_node, call_node)
    cfg.addEdge(return_node, (fid,LExit))

    // []built-in-init
    cfg.addInst(init_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName+".init",
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))
    // shortcut
    cfg.addInst(shortcut_node,
      CFGAssert(cfg.newInstId, dummyInfo,
          CFGBin(dummyInfo, 
            CFGLoad(dummyInfo, CFGThis(dummyInfo), CFGString("length")), IRFactory.makeOp("=="), 
            CFGNumber("0", 0)), false))

    // []built-in-call
    cfg.addInst(call_node,
      CFGAssert(cfg.newInstId, dummyInfo,
        CFGUn(dummyInfo, IRFactory.makeOp("!"), 
          CFGBin(dummyInfo, 
            CFGLoad(dummyInfo, CFGThis(dummyInfo), CFGString("length")), IRFactory.makeOp("=="), 
            CFGNumber("0", 0))), false))
    cfg.addInst(call_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName+".call",
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))

    // after-call(x)
    // funName.ret(x)
    // return x;
    cfg.addInst(return_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName+".ret",
        CFGVarRef(dummyInfo, CFGTempId(rtn, PureLocalVar))))
    cfg.addInst(return_node,
      CFGReturn(cfg.newInstId, dummyInfo,
        Some(CFGVarRef(dummyInfo, CFGTempId(rtn, PureLocalVar)))))

    (fid)
  }

  def makeAftercallOptionalAPICFG(modelName: String, funName: String) : FunctionId = {
    val nameArg = freshName("arguments")
    val rtn = freshName("temp")
    val fid = cfg.newFunction(nameArg, List[CFGId](), List[CFGId](), funName, dummyInfo)
    val call_node = cfg.newBlock(fid)
    val return_node = cfg.newAfterCallBlock(fid, CFGTempId(rtn, PureLocalVar))
    val return_exc_node = cfg.newAfterCatchBlock(fid)
    val normal_node = cfg.newBlock(fid)

    // for Call flow
    cfg.addEdge((fid, LEntry), call_node)
    cfg.addEdge(return_node, (fid,LExit))
    cfg.addCall(call_node, return_node, return_exc_node)
    cfg.addExcEdge(call_node, (fid,LExitExc))
    cfg.addEdge(return_exc_node, (fid,LExitExc))
    
    // []built-in-call
    cfg.addInst(call_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName,
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))
    
    // bypass call-node to exit node
//    cfg.addEdge(call_node, (fid, LExit))
        
    // for Normal flow
    cfg.addEdge((fid, LEntry), normal_node)
    cfg.addEdge(normal_node, (fid,LExit))
    cfg.addExcEdge(normal_node, (fid,LExitExc))

    cfg.addInst(normal_node,
      CFGAPICall(cfg.newInstId,
        modelName, funName,
        CFGVarRef(dummyInfo, CFGTempId(nameArg, PureLocalVar))))

    // after-call(x)
    // return x;
    cfg.addInst(return_node,
      CFGReturn(cfg.newInstId, dummyInfo,
        Some(CFGVarRef(dummyInfo, CFGTempId(rtn, PureLocalVar)))))
        
    (fid)
  }
  
  /**
   * Preparing the given AbsProperty to be updated.
   * If a property is a built-in function, create a new function object and pass it to name, value and object pair.
   * If a property is a constant value, pass it to name, value and object pair. At this time, object is None.
   *
   * @param name the name of each property
   * @param v the value of each property.
   */
  def prepareForUpdate(model: String, name: String, v: AbsProperty): (String, PropValue, Option[(Loc, Obj)], Option[(FunctionId,String)]) = {
    v match {
      case AbsBuiltinFunc(id, length) => {
        val fid = makeAPICFG(model, id)
        val loc = newSystemRecentLoc(id)
        val obj = Helper.NewFunctionObject(Some(fid), None, Value(NullTop), None, AbsNumber.alpha(length))
        (name, PropValue(ObjectValue(loc, T, F, T)), Some(loc, obj), Some(fid, id))
      }
      case AbsBuiltinFuncAftercall(id, length) => {
        val fid = makeAftercallAPICFG(model, id)
        val loc = newSystemRecentLoc(id)
        val obj = Helper.NewFunctionObject(Some(fid), None, Value(NullTop), None, AbsNumber.alpha(length))
        (name, PropValue(ObjectValue(loc, T, F, T)), Some(loc, obj), Some(fid, id))
      }
      case AbsBuiltinFuncAftercallOptional(id, length) => {
//        val fid = makeAftercallAPICFG(model, id)
        val fid = makeAftercallOptionalAPICFG(model, id)
//        cfg.addEdge((fid, LEntry), (fid, LExit)) // there is a possibility not to invoke functions
        val loc = newSystemRecentLoc(id)
        val obj = Helper.NewFunctionObject(Some(fid), None, Value(NullTop), None, AbsNumber.alpha(length))
        (name, PropValue(ObjectValue(loc, T, F, T)), Some(loc, obj), Some(fid, id))
      }
      case AbsBuiltinFuncCallback(id, length) => {
        val fid = makeCallbackLoopAPICFG(model, id)
        val loc = newSystemRecentLoc(id)
        val obj = Helper.NewFunctionObject(Some(fid), None, Value(NullTop), None, AbsNumber.alpha(length))
        (name, PropValue(ObjectValue(loc, T, F, T)), Some(loc, obj), Some(fid, id))
      }
      case AbsInternalFunc(id) => {
        val fid = makeAPICFG(model, id)
        (name, PropValue(ObjectValueBot, FunSet(fid)), None, Some(fid, id))
      }
      case AbsInternalFuncAftercallOptional(id) => {
        val fid = makeAftercallAPICFG(model, id)
        cfg.addEdge((fid, LEntry), (fid, LExit)) // there is a possibility not to invoke functions
        (name, PropValue(ObjectValueBot, FunSet(fid)), None, Some(fid, id))
      }
      case AbsConstValue(value) => (name, value, None, None)
    }
  }
}
