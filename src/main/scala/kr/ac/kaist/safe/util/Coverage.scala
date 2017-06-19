///**
// * *****************************************************************************
// * Copyright (c) 2016, KAIST.
// * All rights reserved.
// *
// * Use is subject to license terms.
// *
// * This distribution may include materials developed by third parties.
// * ****************************************************************************
// */
//
//package kr.ac.kaist.safe.util
//
//import _root_.java.util.{ List => JList }
////import edu.rice.cs.plt.tuple.Option
//import kr.ac.kaist.safe.concolic._
//import kr.ac.kaist.safe.nodes.ast._
//import kr.ac.kaist.safe.nodes.ir._
//import kr.ac.kaist.safe.nodes.cfg._
//import kr.ac.kaist.safe.util.useful.Lists
//import kr.ac.kaist.safe.util.useful.Options._
//import kr.ac.kaist.safe.analyzer._
////TODO MV import kr.ac.kaist.safe.analyzer.typing.{ SemanticsExpr => SE }
//
//import scala.collection.mutable.HashMap
//
//class Coverage() {
//  var debug = false
//  var timing = false
//
//  var total = 0
//  var executed = 0
//  var execSet = scala.collection.immutable.Set[Long](0);
//  //var isCount = true
//
//  // To print condition expressions
//  var conditions = List[IRNode]()
//  def printCondition(cond: IRNode) =
//    if (!conditions.contains(cond)) {
//      conditions = conditions :+ cond
//      val ircode = new JSIRUnparser(cond).doit
//      System.out.println(ircode)
//    }
//
//  // For concolic test
//  var inputIR: Option[IRStmt] = None
//  var input = Map[String, Id]()
//  var additional = List[Stmt]()
//
//  var necessaries: List[SymbolicValue] = null
//  def isNecessary(value: String) = necessaries.map(_.toString).find(_ == value).isSome
//
//  var inum = 0
//  var report: List[SymbolicInfo] = null
//  var constraints = List[ConstraintForm]()
//  var target: String = null
//
//  // For analysis
//  var cfg: CFG = null
//// TODO MV removed var typing: TypingInterface = null
//  var semantics: Semantics = null
//  var stateManager: StateManager = null
//
//  // Functions to test
//  var functions: HashMap[String, FunctionInfo] = HashMap[String, FunctionInfo]()
//  functions.put("<>Concolic<>Main", new FunctionInfo)
//
//  // For assistance
//  // To initialize when it first tests a function.
//  var isFirst: Boolean = true
//
//  def setInput(result: Option[Map[String, (Id, List[Stmt])]]) = {
//    // Initialize
//    input = Map[String, Id]()
//    additional = List[Stmt]()
//    result match {
//      case Some(res) =>
//        for (key <- res.keySet) {
//          input += key -> res(key)._1
//          additional = additional ::: res(key)._2
//        }
//      case None =>
//    }
//  }
//  def setInputNumber(n: Int) = {
//    inum = n
//  }
//
//// TODO MV removed  val CG: CallGenerator = new CallGenerator(this)
//// TODO MV removed  def setupCall() = CG.setupCall(target)
//
//  def getConstraints = constraints
//  def getJavaConstraints: JList[ConstraintForm] = Lists.toJavaList(constraints)
//
//  // Check whether testing a function continue or not.
//  def continue: Boolean = { isFirst = false; constraints.nonEmpty }
//  def existCandidate = functions.filter(x => x._2.isCandidate).nonEmpty
//  def removeTarget = {
//    functions.get(target) match {
//      case Some(info) =>
//        // Need to initialize
//        constraints = List[ConstraintForm]()
//        input = Map[String, Id]()
//        info.done
//      case None =>
//        System.out.println("Target should be function type")
//    }
//    if (functions.nonEmpty) {
//      var filters = functions.filter(x => x._2.isCandidate)
//      if (filters.nonEmpty) {
//        // Sort because of test file
//        target = filters.keySet.toList.sorted.head
//        functions(target).targeting
//        //filters.head._2.targeting
//        //target = filters.head._1
//      } else
//        target = null
//    }
//    isFirst = true
//  }
//  def checkTarget(fun: String) = functions.get(fun) match { case Some(f) => f.isTarget; case None => false }
//
//  // Using static analysis, store function information.
//  def updateFunction = {
//    for (k <- NodeRelation.cfg2irMap.keySet) {
//      k match {
//        case inst @ CFGConstruct(iid, info, cons, thisArg, arguments, addr, addr2) => identifyFunction(inst, thisArg) //identifyFunction(inst)
//        case inst @ CFGCall(iid, info, fun, thisArg, arguments, addr, addr2) => identifyFunction(inst, thisArg) //identifyFunction(inst)
//        case _ =>
//      }
//    }
//  }
//
//  def identifyFunction(inst: CFGInst, thisArg: CFGExpr) = {
//    val cfgNode = cfg.findEnclosingNode(inst)
//
//    var functionName: String = null
//
//    // To find a constructor name of this argument.
//    var thisinfo = new TypeInfo("Object")
//    var cstate = stateManager.getOutputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
//    var thisNames = List[String]()
//    for ((callContext, state) <- cstate) {
//      var lset = state._1(SinglePureLocalLoc)(thisArg.toString)._1._1._2.toSet
//      // Compute this object
//      val thisObj = computeObject(state._1, lset)
//      val temp = thisNames ::: computeConstructorName(state._1, thisObj)
//      thisNames = temp.distinct
//    }
//    thisinfo.addConstructors(thisNames)
//
//    // To find other information of a function like argument type, it uses different heap, input states, because only successor nodes of input states can be entry nodes of functions.
//    var finfo = new FunctionInfo
//    cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
//    for ((callContext, state) <- cstate) {
//      val controlPoint: ControlPoint = (cfgNode, callContext)
//
//      semantics.getIPSucc(controlPoint) match {
//        case Some(succMap) =>
//          for ((succCP, (succContext, succObj)) <- succMap) {
//
//            val fid = succCP._1._1
//            val argvars = cfg.getArgVars(fid)
//
//            for ((callContext, state) <- stateManager.getOutputCState(succCP._1, inst.getInstId, _MOST_SENSITIVE)) {
//              val arglset = state._1(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
//              // Number of an argument
//              var i = 0
//              val h_n = argvars.foldLeft(state._1)((hh, x) => {
//                val v_i = arglset.foldLeft(ValueBot)((vv, argloc) => {
//                  vv + Helper.Proto(hh, argloc, AbsString.alpha(i.toString))
//                })
//
//                var v_i_types = v_i.typeKinds.split(", ")
//                for (t <- v_i_types) {
//                  var tinfo = new TypeInfo(t)
//                  if (t == "Object") {
//                    if (state </ StateBot) {
//                      // Compute object
//                      var lset = v_i._2.toSet
//                      val obj = computeObject(hh, lset)
//
//                      // TODO: WARNING, just use one location in location set.
//                      // To distinguish array objects and user generated objects
//                      var isArray = false
//                      for (l <- lset) {
//                        Helper.IsArray(hh, l).getSingle match {
//                          case Some(bool) => isArray = isArray || bool
//                          case None =>
//                        }
//                      }
//
//                      if (isArray) {
//                        tinfo.addConstructors(List("Array"))
//                        val arrayObj = computeObject(hh, lset)
//                        var length = arrayObj("length")._1._1._1.toString
//                        if (length == "UInt")
//                          length = "0"
//                        tinfo.setProperties(List(length))
//                      } else {
//                        // Compute object constructor name
//                        tinfo.addConstructors(computeConstructorName(hh, obj))
//
//                        // Compute object properties
//                        val properties = computePropertyList(obj, false)
//                        tinfo.setProperties(properties)
//                      }
//                    }
//                  }
//                  finfo.storeParameter(i, tinfo)
//                }
//                //finfo.setCandidate
//                i += 1
//                Helper.CreateMutableBinding(hh, x, v_i)
//              })
//
//              // Compute this object.
//              functionName = cfg.getFuncName(fid)
//              if (cfg.isUserFunction(fid))
//                finfo.setCandidate
//              if (functionName.contains(".")) {
//                val thislset = state._1(SinglePureLocalLoc)("@this")._2._2.toSet
//                val thisObj = computeObject(h_n, thislset)
//
//                var properties = computePropertyList(thisObj, true)
//                thisinfo.setProperties(properties)
//              }
//            }
//          }
//        case None =>
//      }
//    }
//    if (functionName != null) {
//      // Except built-in constructors
//      //if (!functionName.contains("constructor"))
//      //finfo.setCandidate
//
//      if (functionName.contains("."))
//        finfo.setThisObject(thisinfo)
//
//      functions.put(functionName, finfo)
//    }
//  }
//
//  def computePropertyList(obj: Obj, onlyPrimitive: Boolean): List[String] = {
//    var properties = obj.getProps.foldLeft[List[String]](List())((list, prop) => {
//      val p = obj(prop)._1._1._1
//      if (!onlyPrimitive || !p.typeKinds.contains("Object"))
//        prop :: list
//      else
//        list
//    })
//    properties
//  }
//
//  def computeObject(heap: Heap, lset: Set[Loc]): Obj = {
//    if (lset.size == 0)
//      Obj.bottom
//    else if (lset.size == 1)
//      heap(lset.head)
//    else
//      lset.tail.foldLeft(heap(lset.head))((o, l) => o + heap(l))
//  }
//
//  def computeConstructorName(heap: Heap, obj: Obj): List[String] = {
//    // Compute prototype object
//    var lset = obj("@proto")._1._1._2.toSet
//    val proto = computeObject(heap, lset)
//
//    // Compute constructor object
//    lset = proto("constructor")._1._1._2.toSet
//    val constructor = computeObject(heap, lset)
//
//    // Get constructor names
//    val temp = constructor("@construct")._3.toSet
//    temp.map(cfg.getFuncName(_)).toList
//  }
//}
