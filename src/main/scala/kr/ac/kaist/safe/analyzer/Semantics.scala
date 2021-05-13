/*
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import javax.script.ScriptEngineManager
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.model._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.LINE_SEP
import scala.collection.mutable.{ Map => MMap }

import spray.json._
import DefaultJsonProtocol._
import java.net._
import collection.mutable.ListBuffer
import java.io._
import scala.io._

case class Semantics(
    cfg: CFG,
    worklist: Worklist,
    timeLimit: Int = 0
) {
  var dsTriedCPs: Set[ControlPoint] = Set()
  def addTriedCPs(json: JsValue): Unit = json match {
    case JsArray(vector) => vector.foreach(_ match {
      case JsObject(fields) => fields("fid") match {
        case JsNumber(d) =>
          val fid = d.toInt
          fields("tracePartition") match {
            case JsString(str) =>
              val tp = TracePartition(str)(cfg)
              val cp = ControlPoint(cfg.getFunc(fid).get.entry, tp)
              dsTriedCPs += cp
            case _ =>
          }
        case _ =>
      }
      case _ =>
    })
    case _ =>
  }

  var startTime: Long = 0
  var instCount: Int = 0
  def setting: Unit = {
    if (instCount == 0) startTime = System.currentTimeMillis
    instCount = instCount + 1
    if (instCount % 1000 != 0) return
    if (timeLimit <= 0) return
    if ((System.currentTimeMillis - startTime) > (timeLimit * 1000))
      throw Timeout(timeLimit)
  }

  def send(message: String): JsValue = {
    val sock = new Socket(InetAddress.getByName("localhost"), 8000)
    lazy val in = new BufferedSource(sock.getInputStream).getLines
    val out = new PrintStream(sock.getOutputStream)

    out.println(message)
    out.flush
    out.print("###EOF###");
    out.flush

    val received = in.mkString
    sock.close
    received.parseJson
  }

  private var calledByCall: Set[Entry] = Set()
  private var calledByConstruct: Set[Entry] = Set()

  lazy val engine = new ScriptEngineManager().getEngineByMimeType("text/javascript")
  def init: Unit = {
    val entry = cfg.globalFunc.entry
    val entryCP = ControlPoint(entry, getState(entry).head match { case (tp, _) => tp })
    val initSt = getState(entryCP)
    cpToState.clear
    setState(entryCP, initSt)
    worklist.init(entryCP)
  }

  lazy val excLog: ExcLog = new ExcLog

  private val AB = AbsBool.Bot

  private val ccpToCallInfo: MMap[Call, MMap[TracePartition, CallInfo]] = MMap()
  def setCallInfo(call: Call, tp: TracePartition, info: CallInfo): Unit = {
    val map = ccpToCallInfo.getOrElse(call, {
      val newMap = MMap[TracePartition, CallInfo]()
      ccpToCallInfo(call) = newMap
      newMap
    })
    map(tp) = info
  }

  def getCallInfo(call: Call, tp: TracePartition): CallInfo = {
    ccpToCallInfo
      .getOrElse(call, MMap())
      .getOrElse(tp, CallInfo(AbsState.Bot, AbsValue.Bot, AbsValue.Bot))
  }

  // control point maps to state
  private val cpToState: MMap[CFGBlock, MMap[TracePartition, AbsState]] = MMap()
  def getState(block: CFGBlock): Map[TracePartition, AbsState] =
    cpToState.getOrElse(block, {
      val newMap = MMap[TracePartition, AbsState]()
      cpToState(block) = newMap
      newMap
    }).foldLeft(Map[TracePartition, AbsState]())(_ + _)
  def getState(cp: ControlPoint): AbsState = {
    val block = cp.block
    val tp = cp.tracePartition
    getState(block).getOrElse(tp, AbsState.Bot)
  }
  def setState(cp: ControlPoint, state: AbsState): Unit = {
    val block = cp.block
    val tp = cp.tracePartition
    val map = cpToState.getOrElse(block, {
      val newMap = MMap[TracePartition, AbsState]()
      cpToState(block) = newMap
      newMap
    })
    if (state.isBottom) map -= tp
    else map(tp) = state
  }

  type OutCtxtMap = Map[CFGBlock, Set[LoopContext]]
  private var outCtxtMap: OutCtxtMap = Map()
  def addOutCtxt(block: CFGBlock, ctxt: LoopContext): Unit =
    outCtxtMap += block -> (getOutCtxtSet(block) + ctxt)
  def getOutCtxtSet(block: CFGBlock): Set[LoopContext] =
    outCtxtMap.getOrElse(block, Set())

  type IPSucc = Map[ControlPoint, EdgeData]
  type IPSuccMap = Map[ControlPoint, IPSucc]
  private var ipSuccMap: IPSuccMap = Map()
  def getAllIPSucc: IPSuccMap = ipSuccMap
  def setAllIPSucc(newMap: IPSuccMap): Unit = { ipSuccMap = newMap }
  def getInterProcSucc(cp: ControlPoint): Option[IPSucc] = ipSuccMap.get(cp)

  // Adds inter-procedural call edge from call-block cp1 to entry-block cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addIPEdge(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => Map(cp2 -> data)
      case Some(map2) => map2.get(cp2) match {
        case None =>
          map2 + (cp2 -> data)
        case Some(oldData) =>
          map2 + (cp2 -> (data ⊔ oldData))
      }
    }
    ipSuccMap += (cp1 -> updatedSuccMap)
  }

  def E(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData, st: AbsState): AbsState = {
    (cp1.block, cp2.block) match {
      case (call: Call, entry @ Entry(f)) => st.context match {
        case _ if st.context.isBottom => AbsState.Bot
        case ctx1: AbsContext => {
          call.callInst match {
            case (_: CFGCall) => calledByCall += entry
            case (_: CFGConstruct) => calledByConstruct += entry
          }
          val objEnv = data.env.record.decEnvRec.GetBindingValue("@scope") match {
            case (value, _) => AbsLexEnv.NewDeclarativeEnvironment(value.locset)
          }
          val (envRec, _) = data.env.record.decEnvRec.DeleteBinding("@scope")
          val ctx2 = ctx1.subsPureLocal(data.env.copy(record = envRec))
          val ctx3 = data.env.outer.foldLeft[AbsContext](AbsContext.Bot)((hi, locEnv) => {
            hi ⊔ ctx2.update(locEnv, objEnv)
          })
          st.copy(context = ctx3.setThisBinding(data.thisBinding))
            .setAllocLocSet(data.allocs)
        }
      }
      case (Exit(_), _) if st.context.isBottom => AbsState.Bot
      case (Exit(f1), acall @ AfterCall(f2, retVar, call)) =>
        val call = acall.call
        val params = f1.argVars
        val info = getCallInfo(call, cp2.tracePartition)
        val state = st
        val (ctx1, allocs1) = (state.context, state.allocs)
        val EdgeData(allocs2, env1, thisBinding) = data.fix(allocs1)

        if (allocs2.isBottom) AbsState.Bot
        else {
          val localEnv = ctx1.pureLocal
          val (returnV, _) = localEnv.record.decEnvRec.GetBindingValue("@return")
          val ctx2 = ctx1.subsPureLocal(env1)
          val newSt = state.copy(context = ctx2.setThisBinding(thisBinding))
            .setAllocLocSet(allocs2)
          newSt.varStore(retVar, returnV)
        }
      case (ExitExc(_), _) if st.context.isBottom => AbsState.Bot
      case (ExitExc(_), _) if st.allocs.isBottom => AbsState.Bot
      case (ExitExc(f1), acatch @ AfterCatch(_, _)) =>
        val call = acatch.call
        val params = f1.argVars
        val info = getCallInfo(call, cp2.tracePartition)
        val state = st
        val (ctx1, c1) = (state.context, state.allocs)
        val EdgeData(c2, envL, thisBinding) = data.fix(c1)
        val env1 = envL.record.decEnvRec
        if (c2.isBottom) AbsState.Bot
        else {
          val localEnv = ctx1.pureLocal
          val (excValue, _) = localEnv.record.decEnvRec.GetBindingValue("@exception")
          val (oldExcAllValue, _) = env1.GetBindingValue("@exception_all")
          val (env2, _) = env1.SetMutableBinding("@exception", excValue)
          val (env3, _) = env2.SetMutableBinding("@exception_all", excValue ⊔ oldExcAllValue)
          val ctx2 = ctx1.subsPureLocal(envL.copy(record = env3))
          state.copy(context = ctx2.setThisBinding(thisBinding))
            .setAllocLocSet(c2)
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, st: AbsState): (AbsState, AbsState) = {
    if (st.isBottom) (AbsState.Bot, AbsState.Bot)
    else {
      val h = st.heap
      val ctx = st.context
      val allocs = st.allocs
      cp.block match {
        case entry @ Entry(func) => {
          val fun = cp.block.func
          val xArgVars = fun.argVars
          val xLocalVars = fun.localVars
          val localEnv = ctx.pureLocal
          val (argV, _) = localEnv.record.decEnvRec.GetBindingValue(fun.argumentsName)
          val (nSt, _) = xArgVars.foldLeft((st, 0))((res, x) => {
            val (iSt, i) = res
            val vi = argV.locset.foldLeft(AbsValue.Bot)((vk, lArg) => {
              vk ⊔ iSt.heap.get(lArg).Get(i.toString, iSt.heap)
            })
            (iSt.createMutableBinding(
              x,
              vi
            ), i + 1)
          })
          val newSt = xLocalVars.foldLeft(nSt)((jSt, x) => {
            val undefV = AbsValue(Undef)
            jSt.createMutableBinding(x, undefV)
          })

          var touchedFunc = func.id < 0

          var notModelCallSite = cp.tracePartition match {
            case ProductTP(CallSiteContext(callsiteList, _), _) =>
              callsiteList match {
                case h :: t => h.func.id >= 0
                case Nil => true
              }
            case _ => true
          }

          val result = if (dynamicShortcut && !dsTriedCPs.contains(cp) && cp.block.func.id != 0 && notModelCallSite) try {
            val fid = cp.block.func.id;
            dsTriedCPs += cp
            val startTime = System.currentTimeMillis

            // unique id mutable map
            implicit val uomap = new UIdObjMap

            globalLocJSON = newSt.heap.get(GLOBAL_LOC).toJSON
            if (globalLocJSON.asJsObject.fields contains uomap.UNIQUE)
              throw new ToJSONFail("GLOBAL_LOC")
            var dump = {
              val fields = Map(
                "fid" -> JsNumber(fid),
                "state" -> newSt.toJSON,
                "tracePartition" -> cp.tracePartition.toJSON
              )
              JsObject(
                if (cp.block.func.id < 0) fields + ("code" -> JsObject(
                  "isCall" -> JsBoolean(fidToName(fid).isCall),
                  "name" -> JsString(fidToName(fid).name)
                ))
                else fields
              )
            }

            // remove this object for construct
            val thisBindingForConstruct =
              calledByConstruct.contains(entry) && !calledByCall(entry) && dump
                .fields("state").asJsObject
                .fields("context").asJsObject
                .fields("thisBinding").asJsObject
                .fields.contains("location")
            dump = JsObject(dump.fields + ("isConstructor" -> JsBoolean(thisBindingForConstruct)))

            dsCount += 1

            System.err.println(s"[DS] [${func.id}] ${func.simpleName} @ ${func.span}")

            val newTP = cp.tracePartition
            val exitCP = ControlPoint(func.exit, newTP)

            val dumped = dump.compactPrint
            val json = send(dumped)

            val fields = json.asJsObject().fields
            addTriedCPs(fields("visitedEntryControlPoints"))

            val loaded = AbsState.fromJSON(fields("state"), cfg, newSt)
            val result = if (loaded.isBottom) {
              (newSt, AbsState.Bot)
            } else {
              dsSuccessCount += 1
              touchedFunc = false

              setState(exitCP, loaded)
              worklist.add(exitCP)
              (AbsState.Bot, AbsState.Bot)
            }

            val duration = System.currentTimeMillis - startTime
            dsDuration += duration

            result
          } catch {
            case e: ToJSONFail =>
              if (analysisDebug) {
                println(s"[WARNING] toJSON Failed @ ${e.getStackTrace.toList.mkString("\n")}")
                println
                println(s"[Target] ${e.target}")
                println
              } else if (!toJSONFailed) {
                println(s"[WARNING] toJSON Failed")
                toJSONFailed = true
              }
              (newSt, AbsState.Bot)
          }
          else (newSt, AbsState.Bot)

          if (touchedFunc) touchedFuncs += func.id

          result
        }
        case (call: Call) =>
          val (thisVal, argVal, resSt, resExcSt) = internalCI(cp, call.callInst, st, AbsState.Bot)
          setCallInfo(call, cp.tracePartition, CallInfo(resSt, thisVal, argVal))
          (resSt, resExcSt)
        case block: NormalBlock =>
          block.getInsts.foldRight((st, AbsState.Bot))((inst, states) => {
            val (oldSt, oldExcSt) = states
            I(cp, inst, oldSt, oldExcSt)
          })
        case _ => (st, AbsState.Bot)
      }
    }
  }

  def I(cp: ControlPoint, i: CFGNormalInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    setting
    val tp = cp.tracePartition
    i match {
      case _ if st.isBottom => (AbsState.Bot, excSt)
      case CFGAlloc(_, _, x, e, newASite) => {
        val objProtoSingleton = LocSet(OBJ_PROTO_LOC)
        val loc = Loc(newASite, tp)
        val st1 = st.alloc(loc)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExcSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset + OBJ_PROTO_LOC, es)
            else
              (v.locset, es)
          }
        }
        val h2 = st1.heap.update(loc, AbsObj.newObject(vLocSet))
        val newSt = st1.copy(heap = h2).varStore(x, AbsValue(loc))
        val newExcSt = st.raiseException(excSet)
        val s1 = excSt ⊔ newExcSt
        (newSt, s1)
      }
      case CFGAllocArray(_, _, x, n, newASite) => {
        val loc = Loc(newASite, tp)
        val st1 = st.alloc(loc)
        val np = AbsNum(n.toInt)
        val h2 = st1.heap.update(loc, AbsObj.newArrayObject(np))
        val newSt = st1.copy(heap = h2).varStore(x, AbsValue(loc))
        (newSt, excSt)
      }
      case CFGAllocArg(_, _, x, n, newASite) => {
        val loc = Loc(newASite, tp)
        val st1 = st.alloc(loc)
        val absN = AbsNum(n.toInt)
        val h2 = st1.heap.update(loc, AbsObj.newArgObject(absN))
        val newSt = st1.copy(heap = h2).varStore(x, AbsValue(loc))
        (newSt, excSt)
      }
      case CFGEnterCode(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val thisVal = AbsValue(v.getThis(st.heap))
        val st1 =
          if (!v.isBottom) st.varStore(x, thisVal)
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val st1 =
          if (!v.isBottom) st.varStore(x, v)
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseV = st.lookupBase(x2)
        val undefB = baseV.pvalue.undefval.fold(AB)(_ => AT)
        val (st1, locB) =
          baseV.locset.foldLeft[(AbsState, AbsBool)](AbsState.Bot, AB)((res, baseLoc) => {
            val (tmpState, tmpB) = res
            val (delState, delB) = st.delete(baseLoc, x2.text)
            (tmpState ⊔ delState, tmpB ⊔ delB)
          })
        val st2 = st1.varStore(x1, locB ⊔ undefB)
        (st2, excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) {
            val trueVal = AbsValue(AT)
            st.varStore(x1, trueVal)
          } else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) Set[AbsStr]()
          else TypeConversionHelper.ToPrimitive(v, st.heap).toStringSet
        val (h1, b) = locSet.foldLeft[(AbsHeap, AbsBool)](AbsHeap.Bot, AB)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = st.heap.delete(l, s)
            (tmpHeap2 ⊔ delHeap, tmpB2 ⊔ delB)
          })
        })
        val st1 = st.copy(heap = h1)
        val st2 =
          if (st1.isBottom) AbsState.Bot
          else {
            st1.varStore(lhs, AbsValue(b))
          }
        val newExcSt = st.raiseException(excSet)
        (st2, excSt ⊔ newExcSt)
      }
      case CFGStore(_, block, obj, index, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset

        val (idxV, excSetIdx) = V(index, st)
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (idxV, vRhs) match {
            case (v, _) if v.isBottom => (AbsHeap.Bot, excSetIdx)
            case (_, v) if v.isBottom => (AbsHeap.Bot, excSetIdx ++ esRhs)
            case _ =>
              // iterate over set of strings for index
              val absStrSet = TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
              absStrSet.foldLeft[(AbsHeap, Set[Exception])]((AbsHeap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
                val (tmpHeap1, tmpExcSet1) = res1
                val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
                (tmpHeap1 ⊔ tmpHeap2, tmpExcSet1 ++ tmpExcSet2)
              })
          }

        val newExcSt = st.raiseException(excSet1)
        (st.copy(heap = heap1), excSt ⊔ newExcSt)
      }
      case CFGStoreStringIdx(_, block, obj, strIdx, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (strIdx, vRhs) match {
            case (_, v) if v.isBottom => (AbsHeap.Bot, esRhs)
            case (EJSString(str), v) =>
              val absStr = AbsStr(str)
              val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = st.raiseException(excSet1)
        (st.copy(heap = heap1), excSt ⊔ newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val loc1 = Loc(aNew1, tp)
        val loc2 = Loc(aNew2, tp)
        val st1 = st.alloc(loc1)
        val st2 = st1.alloc(loc2)
        val oNew = AbsObj.newObject(OBJ_PROTO_LOC)

        val n = AbsNum(f.argVars.length)
        val localEnv = st2.context.pureLocal
        val h3 = st2.heap.update(loc1, AbsObj.newFunctionObject(f.id, localEnv.outer, loc2, n))

        val fVal = AbsValue(loc1)
        val h4 = h3.update(loc2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val newSt = st2.copy(heap = h4).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val loc1 = Loc(aNew1, tp)
        val loc2 = Loc(aNew2, tp)
        val loc3 = Loc(aNew3, tp)
        val st1 = st.alloc(loc1)
        val st2 = st1.alloc(loc2)
        val st3 = st2.alloc(loc3)

        val oNew = AbsObj.newObject(OBJ_PROTO_LOC)
        val n = AbsNum(f.argVars.length)
        val fObjValue = AbsValue(loc3)
        val h4 = st3.heap.update(loc1, AbsObj.newFunctionObject(f.id, fObjValue, loc2, n))

        val fVal = AbsValue(loc1)
        val h5 = h4.update(loc2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val localEnv = st3.context.pureLocal
        val oEnv = AbsLexEnv.NewDeclarativeEnvironment(localEnv.outer)
        val oEnvRec2 = oEnv.record.decEnvRec
          .CreateImmutableBinding(name.text)
          .InitializeImmutableBinding(name.text, fVal)
        val newCtx = st3.context.update(loc3, oEnv.copy(record = oEnvRec2))
        val newSt = AbsState(h5, newCtx, st3.allocs).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt)
      case CFGCatch(_, _, x) => {
        val localEnv = st.context.pureLocal
        val (excSetV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception_all")
        val (excV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception")
        val st1 = st.createMutableBinding(x, excV)
        val env = st1.context.pureLocal
        val (newEnv, _) = env.record.decEnvRec.SetMutableBinding("@exception", excSetV)
        val newCtx = st1.context.subsPureLocal(env.copy(record = newEnv))
        val newSt = st1.copy(context = newCtx)
        (newSt, AbsState.Bot)
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val ctx1 =
          if (!v.isBottom) {
            val localEnv = st.context.pureLocal
            val (localEnv2, _) = localEnv.record.decEnvRec.SetMutableBinding("@return", v)
            st.context.subsPureLocal(localEnv.copy(record = localEnv2))
          } else AbsContext.Bot
        val newExcSt = st.raiseException(excSet)
        (st.copy(context = ctx1), excSt ⊔ newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localEnv = st.context.pureLocal
        val (localEnv2, _) = localEnv.record.decEnvRec.SetMutableBinding("@return", AbsUndef.Top)
        val ctx1 = st.context.subsPureLocal(localEnv.copy(record = localEnv2))
        val newSt = st.copy(context = ctx1)
        (newSt, excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localEnv = st.context.pureLocal
        val (excSetV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception_all")
        val (newEnv, _) = localEnv.record.decEnvRec.SetMutableBinding("@exception", v)
        val (newEnv2, _) = newEnv.SetMutableBinding("@exception_all", v ⊔ excSetV)
        val (newEnv3, _) = newEnv2
          .CreateMutableBinding("@return").fold(newEnv2)((e: AbsDecEnvRec) => e)
          .SetMutableBinding("@return", AbsUndef.Top)
        val ctx1 = st.context.subsPureLocal(localEnv.copy(record = newEnv3))
        val newExcSt = st.raiseException(excSet)

        (AbsState.Bot, excSt ⊔ st.copy(context = ctx1) ⊔ newExcSt)
      }
      case CFGInternalCall(ir, _, lhs, name, arguments, loc) =>
        IC(cp, ir, lhs, name, arguments, loc, st, excSt)
      case CFGNoOp(_, _, _) => (st, excSt)
    }
  }

  // internal API value
  def getInternalValue(name: String): Option[AbsValue] = name match {
    case (NodeUtil.INTERNAL_TOP) => Some(AbsValue.Top)
    case (NodeUtil.INTERNAL_UINT) => Some(AbsNum.UInt)
    case (NodeUtil.INTERNAL_NUINT) => Some(AbsNum.NUInt)
    case (NodeUtil.INTERNAL_GLOBAL) => Some(AbsValue(GLOBAL_LOC))
    case (NodeUtil.INTERNAL_BOOL_TOP) => Some(AbsBool.Top)
    case (NodeUtil.INTERNAL_NUM_TOP) => Some(AbsNum.Top)
    case (NodeUtil.INTERNAL_STR_TOP) => Some(AbsStr.Top)

    case (NodeUtil.INTERNAL_EVAL_ERR) => Some(EVAL_ERROR_LOC)
    case (NodeUtil.INTERNAL_RANGE_ERR) => Some(RANGE_ERROR_LOC)
    case (NodeUtil.INTERNAL_REF_ERR) => Some(REF_ERROR_LOC)
    case (NodeUtil.INTERNAL_SYNTAX_ERR) => Some(SYNTAX_ERROR_LOC)
    case (NodeUtil.INTERNAL_TYPE_ERR) => Some(TYPE_ERROR_LOC)
    case (NodeUtil.INTERNAL_URI_ERR) => Some(URI_ERROR_LOC)
    case (NodeUtil.INTERNAL_EVAL_ERR_PROTO) => Some(EVAL_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_RANGE_ERR_PROTO) => Some(RANGE_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_REF_ERR_PROTO) => Some(REF_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_SYNTAX_ERR_PROTO) => Some(SYNTAX_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_TYPE_ERR_PROTO) => Some(TYPE_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_URI_ERR_PROTO) => Some(URI_ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_ERR_PROTO) => Some(ERROR_PROTO_LOC)
    case (NodeUtil.INTERNAL_OBJ_CONST) => Some(OBJ_LOC)
    case (NodeUtil.INTERNAL_ARRAY_CONST) => Some(ARR_LOC)
    case _ => None
  }

  // internal API call
  // CFGInternalCall(ir, _, lhs, name, arguments, loc)
  def IC(
    cp: ControlPoint, ir: IRNode, lhs: CFGId, name: String, args: List[CFGExpr],
    loc: Option[AllocSite], st: AbsState, excSt: AbsState
  ): (AbsState, AbsState) = {
    val tp = cp.tracePartition
    (name, args, loc) match {
      case (NodeUtil.INTERNAL_PRINT, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        println(s"[DEBUG] $cp")
        println(s"        expression: $expr")
        println(s"        exceptions: $excSet")
        println(s"        pvalue    : ${v.pvalue}")
        println(s"        objects:")
        v.locset.foreach(loc => println(st.heap.toStringLoc(loc).getOrElse(s"[LocNotFound] $loc")))
        (st, excSt)
      }
      case (NodeUtil.INTERNAL_NOT_YET_IMPLEMENTED, List(expr), None) => {
        val (v, excSet) = V(expr, st);
        excLog.signal(SemanticsNotYetImplementedError(v, cp))
        (st, excSt)
      }
      case (NodeUtil.INTERNAL_CHAR_CODE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val numval = v.pvalue.strval.gamma match {
          case ConFin(strset) => AbsNum(strset.map {
            case Str(str) => Num(str(0).toInt)
          })
          case ConInf => AbsNum.UInt
        }
        val newSt = st.varStore(lhs, AbsValue(numval))
        val newExcSt = st.raiseException(excSet)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_CLASS, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IClass, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_CLASS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val className = obj(IClass).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, className)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_PRIM_VAL, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IPrimitiveValue, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_PRIM_VAL, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(IPrimitiveValue).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_PROTO, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IPrototype, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_PROTO, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(IPrototype).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_EXTENSIBLE, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IExtensible, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_EXTENSIBLE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(IExtensible).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_GET_BASE, List(CFGVarRef(_, x2)), None) => {
        val baseV = st.lookupBase(x2)
        val st1 = st.varStore(lhs, baseV)
        (st1, excSt)
      }
      case (NodeUtil.INTERNAL_GET_OWN_PROP, List(exprO, exprP), Some(aNew)) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val obj = st.heap.get(v.locset)
        val name = TypeConversionHelper.ToString(p)
        val (desc, undef) = obj.GetOwnProperty(name)
        val (retSt, retV, excSet) = if (!desc.isBottom) {
          val (descObj, excSet) = AbsObj.FromPropertyDescriptor(st.heap, desc)
          val descLoc = Loc(aNew, tp)
          val state = st.alloc(descLoc)
          val retH = state.heap.update(descLoc, descObj.alloc(aNew))
          val retV = AbsValue(undef, LocSet(descLoc))
          (state.copy(heap = retH), retV, excSet)
        } else (st, AbsValue(undef), ExcSetEmpty)
        val newSt = retSt.varStore(lhs, retV)
        val newExcSt = st.raiseException(excSetO ++ excSetP ++ excSet)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_DEF_OWN_PROP, List(exprO, exprP, exprA), None) => {
        val h = st.heap
        val (objV, excSetO) = V(exprO, st)
        val (propV, excSetP) = V(exprP, st)
        val (attrV, excSetA) = V(exprA, st)

        val name = propV.pvalue.strval
        // ToPropertyDescriptor ( Obj )
        // 1. If Type(Obj) is not Object throw a TypeError exception.
        val excSet =
          if (attrV.pvalue.isBottom) ExcSetEmpty
          else Set(TypeError)
        val attr = h.get(attrV.locset)
        val desc = AbsDesc.ToPropertyDescriptor(attr, h)
        val (retH, retExcSet) = objV.locset.foldLeft((h, excSet ++ excSetO ++ excSetP ++ excSetA)) {
          case ((heap, e), loc) => {
            val obj = heap.get(loc)
            val (retObj, _, newExcSet) = obj.DefineOwnProperty(name, desc, true, h)
            val retH = heap.update(loc, retObj)
            (retH, e ++ newExcSet)
          }
        }
        val retSt = st.copy(heap = retH).varStore(lhs, AbsValue(objV.locset))
        val excSt = st.raiseException(retExcSet)
        (retSt, excSt)
      }
      case (NodeUtil.INTERNAL_TO_PRIM, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToPrimitive(v)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_BOOL, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToBoolean(v)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_NUM, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToNumber(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_INT, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToInteger(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_UINT_32, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToUint32(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_UINT_16, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToUint16(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_STR, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToString(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_OBJ, List(expr), Some(aNew)) => {
        val (v, excSet1) = V(expr, st)
        val (newSt, newExcSet) =
          if (v.isBottom) {
            (AbsState.Bot, excSet1)
          } else {
            val (v1, st1, excSet2) = TypeConversionHelper.ToObject(tp, v, st, aNew)
            val st2 =
              if (!v1.isBottom) st1.varStore(lhs, v1)
              else AbsState.Bot
            (st2, excSet1 ++ excSet2)
          }
        val newExcSt = st.raiseException(newExcSet)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_IS_CALLABLE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.IsCallable(v, st.heap)))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_SAME_VALUE, List(left, right), None) => {
        val (l, excSet1) = V(left, st)
        val (r, excSet2) = V(right, st)
        val st1 =
          if (!l.isBottom && !r.isBottom) {
            st.varStore(lhs, AbsValue(TypeConversionHelper.SameValue(st.heap, l, r)))
          } else AbsState.Bot

        val newExcSt = st.raiseException(excSet1 ++ excSet2)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_IS_NATIVE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val fidset = obj(ICall).fidset

        val abool = AbsBool(fidset.foldLeft(Set[Boolean]()) {
          case (set, fid) => set + (fid < 0)
        })

        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(abool))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_GET_OWN_PROP_NAMES, List(expr), Some(aNew)) => {
        val h = st.heap
        val arrASite = aNew
        val (objV, excSet1) = V(expr, st)
        // 1. If Type(O) is not Object throw a TypeError exception.
        val excSet2: Set[Exception] =
          if (objV.pvalue.isBottom) ExcSetEmpty
          else Set(TypeError)

        // 2. Let array be the result of creating a new Array object.
        // (XXX: we assign the length of the Array object as the number of properties)
        val uint = "(0|[1-9][0-9]*)".r
        def toUInt(str: String): Option[Int] = str match {
          case uint(n) => Some(n.toInt)
          case _ => None
        }
        val uintFirst: (String, String) => Boolean = (l, r) => (toUInt(l), toUInt(r)) match {
          case (Some(l), Some(r)) => l < r
          case (Some(_), _) => true
          case (_, Some(_)) => false
          case _ => l < r
        }
        val (obj, resExcSt) = objV.locset.foldLeft(AbsObj.Bot, excSet1 ++ excSet2) {
          case ((o, es), loc) => h.get(loc).collectKeySet match {
            case ConInf => (AbsObj.Top, es)
            case ConFin(set) => {
              val array = AbsObj.newArrayObject(AbsNum(set.size))
              val AT = (AbsBool.True, AbsAbsent.Bot)
              // 3. For each named own property P of O (with index n started from 0)
              //   a. Let name be the String value that is the name of P.
              val (obj, resExcSt) = set.toSeq.sortWith(uintFirst).zipWithIndex.foldLeft((array, es)) {
                case ((arr, es), (key, n)) => {
                  val desc = AbsDesc((AbsValue(AbsStr(key)), AbsAbsent.Bot), AT, AT, AT)
                  val prop = AbsStr(n.toString)
                  // b. Call the [[DefineOwnProperty]] internal method of array with arguments
                  //    ToString(n), the PropertyDescriptor {[[Value]]: name, [[Writable]]:
                  //    true, [[Enumerable]]: true, [[Configurable]]:true}, and false.
                  val (newArr, _, excSet) = arr.DefineOwnProperty(prop, desc, false, h)
                  (newArr, es ++ excSet)
                }
              }
              (o ⊔ obj, resExcSt)
            }
          }
        }

        val excSt = st.raiseException(resExcSt)

        // 5. Return array.
        obj.isBottom match {
          case true => (AbsState.Bot, excSt)
          case false => {
            val arrLoc = Loc(arrASite, tp)
            val state = st.alloc(arrLoc)
            val retHeap = state.heap.update(arrLoc, obj.alloc(arrLoc))
            val excSt = state.raiseException(resExcSt)
            val st2 = state.copy(heap = retHeap)
            val retSt = st2.varStore(lhs, AbsValue(arrLoc))

            (retSt, excSt)
          }
        }
      }
      case (NodeUtil.INTERNAL_STR_OBJ, List(expr), Some(aNew)) => {
        val (v, excSet) = V(expr, st)
        val str = TypeConversionHelper.ToString(v)
        val loc = Loc(aNew, tp)
        val st1 = st.alloc(loc)
        val heap = st1.heap.update(loc, AbsObj.newStringObj(str))
        val st2 = st1.copy(heap = heap)
        val st3 =
          if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st3, newExcSt)
      }
      case (NodeUtil.INTERNAL_INDEX_OF, List(expr, str, pos), None) => {
        val (thisval, excSet1) = V(expr, st)
        val (strval, excSet2) = V(str, st)
        val (posval, excSet3) = V(pos, st)
        val kval = (
          thisval.pvalue.strval.gamma,
          strval.pvalue.strval.gamma,
          posval.pvalue.numval.gamma
        ) match {
            case (ConFin(thisset), ConFin(strset), ConFin(posset)) =>
              AbsNum(for (t <- thisset; s <- strset; p <- posset)
                yield Num(t.str.indexOf(s.str, p.num.toInt)))
            case _ => AbsNum.Top
          }
        val st1 = st.varStore(lhs, AbsValue(kval))
        val newExcSt = st.raiseException(excSet1 ++ excSet2 ++ excSet3)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_LAST_INDEX_OF, List(expr, str, pos), None) => {
        val (thisval, excSet1) = V(expr, st)
        val (strval, excSet2) = V(str, st)
        val (posval, excSet3) = V(pos, st)
        val kval = (
          thisval.pvalue.strval.gamma,
          strval.pvalue.strval.gamma,
          posval.pvalue.numval.gamma
        ) match {
            case (ConFin(thisset), ConFin(strset), ConFin(posset)) =>
              AbsNum(for (t <- thisset; s <- strset; p <- posset)
                yield Num(t.str.lastIndexOf(s.str, p.num.toInt)))
            case _ => AbsNum.Top
          }
        val st1 = st.varStore(lhs, AbsValue(kval))
        val newExcSt = st.raiseException(excSet1 ++ excSet2 ++ excSet3)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_SPLIT, List(str, sep), Some(aNew)) => {
        val h = st.heap
        val arrASite = aNew
        val (strval, excSet1) = V(str, st)
        val (sepval, excSet2) = V(sep, st)
        val arr = (
          strval.pvalue.strval.gamma,
          sepval.pvalue.strval.gamma
        ) match {
            case (ConFin(strset), ConFin(sepset)) => {
              val arrs = {
                for (s <- strset; p <- sepset) yield {
                  var arr = (s.str).split(p.str)
                  if (p.str != "" && s.str.endsWith(p.str)) arr :+= ""
                  arr
                }
              }
              (AbsObj.Bot /: arrs) {
                case (obj, arr) => obj ⊔ ((AbsObj.newArrayObject(AbsNum(arr.length)) /: arr.zipWithIndex) {
                  case (arr, (str, idx)) => arr.update(
                    AbsStr(idx.toString),
                    AbsDataProp(DataProp(str, T, T, T))
                  )
                })
              }
            }
            case _ => AbsObj.newArrayObject(AbsNum.Top).update(AbsStr.Number, AbsDataProp.Top)
          }
        val arrLoc = Loc(arrASite, tp)
        val state = st.alloc(arrLoc)
        val retHeap = state.heap.update(arrLoc, arr.alloc(arrLoc))
        val excSt = state.raiseException(excSet1 ++ excSet2)
        val st2 = state.copy(heap = retHeap)
        val retSt = st2.varStore(lhs, AbsValue(arrLoc))

        (retSt, excSt)
      }
      case (NodeUtil.INTERNAL_SPLIT, List(str, sep, lim), Some(aNew)) => {
        val h = st.heap
        val arrASite = aNew
        val (strval, excSet1) = V(str, st)
        val (sepval, excSet2) = V(sep, st)
        val (limval, excSet3) = V(lim, st)
        val arr = (
          strval.pvalue.strval.gamma,
          sepval.pvalue.strval.gamma,
          limval.pvalue.numval.gamma
        ) match {
            case (ConFin(strset), ConFin(sepset), ConFin(limset)) => {
              val arrs = {
                for (s <- strset; p <- sepset; l <- limset)
                  yield s.str.split(p.str).take(l.num.toInt)
              }
              (AbsObj.Bot /: arrs) {
                case (obj, arr) => obj ⊔ ((AbsObj.newArrayObject(AbsNum(arr.length)) /: arr.zipWithIndex) {
                  case (arr, (str, idx)) => arr.update(
                    AbsStr(idx.toString),
                    AbsDataProp(DataProp(str, T, T, T))
                  )
                })
              }
            }
            case _ => AbsObj.newArrayObject(AbsNum.Top).update(AbsStr.Number, AbsDataProp.Top)
          }
        val arrLoc = Loc(arrASite, tp)
        val state = st.alloc(arrLoc)
        val retHeap = state.heap.update(arrLoc, arr.alloc(arrLoc))
        val excSt = state.raiseException(excSet1 ++ excSet2 ++ excSet3)
        val st2 = state.copy(heap = retHeap)
        val retSt = st2.varStore(lhs, AbsValue(arrLoc))

        (retSt, excSt)
      }
      case (NodeUtil.INTERNAL_SUBSTRING, List(str, from, to), None) => {
        val (strval, excSet1) = V(str, st)
        val (fromval, excSet2) = V(from, st)
        val (toval, excSet3) = V(to, st)
        val res = (
          strval.pvalue.strval.gamma,
          fromval.pvalue.numval.gamma,
          toval.pvalue.numval.gamma
        ) match {
            case (ConFin(strset), ConFin(fromset), ConFin(toset)) =>
              AbsStr(for (s <- strset; f <- fromset; t <- toset)
                yield Str(s.str.substring(f.num.toInt, t.num.toInt)))
            case _ => AbsStr.Top
          }
        val st1 = st.varStore(lhs, AbsValue(res))
        val newExcSt = st.raiseException(excSet1 ++ excSet2 ++ excSet3)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_LOWER_CASE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val str = v.pvalue.strval
        val lower = AbsStr.alpha(s => Str(s.str.toLowerCase))(AbsStr)(str)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(lower))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TO_UPPER_CASE, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val str = v.pvalue.strval
        val upper = AbsStr.alpha(s => Str(s.str.toUpperCase))(AbsStr)(str)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(upper))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TRIM, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val str = v.pvalue.strval
        val trimmed = AbsStr.alpha(s => Str(s.str.trim))(AbsStr)(str)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(trimmed))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_BOOL_OBJ, List(expr), Some(aNew)) => {
        val (v, excSet) = V(expr, st)
        val bool = TypeConversionHelper.ToBoolean(v)
        val loc = Loc(aNew, tp)
        val st1 = st.alloc(loc)
        val heap = st1.heap.update(loc, AbsObj.newBooleanObj(bool))
        val st2 = st1.copy(heap = heap)
        val st3 =
          if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st3, newExcSt)
      }
      case (NodeUtil.INTERNAL_NUM_OBJ, List(expr), Some(aNew)) => {
        val (v, excSet) = V(expr, st)
        val num = TypeConversionHelper.ToNumber(v)
        val loc = Loc(aNew, tp)
        val st1 = st.alloc(loc)
        val heap = st1.heap.update(loc, AbsObj.newNumberObj(num))
        val st2 = st1.copy(heap = heap)
        val st3 =
          if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st3, newExcSt)
      }
      case (NodeUtil.INTERNAL_ABS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).abs)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ACOS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).acos)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ASIN, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).asin)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ATAN, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).atan)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ATAN_TWO, List(exprY, exprX), None) => {
        val (y, excSetY) = V(exprY, st)
        val (x, excSetX) = V(exprX, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(y, st.heap)
          .atan2(TypeConversionHelper.ToNumber(x, st.heap)))
        val st1 =
          if (!y.isBottom && !x.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSetX ++ excSetY)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_CEIL, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).ceil)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_COS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).cos)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_EXP, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).exp)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_FLOOR, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).floor)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_LOG, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).log)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_POW, List(exprX, exprY), None) => {
        val (x, excSetX) = V(exprX, st)
        val (y, excSetY) = V(exprY, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(x, st.heap)
          .pow(TypeConversionHelper.ToNumber(y, st.heap)))
        val st1 =
          if (!x.isBottom && !y.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSetX ++ excSetY)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ROUND, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).round)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_SIN, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).sin)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_SQRT, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).sqrt)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TAN, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).tan)
        val st1 =
          if (!v.isBottom) st.varStore(lhs, resV)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_REGEX_TEST, List(thisE, strE), None) => {
        val (thisV, excSet1) = V(thisE, st)
        val (strV, excSet2) = V(strE, st)
        val resV = (thisV.getSingle, strV.getSingle) match {
          case (ConOne(loc: Loc), ConOne(Str(arg))) =>
            val obj = st.heap.get(loc)
            (obj("source").value.getSingle, obj("flags").value.getSingle) match {
              case (ConOne(Str(source)), ConOne(Str(flags))) =>
                AbsBool(true == engine.eval(s"/$source/$flags.test(${JsString(arg).toString});"))
              case _ => AbsBool.Top
            }
          case _ => AbsBool.Top
        }
        val st1 = st.varStore(lhs, resV)
        val newExcSt = st.raiseException(excSet1 ++ excSet2)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_REGEX_EXEC, List(thisE, strE), Some(aNew)) => {
        val (thisV, excSet1) = V(thisE, st)
        val (strV, excSet2) = V(strE, st)

        val aLoc = Loc(aNew, tp)
        val (st1, resV) = try {
          val obj = st.heap.get(thisV.locset)
          val set = strV.gamma match {
            case ConFin(set) => set
            case _ => ???
          }
          (obj(IPrimitiveValue).value.getSingle, obj("lastIndex").value.getSingle) match {
            case (ConOne(Str(prim)), ConOne(Num(num))) =>
              val regexStr = ('"' + prim + '"').parseJson match {
                case JsString(str) => str
                case _ => ???
              }
              val lastIdx = num.toInt
              val (absObj, pval) = set.foldLeft[(AbsObj, AbsPValue)]((AbsObj.Bot, AbsPValue.Bot))((acc, str) => {
                val (accObj, accPVal) = acc
                val arg = str match {
                  case Str(str) => str
                  case _ => ???
                }
                val script = s"var regex = ${regexStr}; regex.lastIndex = $lastIdx; var ret = regex.exec(${JsString(arg).toString}); if(ret) { ret.push(ret.index); ret.push(ret.input); } JSON.stringify(ret);"
                val evalRes = engine.eval(script).toString
                val jsonRes = evalRes.parseJson
                val (absObj, pval): (AbsObj, AbsPValue) = jsonRes match {
                  case JsArray(lst) =>
                    val len = lst.length - 2
                    val absObj = (0 until len).zip(lst).foldLeft(AbsObj.newArrayObject(AbsNum(len))) {
                      case (acc, (i, e)) =>
                        acc.update(i.toString, AbsDataProp(alphaJSONPrimitive(e), AT, AT, AT))
                    }
                    val added = absObj.update("index", AbsDataProp(alphaJSONPrimitive(lst(len)), AT, AT, AT)).update("input", AbsDataProp(alphaJSONPrimitive(lst(len + 1)), AT, AT, AT))
                    (added, AbsPValue.Bot)
                  case JsNull => (AbsObj.Bot, AbsNull.Top)
                  case _ => ???
                }
                (accObj ⊔ absObj, accPVal ⊔ pval)
              })
              if (absObj.isBottom) {
                (st, AbsValue(pval))
              } else {
                val st1 = st.alloc(aLoc)
                val h2 = st1.heap.update(aLoc, absObj)
                (st1.copy(heap = h2), AbsValue(pval, aLoc))
              }
            case _ =>
              val st1 = st.alloc(aLoc)
              val h2 = st1.heap.update(aLoc, AbsObj.Top)
              (st1.copy(heap = h2), AbsValue(AbsPValue.Bot, aLoc))
          }
        } catch {
          case e: Throwable =>
            val state = st.alloc(aLoc)
            val heap = st.heap.update(aLoc, AbsObj.Top)
            val resV = AbsValue(AbsNull.Top, aLoc)
            (st.copy(heap = heap), resV)
        }

        val st2 = st1.varStore(lhs, resV)
        val newExcSt = st.raiseException(excSet1) ⊔ st.raiseException(excSet2)
        (st2, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_IS_OBJ, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) {
            val b1 =
              if (!v.locset.isBottom) AT
              else AB
            val b2 =
              if (!v.pvalue.isBottom) AF
              else AB
            val boolVal = AbsValue(b1 ⊔ b2)
            st.varStore(lhs, boolVal)
          } else {
            AbsState.Bot
          }
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ITER_INIT, List(expr), Some(aNew)) => {
        val (v, excSet1) = V(expr, st)
        val vObj = AbsValue(v.pvalue.copy(
          undefval = AbsUndef.Bot,
          nullval = AbsNull.Bot
        ), v.locset)
        val (locset, st1, excSet2) = TypeConversionHelper.ToObject(tp, vObj, st, aNew)
        val (locset2, st2) =
          if (v.pvalue.undefval.isTop || v.pvalue.nullval.isTop) {
            val heap = st.heap
            val newObj = heap.get(locset) ⊔ AbsObj.Empty
            val loc = Loc(aNew, tp)
            (locset + loc, st.copy(heap = st1.heap ⊔ heap.update(loc, newObj)))
          } else (locset, st1)
        val st3 = st2.varStore(lhs, AbsValue(AbsNum(0), locset2))
        val newExcSt = st.raiseException(excSet1 ++ excSet2)
        (st3, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ITER_HAS_NEXT, List(_, expr), None) => {
        val heap = st.heap
        val (v, excSet) = V(expr, st)
        val locset = v.locset
        val cur = v.pvalue.numval
        val boolV = cur.gamma match {
          case ConInf => AbsBool.Top
          case ConFin(idxSet) => idxSet.foldLeft(AbsBool.Bot) {
            case (b, idx) => locset.foldLeft(b) {
              case (b, loc) => {
                val (strList, astr) = heap.get(loc).keySetPair(heap)
                if (idx < strList.length) b ⊔ AbsBool.True
                else b ⊔ astr.fold(AbsBool.False) { _ => AbsBool.Top }
              }
            }
          }
        }
        val st1 = st.varStore(lhs, boolV)
        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ITER_NEXT, List(_, expr @ CFGVarRef(_, id)), None) => {
        val heap = st.heap
        val (v, excSet) = V(expr, st)
        val locset = v.locset
        val cur = v.pvalue.numval
        val strV = locset.foldLeft(AbsStr.Bot) {
          case (str, loc) => {
            val obj = heap.get(loc)
            val (strList, astr) = heap.get(loc).keySetPair(heap)
            cur.gamma match {
              case ConInf => str ⊔ AbsStr(strList.toSet) ⊔ astr
              case ConFin(idxSet) => idxSet.foldLeft(str) {
                case (str, Num(idx)) => {
                  if (idx < strList.length) str ⊔ AbsStr(strList(idx.toInt))
                  else str ⊔ astr
                }
              }
            }
          }
        }
        val st1 = st.varStore(lhs, strV)
        val next = AbsValue(cur + AbsNum(1), locset)
        val st2 = st1.varStore(id, next)
        val newExcSt = st.raiseException(excSet)
        (st2, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_ADD_EVENT_FUNC, List(exprV), None) => {
        val (v, excSetV) = V(exprV, st)
        val id = NodeUtil.getInternalVarId(NodeUtil.INTERNAL_EVENT_FUNC)
        val (curV, _) = st.lookup(id)
        val newSt = st.varStore(id, curV.locset ⊔ v.locset)
        val newExcSt = st.raiseException(excSetV)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_GET_LOC, List(exprV), None) => {
        val (v, excSetV) = V(exprV, st)
        val locset = v.pvalue.strval.gamma match {
          case ConInf => LocSet.Top
          case ConFin(strset) => LocSet(strset.map(str => Loc(str)))
        }
        val newSt = st.varStore(lhs, locset)
        val newExcSt = st.raiseException(excSetV)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TARGET_FUN, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(ITargetFunction, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_TARGET_FUN, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(ITargetFunction).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_BOUND_THIS, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IBoundThis, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_BOUND_THIS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(IBoundThis).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_BOUND_ARGS, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)
        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IBoundArgs, AbsIValue(p))
            h.update(loc, newObj)
          }
        }
        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_BOUND_ARGS, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val value = obj(IBoundArgs).value
        val st1 =
          if (!v.isBottom) st.varStore(lhs, value)
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_CALL, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)

        val obj = st.heap.get(p.locset)
        val fidset = obj(ICall).fidset

        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(ICall, fidset)
            h.update(loc, newObj)
          }
        }

        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_CONSTRUCT, List(exprO, exprP), None) => {
        val (v, excSetO) = V(exprO, st)
        val (p, excSetP) = V(exprP, st)

        val obj = st.heap.get(p.locset)
        val cidset = obj(IConstruct).fidset

        val newH = v.locset.foldLeft(st.heap) {
          case (h, loc) => {
            val obj = st.heap.get(loc)
            val newObj = obj.update(IConstruct, cidset)
            h.update(loc, newObj)
          }
        }

        val newSt = st.copy(heap = newH).varStore(lhs, p)
        val newExcSt = st.raiseException(excSetO ++ excSetP)
        (newSt, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_HAS_CONST, List(expr), None) => {
        val (v, excSet) = V(expr, st)
        val obj = st.heap.get(v.locset)
        val isDomIn = obj.fold(AbsBool.False) { obj => (obj contains IConstruct) }
        val b1 =
          if (AbsBool.True ⊑ isDomIn) AbsBool.True
          else AbsBool.Bot
        val b2 =
          if (AbsBool.False ⊑ isDomIn) AbsBool.False
          else AbsBool.Bot

        val st1 =
          if (!v.isBottom) st.varStore(lhs, AbsValue(b1 ⊔ b2))
          else AbsState.Bot

        val newExcSt = st.raiseException(excSet)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_MAX2, List(e1, e2), None) => {
        val (v1, es1) = V(e1, st)
        val (v2, es2) = V(e2, st)
        val n1 = v1.pvalue.numval
        val n2 = v2.pvalue.numval
        val b = n1 > n2
        val res1 = if (AT ⊑ b) n1 else AbsNum.Bot
        val res2 = if (AF ⊑ b) n2 else AbsNum.Bot
        val st1 = st.varStore(lhs, AbsValue(res1 ⊔ res2))
        val newExcSt = st.raiseException(es1 ++ es2)
        (st1, excSt ⊔ newExcSt)
      }
      case (NodeUtil.INTERNAL_MIN2, List(e1, e2), None) => {
        val (v1, es1) = V(e1, st)
        val (v2, es2) = V(e2, st)
        val n1 = v1.pvalue.numval
        val n2 = v2.pvalue.numval
        val b = n1 < n2
        val res1 = if (AT ⊑ b) n1 else AbsNum.Bot
        val res2 = if (AF ⊑ b) n2 else AbsNum.Bot
        val st1 = st.varStore(lhs, AbsValue(res1 ⊔ res2))
        val newExcSt = st.raiseException(es1 ++ es2)
        (st1, excSt ⊔ newExcSt)
      }
      case _ =>
        excLog.signal(IRSemanticsNotYetImplementedError(ir))
        (AbsState.Bot, AbsState.Bot)
    }
  }

  def alphaJSONPrimitive(jv: JsValue): AbsValue = jv match {
    case JsBoolean(b) => AbsValue(b)
    case JsString(str) => AbsValue(str)
    case JsNumber(num) => AbsValue(num.toDouble)
    case JsNull => AbsNull.Top
    case _ => ??? //throw new RegexPrimitiveValueError(jv.toString)
  }

  def CI(cp: ControlPoint, i: CFGCallInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    val (_, _, s, e) = internalCI(cp, i, st, excSt)
    (s, e)
  }
  def internalCI(cp: ControlPoint, i: CFGCallInst, st: AbsState, excSt: AbsState): (AbsValue, AbsValue, AbsState, AbsState) = {
    // cons, thisArg and arguments must not be bottom
    val tp = cp.tracePartition
    val loc = Loc(i.asite, tp)
    val st1 = st.alloc(loc)
    val (funVal, funExcSet) = V(i.fun, st1)
    val funLocSet = i match {
      case (_: CFGConstruct) => funVal.locset.filter(l => AT ⊑ st1.heap.hasConstruct(l))
      case (_: CFGCall) => funVal.locset.filter(l => AT ⊑ TypeConversionHelper.IsCallable(l, st1.heap))
    }
    val (thisVal, _) = V(i.thisArg, st1)
    // val thisVal = AbsValue(thisV.getThis(st.heap))
    val (argVal, _) = V(i.arguments, st1)

    // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
    if (thisVal.isBottom || argVal.isBottom) {
      (AbsValue.Bot, AbsValue.Bot, st, excSt)
    } else {
      val oldLocalEnv = st1.context.pureLocal
      val tp = cp.tracePartition
      val nCall = i.block
      val cpAfterCall = ControlPoint(nCall.afterCall, tp)
      val cpAfterCatch = ControlPoint(nCall.afterCatch, tp)

      // Draw call/return edges
      funLocSet.foreach((fLoc) => {
        val funObj = st1.heap.get(fLoc)
        val fidSet = i match {
          case _: CFGConstruct =>
            funObj(IConstruct).fidset
          case _: CFGCall =>
            funObj(ICall).fidset
        }
        fidSet.foreach((fid) => {
          cfg.getFunc(fid) match {
            case Some(funCFG) => {
              val scopeValue = funObj(IScope).value
              val newEnv = AbsLexEnv.newPureLocal(LocSet(loc))
              val (newRec, _) = newEnv.record.decEnvRec
                .CreateMutableBinding(funCFG.argumentsName)
                .SetMutableBinding(funCFG.argumentsName, argVal)
              val (newRec2, _) = newRec
                .CreateMutableBinding("@scope")
                .SetMutableBinding("@scope", scopeValue)
              cp.next(funCFG.entry, CFGEdgeCall, this, st1).foreach(entryCP => {
                val newTP = entryCP.tracePartition
                val exitCP = ControlPoint(funCFG.exit, newTP)
                val exitExcCP = ControlPoint(funCFG.exitExc, newTP)
                val data = EdgeData(
                  AllocLocSet.Empty,
                  newEnv.copy(record = newRec2),
                  thisVal
                )
                addIPEdge(cp, entryCP, data)
                addIPEdge(exitCP, cpAfterCall, EdgeData(
                  st1.allocs,
                  oldLocalEnv,
                  st1.context.thisBinding
                ))
                addIPEdge(exitExcCP, cpAfterCatch, EdgeData(
                  st1.allocs,
                  oldLocalEnv,
                  st1.context.thisBinding
                ))
              })
            }
            case None => excLog.signal(UndefinedFunctionCallError(i.ir))
          }
        })
      })

      val h2 = argVal.locset.foldLeft[AbsHeap](AbsHeap.Bot)((tmpHeap, l) => {
        val argObj = st1.heap.get(l)
        tmpHeap ⊔ st1.heap.update(l, argObj.update("callee", AbsDataProp(funLocSet, AT, AF, AT)))
      })

      // exception handling
      val typeExcSet1 = i match {
        case _: CFGConstruct if funVal.locset.exists(l => AF ⊑ st1.heap.hasConstruct(l)) => Set(TypeError)
        case _: CFGCall if funVal.locset.exists(l => AF ⊑ TypeConversionHelper.IsCallable(l, st1.heap)) => Set(TypeError)
        case _ => ExcSetEmpty
      }
      val typeExcSet2 =
        if (!funVal.pvalue.isBottom) Set(TypeError)
        else ExcSetEmpty

      val totalExcSet = funExcSet ++ typeExcSet1 ++ typeExcSet2
      val newExcSt = st1.raiseException(totalExcSet)

      val h3 =
        if (!funLocSet.isBottom) h2
        else AbsHeap.Bot

      val newSt = st1.copy(heap = h3)
      (thisVal, argVal, newSt, excSt ⊔ newExcSt)
    }
  }

  def V(expr: CFGExpr, st: AbsState): (AbsValue, Set[Exception]) = expr match {
    case CFGVarRef(ir, id) => st.lookup(id)
    case CFGLoad(ir, obj, index) => {
      val (objV, _) = V(obj, st)
      val (idxV, idxExcSet) = V(index, st)
      val absStrSet =
        if (!idxV.isBottom) TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
        else Set[AbsStr]()
      val v1 = Helper.propLoad(objV, absStrSet, st.heap)
      (v1, idxExcSet)
    }
    case CFGThis(ir) =>
      (st.context.thisBinding, ExcSetEmpty)
    case CFGBin(ir, expr1, op, expr2) => {
      val (v1, excSet1) = V(expr1, st)
      val (v2, excSet2) = V(expr2, st)
      (v1, v2) match {
        case _ if v1.isBottom => (AbsValue.Bot, excSet1)
        case _ if v2.isBottom => (AbsValue.Bot, excSet1 ++ excSet2)
        case _ =>
          val h = st.heap
          op.name match {
            case "|" => (Helper.bopBitOr(v1, v2), excSet1 ++ excSet2)
            case "&" => (Helper.bopBitAnd(v1, v2), excSet1 ++ excSet2)
            case "^" => (Helper.bopBitXor(v1, v2), excSet1 ++ excSet2)
            case "<<" => (Helper.bopLShift(v1, v2), excSet1 ++ excSet2)
            case ">>" => (Helper.bopRShift(v1, v2), excSet1 ++ excSet2)
            case ">>>" => (Helper.bopURShift(v1, v2), excSet1 ++ excSet2)
            case "+" => (Helper.bopPlus(v1, v2), excSet1 ++ excSet2)
            case "-" => (Helper.bopMinus(v1, v2), excSet1 ++ excSet2)
            case "*" => (Helper.bopMul(v1, v2), excSet1 ++ excSet2)
            case "/" => (Helper.bopDiv(v1, v2), excSet1 ++ excSet2)
            case "%" => (Helper.bopMod(v1, v2), excSet1 ++ excSet2)
            case "==" => (Helper.bopEqBetter(h, v1, v2), excSet1 ++ excSet2)
            case "!=" => (Helper.bopNeq(h, v1, v2), excSet1 ++ excSet2)
            case "===" => (Helper.bopSEq(h, v1, v2), excSet1 ++ excSet2)
            case "!==" => (Helper.bopSNeq(h, v1, v2), excSet1 ++ excSet2)
            case "<" => (Helper.bopLess(v1, v2), excSet1 ++ excSet2)
            case ">" => (Helper.bopGreater(v1, v2), excSet1 ++ excSet2)
            case "<=" => (Helper.bopLessEq(v1, v2), excSet1 ++ excSet2)
            case ">=" => (Helper.bopGreaterEq(v1, v2), excSet1 ++ excSet2)
            case "instanceof" =>
              val locSet1 = v1.locset
              val locSet2 = v2.locset
              val locSet3 = locSet2.filter((l) => AT ⊑ st.heap.hasInstance(l))
              val protoVal = locSet3.foldLeft(AbsValue.Bot)((v, l) => {
                v ⊔ st.heap.get(l).Get("prototype", st.heap)
              })
              val locSet4 = protoVal.locset
              val locSet5 = locSet2.filter((l) => AF ⊑ st.heap.hasInstance(l))
              val b1 = locSet1.foldLeft[AbsValue](AbsValue.Bot)((tmpVal1, loc1) => {
                locSet4.foldLeft[AbsValue](tmpVal1)((tmpVal2, loc2) =>
                  tmpVal2 ⊔ Helper.inherit(st.heap, loc1, loc2))
              })
              val b2 =
                if (!v1.pvalue.isBottom && !locSet4.isBottom) AbsValue(AF)
                else AbsValue.Bot
              val excSet3 =
                if (!v2.pvalue.isBottom || !locSet5.isBottom || !protoVal.pvalue.isBottom) Set(TypeError)
                else ExcSetEmpty
              val b = b1 ⊔ b2
              val excSet = excSet1 ++ excSet2 ++ excSet3
              (b, excSet)
            case "in" => {
              val str = TypeConversionHelper.ToString(v1, st.heap)
              val absB = v2.locset.foldLeft(AB)((tmpAbsB, loc) => {
                tmpAbsB ⊔ st.heap.get(loc).HasProperty(str, st.heap)
              })
              val b = AbsValue(absB)
              val excSet3 =
                if (!v2.pvalue.isBottom) Set(TypeError)
                else ExcSetEmpty
              val excSet = excSet1 ++ excSet2 ++ excSet3
              (b, excSet)
            }
          }
      }
    }
    case CFGUn(ir, op, expr) => {
      val (v, excSet) = V(expr, st)
      op.name match {
        case "void" => (Helper.uVoid(v), excSet)
        case "+" => (Helper.uopPlus(v), excSet)
        case "-" => (Helper.uopMinusBetter(st.heap, v), excSet)
        case "~" => (Helper.uopBitNeg(v), excSet)
        case "!" => (Helper.uopNeg(v), excSet)
        case "typeof" =>
          expr match {
            case CFGVarRef(_, x) =>
              val absStr1 = TypeConversionHelper.typeTag(v, st.heap)
              val absStr2 =
                if (excSet.contains(ReferenceError)) AbsStr("undefined")
                else AbsStr.Bot
              val absStr = absStr1 ⊔ absStr2
              (AbsValue(absStr), ExcSetEmpty)
            case _ =>
              val absStr = TypeConversionHelper.typeTag(v, st.heap)
              (AbsValue(absStr), excSet)
          }
      }
    }
    case CFGInternalValue(ir, name) => getInternalValue(name) match {
      case Some(value) => (value, ExcSetEmpty)
      case None =>
        excLog.signal(IRSemanticsNotYetImplementedError(ir))
        (AbsValue.Bot, ExcSetEmpty)
    }
    case CFGVal(ejsVal) =>
      val pvalue: AbsPValue = ejsVal match {
        case EJSNumber(_, num) => AbsPValue(num)
        case EJSString(str) => AbsPValue(str)
        case EJSBool(bool) => AbsPValue(bool)
        case EJSNull => AbsPValue(Null)
        case EJSUndef => AbsPValue(Undef)
      }
      (AbsValue(pvalue), ExcSetEmpty)
  }

  def B(expr: CFGExpr, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    val st1 = st //TODO should be the pruned state

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)
    val st2 =
      if (AbsBool(true) ⊑ TypeConversionHelper.ToBoolean(v)) st1
      else AbsState.Bot

    (st2, excSt ⊔ newExcSt)
  }
}

// Interprocedural edges
case class EdgeData(allocs: AllocLocSet, env: AbsLexEnv, thisBinding: AbsValue) {
  def ⊔(other: EdgeData): EdgeData = EdgeData(
    this.allocs ⊔ other.allocs,
    this.env ⊔ other.env,
    this.thisBinding ⊔ other.thisBinding
  )
  def ⊑(other: EdgeData): Boolean = {
    this.allocs ⊑ other.allocs &&
      this.env ⊑ other.env &&
      this.thisBinding ⊑ other.thisBinding
  }

  def subsLoc(from: Loc, to: Loc): EdgeData = EdgeData(
    allocs.subsLoc(from, to),
    env.subsLoc(from, to),
    thisBinding.subsLoc(from, to)
  )

  def weakSubsLoc(from: Loc, to: Loc): EdgeData = EdgeData(
    allocs.weakSubsLoc(from, to),
    env.weakSubsLoc(from, to),
    thisBinding.weakSubsLoc(from, to)
  )

  def fix(given: AllocLocSet): EdgeData = given.mayAlloc.foldLeft(this) {
    case (data, loc) => {
      val EdgeData(allocs, env, thisBinding) = loc match {
        case locR @ Recency(l, Recent) => {
          val locO = Recency(l, Old)
          if (given.mustAlloc contains locR) data.subsLoc(locR, locO)
          else data.weakSubsLoc(locR, locO)
        }
        case _ => data
      }
      val newAllocs =
        if (given.mustAlloc contains loc) allocs.alloc(loc)
        else allocs.weakAlloc(loc)
      EdgeData(newAllocs, env, thisBinding)
    }
  }
}
object EdgeData {
  val Bot: EdgeData = EdgeData(AllocLocSet.Bot, AbsLexEnv.Bot, AbsValue.Bot)
}

// call infomation
case class CallInfo(state: AbsState, thisVal: AbsValue, argVal: AbsValue)
