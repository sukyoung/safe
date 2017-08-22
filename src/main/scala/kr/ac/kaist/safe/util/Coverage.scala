/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.DefaultState._
import kr.ac.kaist.safe.concolic._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.phase._

import scala.collection.mutable.HashMap

class Coverage(
    var cfg: CFG,
    val semantics: Semantics
) {

  val irRoot: IRNode = cfg.ir

  var debug = false
  var timing = false

  var total = 0
  var executed = 0
  var execSet = scala.collection.immutable.Set[Long](0)
  //var isCount = true

  // To print condition expressions
  var conditions = List[IRNode]()
  def printCondition(cond: IRNode): Unit =
    if (!conditions.contains(cond)) {
      conditions = conditions :+ cond
      val ircode = new JSIRUnparser(cond).result
      System.out.println(ircode)
    }

  // For concolic test
  var inputIR: Option[IRStmt] = None
  var input = Map[String, Id]()
  var additional = List[Stmt]()

  var necessaries: List[SymbolicValue] = null
  def isNecessary(value: String): Boolean = necessaries.map(_.toString).contains(value)

  var inum = 0
  var report: List[SymbolicInfo] = null
  var constraints = List[ConstraintForm]()
  var target: String = null

  // For analysis
  var typing = Analyze

  // Functions to test
  var functions: HashMap[String, FunctionInfo] = HashMap[String, FunctionInfo]()
  functions.put("<>Concolic<>Main", new FunctionInfo)

  // For assistance
  // To initialize when it first tests a function.
  var isFirst: Boolean = true

  def setInput(result: Option[Map[String, (Id, List[Stmt])]]): Unit = {
    // Initialize
    input = Map[String, Id]()
    additional = List[Stmt]()
    result match {
      case Some(res) =>
        for (entry <- res) {
          val (key, (id, stmts)) = entry
          input += key -> id
          additional = additional ::: stmts
        }
      case None =>
    }
  }
  def setInputNumber(n: Int): Unit = {
    inum = n
  }

  val CG: CallGenerator = new CallGenerator(this)
  def setupCall(): Option[IRStmt] = CG.setupCall(target)

  def getConstraints: List[ConstraintForm] = constraints

  // Check whether testing a function continue or not.
  def continue: Boolean = {
    isFirst = false
    constraints.nonEmpty
  }
  def existCandidate: Boolean = functions.exists({ case (_, finfo) => finfo.isCandidate })
  def removeTarget(): Unit = {
    functions.get(target) match {
      case Some(info) =>
        // Need to initialize
        constraints = List[ConstraintForm]()
        input = Map[String, Id]()
        info.done()
      case None =>
        System.out.println("Target should be function type")
    }
    if (functions.nonEmpty) {
      val filters = functions.filter({ case (_, x) => x.isCandidate })
      if (filters.nonEmpty) {
        // Sort because of test file
        target = filters.keySet.toList.sorted.head
        functions(target).targeting()
        //filters.head._2.targeting
        //target = filters.head._1
      } else
        target = null
    }
    isFirst = true
  }
  def checkTarget(fun: String): Boolean = functions.get(fun) match {
    case Some(f) => f.isTarget
    case None => false
  }

  // Using static analysis, store function information.
  def updateFunction(cfgRoot: CFGNode): Unit = {
    val cfgNodes = CFGCollector.collect(cfgRoot).filter({
      case c: CFGFunction => c.isUser
      case _ => true
    })
    for (k <- cfgNodes) {
      k match {
        case inst @ CFGConstruct(ir, block, fun, thisArg, arguments, asite) if block.func.isUser =>
          identifyFunction(inst, thisArg)
        case inst @ CFGCall(ir, block, fun, thisArg, arguments, asite) if block.func.isUser =>
          identifyFunction(inst, thisArg)
        case _ =>
      }
    }
  }

  def identifyFunction(inst: CFGInst, thisArg: CFGExpr): Unit = {
    val cfgBlock = inst.block

    var functionName: String = cfgBlock.func.name //null

    // To find a constructor name of this argument.
    val thisinfo = new TypeInfo("Object")

    var cstate = semantics.getState(cfgBlock) //stateManager.getOutputCState(cfgBlock, inst.id)
    var thisNames = List[String]()
    for ((callContext, Dom(heap, context)) <- cstate) {
      val bindingValue = context.pureLocal.record.decEnvRec.GetBindingValue(thisArg.toString)
      val (absValue, _) = bindingValue
      val lset = absValue.locset
      // Compute this object
      val thisObj = computeObject(heap, lset)
      val temp = thisNames ::: computeConstructorName(heap, thisObj)
      thisNames = temp.distinct
    }
    thisinfo.addConstructors(thisNames)

    // To find other information of a function like argument type, it uses different heap, input states, because only successor nodes of input states can be entry nodes of functions.
    val finfo = new FunctionInfo
    cstate = semantics.getState(cfgBlock)
    for ((callContext, state) <- cstate) {
      val controlPoint: ControlPoint = ControlPoint(cfgBlock, callContext)

      semantics.getInterProcSucc(controlPoint) match {
        case Some(succMap) =>
          // succMap = Map[ControlPoint, EdgeData]; succCp = ControlPoint
          for ((succCP, _) <- succMap) {

            val succBlock: CFGBlock = succCP.block
            val func = succBlock.func
            val argvars = func.argVars // cfg.getArgVars(func)
            functionName = func.name

            val tpStateMap = semantics.getState(succBlock)
            for ((tp, state @ Dom(heap, context)) <- tpStateMap) {
              val (funValue, _) = context.pureLocal.record.decEnvRec.GetBindingValue(func.argumentsName)
              val arglset = funValue.locset
              // Number of an argument
              var i = 0
              val hn = argvars.foldLeft(heap)((hh, x) => {
                val vi = arglset.foldLeft[AbsValue](DefaultValue.Bot)((vv, argloc) => {
                  vv + hh.proto(argloc, Utils.AbsString.alpha(i.toString))
                })

                val viTypes = vi.typeKinds
                for (t <- viTypes) {
                  val tinfo = new TypeInfo(t)
                  if (t == "Object") {
                    if (state </ DefaultState.Bot) {
                      // Compute object
                      val lset = vi.locset
                      val obj = computeObject(hh, lset)

                      // TODO: WARNING, just use one location in location set.
                      // To distinguish array objects and user generated objects
                      var isArray = false
                      for (l <- lset) {
                        hh.isArray(l).getSingle match {
                          case ConMany() =>
                          case ConOne(bool) => isArray = isArray || bool
                          case ConZero() =>
                        }
                      }

                      if (isArray) {
                        tinfo.addConstructors(List("Array"))
                        val arrayObj = computeObject(hh, lset)
                        var length = arrayObj("length").value.pvalue.toString
                        if (length == "UInt") {
                          length = "0"
                        }
                        tinfo.setProperties(List(length))
                      } else {
                        // Compute object constructor name
                        tinfo.addConstructors(computeConstructorName(hh, obj))

                        // Compute object properties
                        val properties: List[String] = computePropertyList(obj, false)
                        tinfo.setProperties(properties)
                      }
                    }
                  }
                  finfo.storeParameter(i, tinfo)
                }
                //finfo.setCandidate
                i += 1
                state.createMutableBinding(x, vi)
                state.heap
              })

              // Compute this object.
              if (func.isUser)
                finfo.setCandidate()
              if (functionName.contains(".")) {
                val thislset = context.thisBinding.locset
                val thisObj = computeObject(hn, thislset)

                val properties = computePropertyList(thisObj, true)
                thisinfo.setProperties(properties)
              }
            }
          }
        case None =>
      }
    }
    if (functionName != null) {
      // Except built-in constructors
      //if (!functionName.contains("constructor"))
      //finfo.setCandidate

      if (functionName.contains(".")) {
        finfo.setThisObject(thisinfo)
      }

      functions.put(functionName, finfo)
    }
  }

  def computePropertyList(obj: AbsObject, onlyPrimitive: Boolean): List[String] = {
    val propNames = obj.abstractKeySet
    val propNamesSet: Set[AbsString] = propNames match {
      case ConFin(s) => s
      case ConInf() => Set()
    }
    val propValuesAbs: List[AbsString] = propNamesSet.foldLeft[List[AbsString]](Nil)((list, prop) => {
      val p = obj(prop).value
      if (!onlyPrimitive || !p.typeKinds.contains("Object"))
        prop :: list
      else
        list
    })
    val propValuesConc: List[String] = propValuesAbs.foldLeft[List[String]](Nil)((list, x) => {
      val concreteStrings = x.gamma match {
        case ConFin(strings) =>
          strings.map(_.str).toList
        case ConInf() =>
          Nil
      }
      list ++ concreteStrings
    })
    propValuesConc
  }

  def computeObject(heap: AbsHeap, lset: AbsLoc): AbsObject = {
    lset.foldLeft(DefaultObject.Bot)({
      case (obj, loc) =>
        obj + heap.get(loc)
    })
  }

  def computeConstructorName(heap: AbsHeap, obj: AbsObject): List[String] = {
    // Compute prototype object
    val lset1 = obj(IPrototype).value.locset
    val proto = computeObject(heap, lset1)

    // Compute constructor object
    val lset2 = proto("constructor").value.locset
    val constructor = computeObject(heap, lset2)

    // Get constructor names
    val temp = constructor(IConstruct).fidset
    temp.map((fid: FunctionId) => cfg.getFunc(fid).get.name).toList

    //    val temp = constructor("@construct")._3.toSet
    //    temp.map(_.name).toList
  }
}
