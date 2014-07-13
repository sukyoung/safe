/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.math.BigInteger
import _root_.java.lang.{Integer => JInteger}
import _root_.java.util.{Map => JavaMap}
import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.CallContext._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.bug_detector.StateManager
import kr.ac.kaist.jsaf.concolic.{ConstraintForm, FunctionInfo}
import kr.ac.kaist.jsaf.concolic.{ConcolicSolver, IRGenerator}
import kr.ac.kaist.jsaf.exceptions.ConcolicError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF, NodeFactory => NF, NodeRelation => NR, NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Maps._
import scala.collection.mutable.HashMap
import scala.util.Random

/* Calculates code coverage. */
class Coverage() {
  var debug = false 

  var total = 0
  var executed = 0
  var execSet = scala.collection.immutable.Set[Long](0);
  //var isCount = true

  // To print condition expressions
  var conditions = List[IRNode]()
  def printCondition(cond: IRNode) = 
    if (!conditions.contains(cond)) {
      conditions = conditions:+cond
      val ircode = new JSIRUnparser(cond).doit
      System.out.println(ircode)
    }

  // For concolic test
  var inputIR: Option[IRStmt] = None
  var input = Map[String, Id]()
  var additional = List[Stmt]()
  var inum = 0
  var constraints = List[ConstraintForm]()
  var target:String = null
  var isFirst: Boolean = true

  // For analysis
  var cfg: CFG = null
  var typing: TypingInterface = null
  var semantics: Semantics = null
  var stateManager: StateManager = null

  // functions to test
  var functions: HashMap[String, FunctionInfo] = HashMap[String,FunctionInfo]()
  functions.put("<>Concolic<>Main", new FunctionInfo)

  def toInt(n: JInteger):Int = n.intValue()
  def setInput(result: Option[Map[String, (Id, List[Stmt])]]) = {
    // Initialize 
    input = Map[String, Id]()
    additional = List[Stmt]()
    result match { 
      case Some(res) =>
        for (key <- res.keySet) {
          input += key -> res(key)._1
          additional = additional:::res(key)._2
        }
      case None =>
    }
  }
  def setInputNumber(n: Int) = {
    inum = n
  }

  val dummySpan = IF.dummySpan("Input")
  def setupCall():Option[IRStmt] = {
    if (target == null) return None
    var env = List[(String, IRId)]()
    if (target.contains("prototype")) {
      // tokenize target name to generate each object and function.
      val token = target.substring(0, target.indexOf("<")).split("prototype")
      if (token.length > 2) throw new ConcolicError("Only a.prototype.x function form is supported") 
      val first = token(0).substring(0, token(0).length-1)
      val second = token(1).substring(1, token(1).length) 

      makeFunApp(first, true) match {
        case Some(x) => 
          val obj = NF.makeNew(dummySpan, x)
          val fresh = NodeUtil.freshName("this")
          val objRef = NF.makeId(dummySpan, fresh, fresh) 

          var stmts = List[Stmt]()
          stmts = stmts:+(new ConcolicSolver).assignValue(objRef, obj)

          stmts = functions(target).thisObject.foldLeft[List[Stmt]](stmts)((list, p) => {
            val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, objRef.getText, objRef.getText))
            val lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, p._1, p._1))
            var rhs = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
            if (input.contains("this."+p._1))
              rhs = NF.makeIntLiteral(dummySpan, new BigInteger(input("this."+p._1).toString)) 
            list:+NF.makeExprStmt(dummySpan, 
                                  NF.makeAssignOpApp(dummySpan, 
                                                    lhs, 
                                                    NF.makeOp(dummySpan, "="),
                                                    rhs))
          })
          additional = additional:::stmts

          val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, objRef.getText, objRef.getText))
          val fun = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, second, second))
          //val fun = NF.makeDot(dummySpan, NF.makeParenthesized(dummySpan, obj), NF.makeId(dummySpan, second, second))

          makeArgs(target, false) match {
            case Some(args) =>
              val ir = additional.foldLeft[List[IRStmt]](List())((list, s) => {
                list:+IRGenerator.additional2ir(s, env) 
              })
              val funapp = NF.makeFunApp(dummySpan, fun, args)
              val res = IF.makeTId(funapp, dummySpan, NU.ignoreName)
              val funir = IRGenerator.funapp2ir(funapp, env, res, target)
              // Make the cutline to calculate coverage.
              //val cutline = new IRInternalCall(makeSpanInfo(false, span), lhs, fun, arg1, toJavaOption(arg2))
              Some(new IRStmtUnit(new IRSpanInfo(false, dummySpan), ir:+funir))
            case None => None
          }
        case None => None
      }
    }
    else {
      makeFunApp(target, false) match {
        case Some(funapp) =>
          val ir = additional.foldLeft[List[IRStmt]](List())((list, s) => {
            list:+IRGenerator.additional2ir(s, env) 
          })
          val res = IF.makeTId(funapp, dummySpan, NU.ignoreName)
          val funir = IRGenerator.funapp2ir(funapp, env, res, target)
          Some(new IRStmtUnit(new IRSpanInfo(true, dummySpan), ir:+funir))
        case None => None
      }
    }
  }

  def makeFunApp(target: String, isObject: Boolean): Option[FunApp] = {
    makeArgs(target, isObject) match { 
      case Some(args) =>
        val fun = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, target, target))
        Some(NF.makeFunApp(dummySpan, fun, args))
      case None => 
        None
    }
  }

  def makeArgs(target: String, isObject: Boolean):Option[List[Expr]] = {
    for (k <- NR.ir2astMap.keySet) {
      k match { 
        case SIRFunctional(_, name, params, args, fds, vds, body) =>
          if (name.getUniqueName == target) { 
            val p = functions(target).params.size
            //System.out.println("Coverage, target function %s 's parameter number is %s ".format(target, p))
            // calculate the number of input to generate
            if (!isObject)
              setInputNumber(p)

            var args = List[Expr]()
            //TODO: Handle multiple type
            for (n <- 0 until p)
              functions(target).getObjectProperties(n) match {
                case Some(props) => 
                  if (n < input.size && !isObject)
                    args = args:+NF.makeVarRef(dummySpan, input("i"+n))
                  else {
                    val fresh = NodeUtil.freshName("a")
                    val arg = NF.makeId(dummySpan, fresh, fresh) 
                    val addstmt = (new ConcolicSolver).assignObject(false, n, arg, functions(target).getObjectConstruct(n), props, Map[String, Int]()) 
                    additional = additional:::addstmt
                    args = args:+NF.makeVarRef(dummySpan, arg)
                  }
                case None =>
                  args = 
                    if (n < input.size && !isObject) 
                      args:+NF.makeVarRef(dummySpan, input("i"+n))
                      //args:+input("i"+n)
                    else 
                      args:+NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(5).toString)) 
              }
            return Some(args)
          }
        case _ =>
      }
    }
    return None
  }

  def getConstraints = constraints
  def getJavaConstraints:JList[ConstraintForm] = toJavaList(constraints)

  def continue:Boolean = {isFirst = false; constraints.nonEmpty}
  def existCandidate = functions.filter(x => x._2.isCandidate).nonEmpty
  def removeTarget = {
    functions.get(target) match {
      case Some(info) =>
        // Need to initialize
        constraints = List[ConstraintForm]()
        input = Map[String, Id]()
        info.done
      case None => 
        System.out.println("Target should be function type")
    }
    if (functions.nonEmpty) {
      var filters = functions.filter(x => x._2.isCandidate)
      if (filters.nonEmpty) {
        // Sort because of test file
        target = filters.keySet.toList.sorted.head
        functions(target).targeting
        //filters.head._2.targeting
        //target = filters.head._1
      }
      else
        target = null
    }
    isFirst = true
  }
  def setProcessing(fun: String) = functions.get(fun) match { case Some(f) => f.processing; case None => }
  def setUnprocessing(fun: String) = functions.get(fun) match { case Some(f) => f.unprocessing; case None => }
  def checkTarget(fun: String) = functions.get(fun) match { case Some(f) => f.isTarget; case None => false }
  def checkProcessing(fun: String) = functions.get(fun) match { case Some(f) => f.isProcess; case None => false }

  // using static analysis, store function information
  def updateFunction = {
    for (k <- NodeRelation.cfg2irMap.keySet) {
      k match {
        case inst@CFGConstruct(iid, info, cons, thisArg, arguments, addr, addr2) => identifyFunction(inst)
        case inst@CFGCall(iid, info, fun, thisArg, arguments, addr, addr2) => identifyFunction(inst)
        case _ =>
      }
    }
  }
  def identifyFunction(inst: CFGInst) = {
    val cfgNode = cfg.findEnclosingNode(inst)
    val cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
    for ((callContext, state) <- cstate) {
      val controlPoint: ControlPoint = (cfgNode, callContext)  
      semantics.getIPSucc(controlPoint) match {
        case Some(succMap) => 
          for ((succCP, (succContext, succObj)) <- succMap) {
            val fid = succCP._1._1
            //System.out.println("Coverage: updateFunction: "+cfg.getFuncName(fid))
            if (!functions.contains(cfg.getFuncName(fid))) {
              var finfo = new FunctionInfo
              val argvars = cfg.getArgVars(fid)
              for ((callContext, state) <- stateManager.getOutputCState(succCP._1, inst.getInstId, _MOST_SENSITIVE)) {
                val arglset = state._1(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._1._2
                var i = 0
                val h_n = argvars.foldLeft(state._1)((hh, x) => {
                  val v_i = arglset.foldLeft(ValueBot)((vv, argloc) => {
                    vv + Helper.Proto(hh, argloc, AbsString.alpha(i.toString))
                  })
      
                  var v_i_types = v_i.typeKinds
          
                  if (finfo.isNewType(i, v_i_types)) {
                    if (v_i_types.contains("Object")) { 
                      if (state </ StateBot) {
                        // Compute object 
                        var lset = v_i._2.toSet 
                        val obj = 
                          if (lset.size == 0)
                            ObjBot
                          else if (lset.size == 1)
                            hh(lset.head)
                          else {
                            println("Coverage, there are multiple location set")
                            lset.tail.foldLeft(hh(lset.head))((o, l) => o + hh(l))
                          }
                        /*System.out.println("function: "+cfg.getFuncName(fid))
                        System.out.println("i: "+i)
                        System.out.println("lset size : "+ lset.size)
                        System.out.println("obj: "+ DomainPrinter.printObj(4, obj))*/

                        // TODO: WARNING, just use one location in location set.
                        Helper.IsArray(hh, lset.head).getSingle match {
                          case Some(isArray) =>
                            if (isArray) {
                              var length = hh(lset.head)("length")._1._1._1._1.toString
                              if (length == "UInt")
                                length = "0"
                              finfo.storeObjectProperties(i, ("Array", List((length.toString, "Number")))) 
                            }
                            else {
                              // Compute prototype object
                              var cons: String = null
                              lset = obj("@proto")._1._1._1._2.toSet
                              val proto = 
                                if (lset.size == 0)
                                  ObjBot
                                else if (lset.size == 1)
                                  hh(lset.head)
                                else
                                  lset.tail.foldLeft(hh(lset.head))((o, l) => o + hh(l))
                              // Compute constructor object
                              lset = proto("constructor")._1._1._1._2.toSet 
                              val constructor = 
                                if (lset.size == 0)
                                  ObjBot
                                else if (lset.size == 1)
                                  hh(lset.head)
                                else
                                  lset.tail.foldLeft(hh(lset.head))((o, l) => o + hh(l))
                              // Get constructor name 
                              /*System.out.println("function: "+cfg.getFuncName(fid))
                              var print =  constructor("@construct")._1._3.toSet
                              if (print.size == 0)
                                System.out.println("constructor size is zero")
                              else if (print.size == 1)
                                System.out.println(cfg.getFuncName(print.head))
                              else
                                while (print.size != 0) {
                                  System.out.println(cfg.getFuncName(print.head))
                                  print = print.tail
                                }
                              */ 
                              cons = cfg.getFuncName(constructor("@construct")._1._3.toSet.head) 

                              // Compute object properties
                              var properties = obj.getProps.foldLeft[List[(String, String)]](List())((list, prop) => { 
                                  val p = obj(prop)._1._1._1
                                  val p_types = p.typeKinds
                                  (prop, p_types)::list
                              })
                              finfo.storeObjectProperties(i, (cons, properties))
                            }
                          case None =>
                        }
                      }
                    }
                    finfo.storeParameter(i, v_i_types)
                    finfo.setCandidate
                  }
                  i += 1
                  Helper.CreateMutableBinding(hh, x, v_i)
                })

                // Compute this object.
                var functionName = cfg.getFuncName(fid)
                if (functionName.contains("prototype")) {
                  if (functionName.contains("<"))
                    functionName = functionName.substring(0, functionName.indexOf("<"))
                  val token = functionName.split("prototype")
                  if (token.length > 2) throw new ConcolicError("Only a.prototype.x function form is supported") 
                  finfo.storeThisName(token(0).substring(0, token(0).length-1))

                  val thislset = state._1(SinglePureLocalLoc)("@this")._1._2._2.toSet
                  val thisObj = 
                    if (thislset.size == 0) 
                      ObjBot
                    else if (thislset.size == 1) 
                      h_n(thislset.head)
                    else
                      thislset.tail.foldLeft(h_n(thislset.head))((o, l) => o + h_n(l))

                  var properties = thisObj.getProps.foldLeft[List[(String, String)]](List())((list, prop) => { 
                      val p = thisObj(prop)._1._1._1
                      val p_types = p.typeKinds
                      (prop, p_types)::list
                  })
                  finfo.storeThisObject(properties)
                }
              }
              functions.put(cfg.getFuncName(fid), finfo)
            }
          }
        case None => 
      }
    }
  }
  def computePropertyList(state: State, lset: Set[Loc]): Option[List[(String, Absent)]]  = {
    if (state <= StateBot) 
      None
    else {
      val h = state._1
      val obj = 
        if (lset.size == 0)
          ObjBot
        else if (lset.size == 1)
          h(lset.head)
        else
          lset.tail.foldLeft(h(lset.head))((o, l) => o + h(l))
        val props = obj.getProps.foldLeft[List[(String, Absent)]](List())((list, p) => (p, obj(p)._2)::list)
      Some(props)
    }
  }

  /*def storeIR(id: IRId) = functions.get(id.getUniqueName) match { case Some(f) => f.storeIR(id); case None => } 

  def getIR(fun: String): IRId = functions.get(fun) match { case Some(f) => f.irId; case None => }*/
}
