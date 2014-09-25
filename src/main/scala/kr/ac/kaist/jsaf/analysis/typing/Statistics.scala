/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinError
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class Statistics(cfg: CFG, builtins:Map[FunctionId, String], inTable: Table, locclone: Boolean) {
  val sem = new Semantics(cfg, Worklist.computes(cfg), locclone)
  val globalStat = new Stat(sem)
  
  def printDump() = {
    globalStat.printDump();
  }
  
  def printTable() = {
    globalStat.printTable();
  }

  def calculate() = {
    def C(node: Node, map: CState, c: Cmd, nodeStat: Stat): Unit = {
      /* for after-call */
      cfg.getReturnVar(node) match {
        case None => Unit
        case Some(id) => nodeStat.countVarUpdate(map, id)
      }
      
      c match {
        case Entry =>
          if (map.forall((kv) => kv._2._1 <= HeapBot)) {
            nodeStat.deadBB = nodeStat.deadBB + 1;
          }
        case Exit =>
          if (map.forall((kv) => kv._2._1 <= HeapBot)) {
            nodeStat.deadBB = nodeStat.deadBB + 1;
          }
        case ExitExc => Unit
        case Block(insts) => {
          if (map.forall((kv) => kv._2._1 <= HeapBot)) {
            nodeStat.deadBB = nodeStat.deadBB + 1;
          }
          
          insts.foldLeft[CState](map)((stateMap, inst) => {
            // count dead instruction
            val isDead = stateMap.exists(
              (kv) => (kv._2._1 <= HeapBot && !builtins.contains(node._1)))
            if (isDead)
                nodeStat.deadInst = nodeStat.deadInst + 1
            
            // compute other statistics
            I(node, inst, stateMap, nodeStat)
            
            // compute next normal states and exception state using semantics
            val (cs, es) = stateMap.foldLeft[(CState,State)]((HashMap(),StateBot))((ms, kv) => {
              val ((h, ctx), (he, ctxe)) = sem.I((node, kv._1), inst, kv._2.heap, kv._2.context, HeapBot, ContextBot, inTable)
              (ms._1 + (kv._1 -> State(h, ctx)), ms._2 + State(he, ctxe))
            })
            
            // count exception
            if (!(es._1 <= HeapBot)) {
              globalStat.countException(es._1(SinglePureLocalLoc)("@exception")._2)
            }
            
            // normal CState for next instruction
            cs
          })
          
          Unit
        }
      }
    }
    
    def I(node: Node, i: CFGInst, stateMap: CState, nodeStat:Stat) = {
        i match {
          case CFGAlloc(_, _ , x, e, a_new) => {
            nodeStat.countVarUpdate(stateMap, x)
            e match {
              case None => () 
              case Some(expr) => {
                V(expr, stateMap, nodeStat)
                nodeStat.countDeref(stateMap, expr, "Alloc")
              }
            }
          }
          case CFGAllocArray(_, _, x, n, a_new) => {
            nodeStat.countVarUpdate(stateMap, x)
          }
          case CFGAllocArg(_, _, x, n, a_new) => {
            nodeStat.countVarUpdate(stateMap, x)
          }
          case CFGExprStmt(_, _,x, expr) => {
            V(expr, stateMap, nodeStat);
            nodeStat.countVarUpdate(stateMap, x)
          }
          case CFGDelete(_, _, lhs, expr) => {
            V(expr, stateMap, nodeStat);
            nodeStat.countVarUpdate(stateMap, lhs)
          }
          case CFGDeleteProp(_, _, lhs, obj, index) => {
            V(obj, stateMap, nodeStat);
            V(index, stateMap, nodeStat);
            nodeStat.countVarUpdate(stateMap, lhs)
            nodeStat.countDeref(stateMap, obj, "Delete")
            nodeStat.countAccess(stateMap, index, "Delete")
          }
          case CFGStore(_, _, obj, index, rhs) => {
            V(obj, stateMap, nodeStat);
            V(index, stateMap, nodeStat);
            V(rhs, stateMap, nodeStat);
            nodeStat.countDeref(stateMap, obj, "Store")
            nodeStat.countAccess(stateMap, index, "Store")
            nodeStat.countPropUpdate(stateMap, obj)
          }
          case CFGFunExpr(_, _, lhs, name, fid, a_new1, a_new2, a_new3) => {
            nodeStat.countVarUpdate(stateMap, lhs)
          }
          case CFGConstruct(_, _, cons, base, arguments, a_new, b_new) => {
            V(cons, stateMap, nodeStat)
            V(base, stateMap, nodeStat)
            V(arguments, stateMap, nodeStat)
            nodeStat.countDeref(stateMap, cons, "New(cons)")
            nodeStat.countDeref(stateMap, base, "New(base)")
            nodeStat.countDefNew(stateMap, cons)
          }
          case CFGCall(_, _, fun, base, arguments, a_new, b_new) => {
            V(fun, stateMap, nodeStat)
            V(base, stateMap, nodeStat)
            V(arguments, stateMap, nodeStat)
            nodeStat.countDeref(stateMap, fun, "Call(fun)")
            nodeStat.countDeref(stateMap, base, "Call(base)")
            nodeStat.countDefCall(stateMap, fun)
          }
          case CFGCatch(_, _, name) => {
            nodeStat.countVarUpdate(stateMap, name)
          }
          case CFGReturn(_, _, expr) => {
            expr match {
              case None => Unit
              case Some(e) => V(e, stateMap, nodeStat)
            }
          }
          case CFGAssert(_, _, expr, flag) => {
            if(flag) V(expr, stateMap, nodeStat)
          }
          case CFGThrow(_, _, expr) => {
            V(expr, stateMap, nodeStat)
          }
          case CFGInternalCall(_, _, lhs, fun, arguments, loc) => {
            nodeStat.countVarUpdate(stateMap, lhs)
            (fun.toString, arguments, loc)  match {
              case ("<>Global<>toObject", List(expr), Some(a_new)) => { 
                V(expr, stateMap, nodeStat)
                nodeStat.countType(stateMap, expr, "Object")
              }
              case ("<>Global<>isObject", List(expr), None) => {
                V(expr, stateMap, nodeStat)
                stateMap.foreach((kv)=> {
                  if (!(kv._2._1 <= HeapBot)) {
                    val v = SE.V(expr, kv._2._1, kv._2._2)._1
                  }})
              }
//              case ("<>Global<>toString", List(expr), None) => {
//                V(expr, stateMap, nodeStat)
//                nodeStat.countType(stateMap, expr, "String")
//              }
              case ("<>Global<>toNumber", List(expr), None) => {
                V(expr, stateMap, nodeStat)
                nodeStat.countType(stateMap, expr, "Number")
              }
              case ("<>Global<>toBoolean", List(expr), None) => {
                V(expr, stateMap, nodeStat)
                nodeStat.countType(stateMap, expr, "Bool")
              }
              case ("<>Global<>getBase", List(expr), None) => {
                V(expr, stateMap, nodeStat)
              }
              case ("<>Global<>iteratorInit", List(expr), None) => {
                V(expr, stateMap, nodeStat)
              }
              case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) => {
                V(expr_2, stateMap, nodeStat)
                V(expr_3, stateMap, nodeStat)
              }
              case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) => {
                V(expr_2, stateMap, nodeStat)
                V(expr_3, stateMap, nodeStat)
              }
              case _ => ()
            }
        }
        case _ => ()
      }
    }
    
    def V(e: CFGExpr, stateMap: CState, nodeStat:Stat): Unit= {
      e match {
        case CFGBin(info, first, op, second) => {
          V(first, stateMap, nodeStat)
          V(second, stateMap, nodeStat)
          op.getText match {
            case "instanceof" => {
              nodeStat.countDeref(stateMap, first, "ins(obj)")
              nodeStat.countDeref(stateMap, second, "ins(cons)")
            }
            case "in" => {
              nodeStat.countDeref(stateMap, second, "in")
            }
            case _ => Unit
          }
        }
        case CFGUn(info, op, expr) => {
          V(expr, stateMap, nodeStat)
        }
        case CFGLoad(info, obj, index) => {
          V(obj, stateMap, nodeStat)
          V(index, stateMap, nodeStat)
          nodeStat.countDeref(stateMap, obj, "Load")
          nodeStat.countAccess(stateMap, index, "Load")
        }
        case CFGThis(info) => {
          nodeStat.countDeref(stateMap, e, "this")
        }
        case _ => Unit
      }
    }
    
    globalStat.totalBB = cfg.getNodes.size
    cfg.getNodes.foreach((node) => {
      inTable.get(node) match {
        case None => {
          globalStat.deadBB = globalStat.deadBB + 1;
          cfg.getCmd(node) match {
            case Block(insts) =>{
              if (!builtins.contains(node._1))
                globalStat.deadInst = globalStat.deadInst + insts.length;
            }
            case _ => Unit
          }
        }
        case Some(map) =>
          C(node, map, cfg.getCmd(node), globalStat)
      }
    })
  }
}


class Stat(sem: Semantics) {
  /* dereference type */
  type DerefStat = (Int, Int, Int, Int) 
  // _1 => total deref. (node)
  // _2 => total deref. node point (node)
  // _3 => maximum deref. (cc)
  // _4 => num. of definite deref (cc)
  
  /* dereference: total */
  var deref: DerefStat = (0,0,0,0)
  /* dereference: Load */
  var derefLoad: DerefStat = (0,0,0,0)
  /* dereference: Store */
  var derefStore: DerefStat = (0,0,0,0)
  /* dereference: DeleteProp */
  var derefDel: DerefStat = (0,0,0,0)
  /* dereference: Call Function, Base */
  var derefCallFun: DerefStat = (0,0,0,0)
  var derefCallBase: DerefStat = (0,0,0,0)
  /* dereference: New Constructor, Base */
  var derefNewCons: DerefStat = (0,0,0,0)
  var derefNewBase: DerefStat = (0,0,0,0)
  /* dereference: Alloc */
  var derefAlloc: DerefStat = (0,0,0,0)
  /* dereference: instanceof Object, Constructor*/
  var derefInsObj: DerefStat = (0,0,0,0)
  var derefInsCons: DerefStat = (0,0,0,0)
  /* dereference: in */
  var derefIn: DerefStat = (0,0,0,0)
  /* dereference: this */
  var derefThis: DerefStat = (0,0,0,0)
  
  /* definite function call/new */
  var callPoint = 0;
  var definiteFun = 0; 
  var newPoint = 0;
  var definiteCons = 0;
  
  /* type */
  type TypeStat = (Int, Int, Int, Int, Int) 
  // _1 => total type (node)
  // _2 => num. of undef (node)
  // _3 => total type conversion point (node)
  // _4 => maximum type (cc)
  // _5 => num. of definite type (cc)
  var typ: TypeStat = (0,0,0,0,0);
  var typBool: TypeStat = (0,0,0,0,0);
  var typNum: TypeStat = (0,0,0,0,0);
  var typStr: TypeStat = (0,0,0,0,0);
  var typObj: TypeStat = (0,0,0,0,0);
  
  /* access type */
  type AccessStat = (Int, Int)
  // _1 => fixed string access
  // _2 => access point
  var access: AccessStat = (0,0);
  var accessLoad: AccessStat = (0,0);
  var accessStore: AccessStat = (0,0);
  var accessDel: AccessStat = (0,0);
  
  /* strong update */
  var updatePoint = 0;
  var updateStrong = 0;
  
  /* dead code */
  var totalBB = 0;
  var deadBB = 0;
  var deadInst = 0;
  
  /* exception */
  var totalEx = 0;
  var errEx = 0;
  var evalEx = 0;
  var rangeEx = 0;
  var refEx = 0;
  var syntaxEx = 0;
  var typeEx = 0;
  var uriEx = 0;
  
  
  def printTable() = {
    val derefAvg = 
      if (deref._2 > 0)  (deref._1 * 1.0d)/ deref._2
      else  0.0
    val derefLoadAvg = 
      if (derefLoad._2 > 0)  (derefLoad._1 * 1.0d)/ derefLoad._2
      else  0.0
    val derefStoreAvg = 
      if (derefStore._2 > 0)  (derefStore._1 * 1.0d)/ derefStore._2
      else  0.0
    val derefDelAvg = 
      if (derefDel._2 > 0)  (derefDel._1 * 1.0d)/ derefDel._2
      else  0.0
    val derefCallFunAvg = 
      if (derefCallFun._2 > 0)  (derefCallFun._1 * 1.0d)/ derefCallFun._2
      else  0.0
    val derefCallBaseAvg = 
      if (derefCallBase._2 > 0)  (derefCallBase._1 * 1.0d)/ derefCallBase._2
      else  0.0
    val derefNewConsAvg = 
      if (derefNewCons._2 > 0)  (derefNewCons._1 * 1.0d)/ derefNewCons._2
      else  0.0
    val derefNewBaseAvg = 
      if (derefNewBase._2 > 0)  (derefNewBase._1 * 1.0d)/ derefNewBase._2
      else  0.0
    val derefAllocAvg = 
      if (derefAlloc._2 > 0)  (derefAlloc._1 * 1.0d)/ derefAlloc._2
      else  0.0
    val derefInsObjAvg = 
      if (derefInsObj._2 > 0)  (derefInsObj._1 * 1.0d)/ derefInsObj._2
      else  0.0
    val derefInsConsAvg = 
      if (derefInsCons._2 > 0)  (derefInsCons._1 * 1.0d)/ derefInsCons._2
      else  0.0
    val derefInAvg = 
      if (derefIn._2 > 0)  (derefIn._1 * 1.0d)/ derefIn._2
      else  0.0
    val derefThisAvg = 
      if (derefThis._2 > 0)  (derefThis._1 * 1.0d)/ derefThis._2
      else  0.0
      
    val derefTable =  
"""================================= Dereference =================================
| dereference point(#)    : %8d                                          |
| average dereference(#)  : %8.2f                                          |
| maximum dereference(#)  : %8d                                          |
| definite deref. ratio(%%): %8.2f                                          |
|-----------+---Load---+---Store--+--Delete--+---Alloc--+---Call(fun, base)---|
|point   (#)| %8d | %8d | %8d | %8d | %8d | %8d |
|average (#)| %8.2f | %8.2f | %8.2f | %8.2f | %8.2f | %8.2f |
|maximum (#)| %8d | %8d | %8d | %8d | %8d | %8d |
|definite(%%)| %8.2f | %8.2f | %8.2f | %8.2f | %8.2f | %8.2f |
|-----------+---New(cons, base)---+-Instanceof(obj,con)-+----In----+---this---|
|point   (#)| %8d | %8d | %8d | %8d | %8d | %8d |
|average (#)| %8.2f | %8.2f | %8.2f | %8.2f | %8.2f | %8.2f |
|maximum (#)| %8d | %8d | %8d | %8d | %8d | %8d |
|definite(%%)| %8.2f | %8.2f | %8.2f | %8.2f | %8.2f | %8.2f |
===============================================================================
""".format(
        deref._2, derefAvg, deref._3, (deref._4 * 1.0d) / deref._2 * 100,
        derefLoad._2, derefStore._2, derefDel._2, derefAlloc._2, derefCallFun._2, derefCallBase._2,
        derefLoadAvg, derefStoreAvg, derefDelAvg, derefAllocAvg, derefCallFunAvg, derefCallBaseAvg,
        derefLoad._3, derefStore._3, derefDel._3, derefAlloc._3, derefCallFun._3, derefCallBase._3,
        
        (derefLoad._4 * 1.0d)    / derefLoad._2    * 100, (derefStore._4 * 1.0d)    / derefStore._2    * 100,
        (derefDel._4 * 1.0d)     / derefDel._2     * 100, (derefAlloc._4 * 1.0d)    / derefAlloc._2    * 100,
        (derefCallFun._4 * 1.0d) / derefCallFun._2 * 100, (derefCallBase._4 * 1.0d) / derefCallBase._2 * 100,
        
        derefNewCons._2, derefNewBase._2, derefInsObj._2, derefInsCons._2, derefIn._2, derefThis._2,
        derefNewConsAvg, derefNewBaseAvg, derefInsObjAvg, derefInsConsAvg, derefInAvg, derefThisAvg,
        derefNewCons._3, derefNewBase._3, derefInsObj._3, derefInsCons._3, derefIn._3, derefThis._3,
        
        (derefNewCons._4 * 1.0d) / derefNewCons._2 * 100, (derefNewBase._4 * 1.0d) / derefNewBase._2 * 100,
        (derefInsObj._4 * 1.0d)  / derefInsObj._2  * 100, (derefInsCons._4 * 1.0d) / derefInsCons._2 * 100,
        (derefIn._4 * 1.0d)      / derefIn._2      * 100, (derefThis._4 * 1.0d)    / derefThis._2    * 100)
    
    /* non-function */
    val nfunTable = 
"""=============== Definite-Function ===============
| definite call ratio(%%)            : %8.2f  |
| definite function call ratio(%%)   : %8.2f  |
| definite constructor call raito(%%): %8.2f  |
=================================================
""".format(((definiteFun + definiteCons)* 1.0d)/ (callPoint + newPoint) * 100,
          (definiteFun * 1.0d)/ callPoint * 100, (definiteCons * 1.0d)/ newPoint * 100)
      
    /* type */
    val typAvg = 
      if (typ._3 > 0)  (typ._1 * 1.0d)/ typ._3
      else  0.0
    val typBoolAvg = 
      if (typBool._3 > 0)  (typBool._1 * 1.0d)/ typBool._3
      else  0.0
    val typNumAvg = 
      if (typNum._3 > 0)  (typNum._1 * 1.0d)/ typNum._3
      else  0.0
    val typStrAvg = 
      if (typStr._3 > 0)  (typStr._1 * 1.0d)/ typStr._3
      else  0.0
    val typObjAvg = 
      if (typObj._3 > 0)  (typObj._1 * 1.0d)/ typObj._3
      else  0.0
      
    val typeTable =
"""========================= Type ==========================
| type conversion point(#): %8d                    |
| average type size(#)    : %8.2f                    |
| maximum type size(#)    : %8d                    |
| definite type ratio(%%)  : %8.2f                    |
| has undefinted ratio(%%) : %8.2f                    |
|-----------+--toBool--+---toNum--+---toStr--+---toObj--|
|point   (#)| %8d | %8d | %8d | %8d |
|average (#)| %8.2f | %8.2f | %8.2f | %8.2f |
|maximum (#)| %8d | %8d | %8d | %8d |
|definite(%%)| %8.2f | %8.2f | %8.2f | %8.2f |
|hasundef(%%)| %8.2f | %8.2f | %8.2f | %8.2f |
=========================================================
""".format(
        typ._3, typAvg, typ._4, (typ._5 * 1.0d) / typ._3 * 100, (typ._2 * 1.0d) / typ._3 * 100,
        typBool._3, typNum._3, typStr._3, typObj._3,
        typBoolAvg, typNumAvg, typStrAvg, typObjAvg,
        typBool._4, typNum._4, typStr._4, typObj._4,
        
        (typBool._5 * 1.0d) / typBool._3 * 100, (typNum._5 * 1.0d) / typNum._3 * 100,
        (typStr._5 * 1.0d)  / typStr._3  * 100, (typObj._5 * 1.0d) / typObj._3 * 100,
        
        (typBool._2 * 1.0d) / typBool._3 * 100, (typNum._2 * 1.0d) / typNum._3 * 100,
        (typStr._2 * 1.0d)  / typStr._3  * 100, (typObj._2 * 1.0d) / typObj._3 * 100)
    

    val acsTable =
"""============== Property Access ===============
| property access point(#): %8d         |
| constant access ratio(%%): %8.2f         |
|-----------+---Load---+---Store--+--Delete--|
|point   (#)| %8d | %8d | %8d |
|constant(%%)| %8.2f | %8.2f | %8.2f |
==============================================
""".format(
        access._2, (access._1 * 1.0d)/ access._2 * 100,
        accessLoad._2, accessStore._2, accessDel._2,
        (accessLoad._1 * 1.0d)/ accessLoad._2 * 100,
        (accessStore._1 * 1.0d)/ accessStore._2 * 100,
        (accessDel._1 * 1.0d)/ accessDel._2 * 100)
    
    val updateTable = 
"""============ Strong update ============
| update point(#)       : %8d    |
| strong update ratio(%%): %8.2f    |
=======================================
""".format(updatePoint, (updateStrong * 1.0d)/ updatePoint * 100)
    
    val deadTable = 
"""============ Dead instructions ============
| active basic block(#)      : %8d   |
| dead instructions(#)       : %8d   |
===========================================
""".format(totalBB - deadBB, deadInst)

    val exTable =
"""============ Exception ============
| Total exception(#) : %8d   |
| Error(#)           : %8d   |
| Eval Error(#)      : %8d   |
| Range Error(#)     : %8d   |
| Reference Error(#) : %8d   |
| Syntax Error(#)    : %8d   |
| Type Error(#)      : %8d   |
| URI Error(#)       : %8d   |
===================================
""".format(totalEx, errEx, evalEx, rangeEx, refEx, syntaxEx, typeEx, uriEx)


    print(derefTable + nfunTable + typeTable + acsTable + updateTable + deadTable + exTable)
  }
  def printDump() = {
    /* dereference */
    val derefAvg = 
      if (deref._2 > 0)  (deref._1 * 1.0d)/ deref._2
      else  0.0
    val derefLoadAvg = 
      if (derefLoad._2 > 0)  (derefLoad._1 * 1.0d)/ derefLoad._2
      else  0.0
    val derefStoreAvg = 
      if (derefStore._2 > 0)  (derefStore._1 * 1.0d)/ derefStore._2
      else  0.0
    val derefDelAvg = 
      if (derefDel._2 > 0)  (derefDel._1 * 1.0d)/ derefDel._2
      else  0.0
    val derefCallFunAvg = 
      if (derefCallFun._2 > 0)  (derefCallFun._1 * 1.0d)/ derefCallFun._2
      else  0.0
    val derefCallBaseAvg = 
      if (derefCallBase._2 > 0)  (derefCallBase._1 * 1.0d)/ derefCallBase._2
      else  0.0
    val derefNewConsAvg = 
      if (derefNewCons._2 > 0)  (derefNewCons._1 * 1.0d)/ derefNewCons._2
      else  0.0
    val derefNewBaseAvg = 
      if (derefNewBase._2 > 0)  (derefNewBase._1 * 1.0d)/ derefNewBase._2
      else  0.0
    val derefAllocAvg = 
      if (derefAlloc._2 > 0)  (derefAlloc._1 * 1.0d)/ derefAlloc._2
      else  0.0
    val derefInsObjAvg = 
      if (derefInsObj._2 > 0)  (derefInsObj._1 * 1.0d)/ derefInsObj._2
      else  0.0
    val derefInsConsAvg = 
      if (derefInsCons._2 > 0)  (derefInsCons._1 * 1.0d)/ derefInsCons._2
      else  0.0
    val derefInAvg = 
      if (derefIn._2 > 0)  (derefIn._1 * 1.0d)/ derefIn._2
      else  0.0
    val derefThisAvg = 
      if (derefThis._2 > 0)  (derefThis._1 * 1.0d)/ derefThis._2
      else  0.0
      
    val derefDump = 
"""============ Dereference ============
# dereference point(#): %d
# average dereference(#): %f
# maximum dereference(#): %d
# definite deref. ratio(%%): %f
-- Load --
# lo dereference point(#)    : %d
# lo average dereference(#)  : %f
# lo maximum dereference(#)  : %d
# lo definite deref. ratio(%%): %f
-- Store --
# st dereference point(#)    : %d
# st average dereference(#)  : %f
# st maximum dereference(#)  : %d
# st definite deref. ratio(%%): %f
-- DeleteProp --
# de dereference point(#)    : %d
# de average dereference(#)  : %f
# de maximum dereference(#)  : %d
# de definite deref. ratio(%%): %f
-- Call(function) --
# cf dereference point(#)    : %d
# cf average dereference(#)  : %f
# cf maximum dereference(#)  : %d
# cf definite deref. ratio(%%): %f
-- Call(base) --
# cb dereference point(#)    : %d
# cb average dereference(#)  : %f
# cb maximum dereference(#)  : %d
# cb definite deref. ratio(%%): %f
-- New(constructor) --
# nc dereference point(#)    : %d
# nc average dereference(#)  : %f
# nc maximum dereference(#)  : %d
# nc definite deref. ratio(%%): %f
-- New(base) --
# nb dereference point(#)    : %d
# nb average dereference(#)  : %f
# nb maximum dereference(#)  : %d
# nb definite deref. ratio(%%): %f
-- Alloc --
# al dereference point(#)    : %d
# al average dereference(#)  : %f
# al maximum dereference(#)  : %d
# al definite deref. ratio(%%): %f
 -- instanceof(object) --
# io dereference point(#)    : %d
# io average dereference(#)  : %f
# io maximum dereference(#)  : %d
# io definite deref. ratio(%%): %f
 -- instanceof(constructor) --
# ic dereference point(#)    : %d
# ic average dereference(#)  : %f
# ic maximum dereference(#)  : %d
# ic definite deref. ratio(%%): %f
 -- in --
# in dereference point(#)    : %d
# in average dereference(#)  : %f
# in maximum dereference(#)  : %d
# in definite deref. ratio(%%): %f
 -- this --
# th dereference point(#)    : %d
# th average dereference(#)  : %f
# th maximum dereference(#)  : %d
# th definite deref. ratio(%%): %f
""".format(
      deref._2,         derefAvg,         deref._3,         (deref._4 * 1.0d)         / deref._2         * 100,
      derefLoad._2,     derefLoadAvg,     derefLoad._3,     (derefLoad._4 * 1.0d)     / derefLoad._2     * 100,
      derefStore._2,    derefStoreAvg,    derefStore._3,    (derefStore._4 * 1.0d)    / derefStore._2    * 100,
      derefDel._2,      derefDelAvg,      derefDel._3,      (derefDel._4 * 1.0d)      / derefDel._2      * 100,
      derefCallFun._2,  derefCallFunAvg,  derefCallFun._3,  (derefCallFun._4 * 1.0d)  / derefCallFun._2  * 100,
      derefCallBase._2, derefCallBaseAvg, derefCallBase._3, (derefCallBase._4 * 1.0d) / derefCallBase._2 * 100,
      derefNewCons._2,  derefNewConsAvg,  derefNewCons._3,  (derefNewCons._4 * 1.0d)  / derefNewCons._2  * 100,
      derefNewBase._2,  derefNewBaseAvg,  derefNewBase._3,  (derefNewBase._4 * 1.0d)  / derefNewBase._2  * 100,
      derefAlloc._2,    derefAllocAvg,    derefAlloc._3,    (derefAlloc._4 * 1.0d)    / derefAlloc._2    * 100,
      derefInsObj._2,   derefInsObjAvg,   derefInsObj._3,   (derefInsObj._4 * 1.0d)   / derefInsObj._2   * 100,
      derefInsCons._2,  derefInsConsAvg,  derefInsCons._3,  (derefInsCons._4 * 1.0d)  / derefInsCons._2  * 100,
      derefIn._2,       derefInAvg,       derefIn._3,       (derefIn._4 * 1.0d)       / derefIn._2       * 100,
      derefThis._2,     derefThisAvg,     derefThis._3,     (derefThis._4 * 1.0d)     / derefThis._2     * 100)
    
    val nfunDump = 
"""
=============== Definite-Function ===============
# definite call ratio(%%): %f      
# definite function call ratio(%%): %f
# definite constructor call raito(%%): %f
""".format(((definiteFun + definiteCons)* 1.0d)/ (callPoint + newPoint) * 100,
    (definiteFun * 1.0d)/ callPoint * 100, (definiteCons * 1.0d)/ newPoint * 100)
      
    /* type */
    val typAvg = 
      if (typ._3 > 0)  (typ._1 * 1.0d)/ typ._3
      else  0.0
    val typBoolAvg = 
      if (typBool._3 > 0)  (typBool._1 * 1.0d)/ typBool._3
      else  0.0
    val typNumAvg = 
      if (typNum._3 > 0)  (typNum._1 * 1.0d)/ typNum._3
      else  0.0
    val typStrAvg = 
      if (typStr._3 > 0)  (typStr._1 * 1.0d)/ typStr._3
      else  0.0
    val typObjAvg = 
      if (typObj._3 > 0)  (typObj._1 * 1.0d)/ typObj._3
      else  0.0
    val typeDump = 
"""============ Type ============
# type conversion point(#): %d
# average type size(#): %f
# maximum type size(#): %d
# definite type ratio(%%): %f
# has undefined ratio(%%): %f
-- toBoolean --
# type conversion point(#) : %d
# average type size(#) : %f
# maximum type size(#) : %d
# definite type ratio(%%) : %f
# has undefinted ratio(%%) : %f
-- toNumber --
# type conversion point(#) : %d
# average type size(#) : %f
# maximum type size(#) : %d
# definite type ratio(%%) : %f
# has undefinted ratio(%%) : %f
-- toString --
# type conversion point(#) : %d
# average type size(#) : %f
# maximum type size(#) : %d
# definite type ratio(%%) : %f
# has undefinted ratio(%%) : %f
-- toObject --
# type conversion point(#) : %d
# average type size(#) : %f
# maximum type size(#) : %d
# definite type ratio(%%) : %f
# has undefinted ratio(%%) : %f
""".format(
      typ._3, typAvg, typ._4, (typ._5 * 1.0d) / typ._3 * 100, (typ._2 * 1.0d)/ typ._3 * 100,
      typBool._3, typBoolAvg, typBool._4, (typBool._5 * 1.0d) / typBool._3 * 100, (typBool._2 * 1.0d)/ typBool._3 * 100,
      typNum._3, typNumAvg, typNum._4, (typNum._5 * 1.0d) / typNum._3 * 100, (typNum._2 * 1.0d)/ typNum._3 * 100,
      typStr._3, typStrAvg, typStr._4, (typStr._5 * 1.0d) / typStr._3 * 100, (typStr._2 * 1.0d)/ typStr._3 * 100,
      typObj._3, typObjAvg, typObj._4, (typObj._5 * 1.0d) / typObj._3 * 100, (typObj._2 * 1.0d)/ typObj._3 * 100)

      
    /* property access */
    val acsDump =
"""============ Property Access ============
# property access point(#): %d
# constant access ratio(%%): %f
-- Load --
# lo property access point(#): %d 
# lo constant access ratio(%%): %f
-- Store --
# st property access point(#): %d
# st constant access ratio(%%): %f
-- Delete Prop --
# de property access point(#): %d
# de constant access ratio(%%): %f
""".format(
        access._2,      (access._1 * 1.0d)/ access._2 * 100,
        accessLoad._2,  (accessLoad._1 * 1.0d)/ accessLoad._2 * 100,
        accessStore._2, (accessStore._1 * 1.0d)/ accessStore._2 * 100,
        accessDel._2,   (accessDel._1 * 1.0d)/ accessDel._2 * 100)
    
    /* strong update */
    val updateDump =
"""============ Strong update ============
# update point(#): %d
# strong update ratio(%%): %f
""".format(updatePoint, (updateStrong * 1.0d)/ updatePoint * 100)
    
    /* dead inst. */
    val deadDump =
"""============ Dead instructions ============
# active basic block(#): %d
# dead instructions(#): %d
""".format(totalBB - deadBB, deadInst)
    
    val exDump =
"""============ Exception ============
# Total exception(#): %d
# Error(#): %d
# Eval Error(#): %d
# Range Error(#): %d
# Reference Error(#): %d
# Syntax Error(#): %d
# Type Error(#): %d
# URI Error(#): %d
""".format(totalEx, errEx, evalEx, rangeEx, refEx, syntaxEx, typeEx, uriEx)

    print(derefDump + nfunDump + typeDump + acsDump + updateDump + deadDump + exDump)
  }
  
  def countDeref(cstate: CState, expr: CFGExpr, what: String): Unit = {
    val drf = what match {
      case "Load" =>       derefLoad
      case "Store" =>      derefStore
      case "Delete" =>     derefDel
      case "Call(fun)" =>  derefCallFun
      case "Call(base)" => derefCallBase
      case "New(cons)" =>  derefNewCons
      case "New(base)" =>  derefNewBase
      case "Alloc" =>      derefAlloc
      case "ins(obj)" =>   derefInsObj
      case "ins(cons)" =>  derefInsCons
      case "in" =>         derefIn
      case "this" =>       derefThis
    }
    
    val (v_join, count) = cstate.foldLeft((ValueBot,0))((curr, kv) => {
      if (kv._2._1 <= HeapBot) curr 
      else {
        val v = SE.V(expr, kv._2._1, kv._2._2)._1
        (v + curr._1, chooseMax(locSize(kv._2._1, v._2), curr._2))
      }
    })
    val mcount = locSize(cstate.foldLeft(StateBot)((s, kv)=> s + kv._2)._1, v_join._2)

    /* total deref */
    val maxGlobal = chooseMax(count, deref._3)
    val definiteGlobal = if (count <=  1) deref._4 + 1 else deref._4
    deref = (deref._1 + mcount, deref._2 + 1, maxGlobal, definiteGlobal)
    
    /* selected deref */
    val max = chooseMax(count, drf._3)
    val definite = if (count <= 1) drf._4 + 1 else drf._4
    val new_drf = (drf._1 + mcount, drf._2 + 1, max, definite)
    what match {
      case "Load" =>       derefLoad     = new_drf
      case "Store" =>      derefStore    = new_drf
      case "Delete" =>     derefDel      = new_drf
      case "Call(fun)" =>  derefCallFun  = new_drf
      case "Call(base)" => derefCallBase = new_drf
      case "New(cons)" =>  derefNewCons  = new_drf
      case "New(base)" =>  derefNewBase  = new_drf
      case "Alloc" =>      derefAlloc    = new_drf
      case "ins(obj)" =>   derefInsObj   = new_drf
      case "ins(cons)" =>  derefInsCons  = new_drf
      case "in" =>         derefIn       = new_drf
      case "this" =>       derefThis     = new_drf
    }
  }
  
  def countType(cstate: CState, expr: CFGExpr, what: String): Unit = {
    val ttyp = 
      what match {
        case "Bool" =>   typBool
        case "Number" => typNum
        case "String" => typStr
        case "Object" => typObj
      }

    val (v_join, count) = cstate.foldLeft((ValueBot,0))((curr, kv) => {
      if (kv._2._1 <= HeapBot) curr 
      else {
        val v = SE.V(expr, kv._2._1, kv._2._2)._1
        (v + curr._1, chooseMax(v.typeCount, curr._2))
      }
    })
    val mcount = v_join.typeCount

    /* type total */
    val undefTotal = if (mcount > 1 && UndefTop <= v_join._1._1) typ._2 + 1 else typ._2
    val maxTotal = chooseMax(count, typ._4) 
    val definiteTotal = if (count <= 1) typ._5 + 1 else typ._5
    typ = (typ._1 + mcount, undefTotal, typ._3 + 1, maxTotal, definiteTotal)
    
    /* selected type */
    val undef = if (mcount > 1 && UndefTop <= v_join._1._1) ttyp._2 + 1 else ttyp._2
    val max = chooseMax(count, ttyp._4)
    val definite = if (count <= 1) ttyp._5 + 1 else ttyp._5
    val new_ttyp = (ttyp._1 + mcount, undef, ttyp._3 + 1, max, definite)
    what match {
      case "Bool" =>   typBool = new_ttyp
      case "Number" => typNum  = new_ttyp
      case "String" => typStr  = new_ttyp
      case "Object" => typObj  = new_ttyp
    }
  }
 
  def countAccess(cstate: CState, expr: CFGExpr, what: String): Unit = {
    expr match {
      case CFGString(_) => Unit
      case _ => {
        val acs = what match {
          case "Load" => accessLoad
          case "Store" => accessStore
          case "Delete" => accessDel
        }
        
        val non_single = cstate.exists(kv => {
          val s = kv._2
          if (s._1 <= HeapBot) false 
          else {
            val (v, es) = SE.V(expr, s._1, s._2)
            val str = Helper.toString(Helper.toPrimitive_better(s._1, v))
            str.getAbsCase match {
              case AbsTop | AbsMulti => true 
              case _ => false
            }
          }
        })
        
        /* total access */
        val countTotal = if (non_single) access._1 else access._1 + 1
        val pointTotal = access._2 + 1;
        access = (countTotal, pointTotal)
        
        /* selected access */
        val count = if (non_single) acs._1 else acs._1 + 1 
        val point = acs._2 + 1;
        val new_acs = (count, point)
        what match {
          case "Load" =>   accessLoad  = new_acs
          case "Store" =>  accessStore = new_acs
          case "Delete" => accessDel   = new_acs
        }
      }
    }
  }

  def countVarUpdate(cstate: CState, x: CFGId) = {
    x match {
      case CFGUserId(_,_,_,_,_) => {
        updatePoint = updatePoint + 1
        val weak = cstate.exists(kv => {
          val s = kv._2
          if (s._1 <= HeapBot) false 
          else {
            val lset_base = Helper.LookupBase(s._1, x)
            locSize(s._1, lset_base) match {
              case 0 => false
              case 1 => if (isOldLoc(lset_base.head)) true else false
              case _ => true
            }
          }
        })
        if (!weak) updateStrong = updateStrong + 1
      }
      // case for x is temporal variable.
      case CFGTempId(_,_) => ()
    }
  }

  def countPropUpdate(cstate: CState, obj: CFGExpr) = {
    updatePoint = updatePoint + 1;
    val weak = cstate.exists(kv => {
      val s = kv._2
      if (s._1 <= HeapBot) false 
      else {
        val lset_obj = SE.V(obj, s._1, s._2)._1._2
        locSize(s._1, lset_obj) match {
          case 0 => false
          case 1 => if (isOldLoc(lset_obj.head)) true else false
          case _ => true
        }
      }      
    })
    if (!weak) updateStrong = updateStrong + 1
  }

  def countDefCall(cstate: CState, expr: CFGExpr) = {
    callPoint = callPoint + 1
    val non_definite = cstate.exists(kv => {
      val s = kv._2
      if (s._1 <= HeapBot) false 
      else {
        val lset = SE.V(expr, s._1, s._2)._1._2
        lset.exists(l => BoolFalse <= Helper.IsCallable(s._1, l))
      }      
    })
    if (!non_definite) definiteFun = definiteFun + 1
  }

  def countDefNew(cstate: CState, expr: CFGExpr) = {
    newPoint = newPoint + 1
    val non_definite = cstate.exists(kv => {
      val s = kv._2
      if (s._1 <= HeapBot) false 
      else {
        val lset = SE.V(expr, s._1, s._2)._1._2
        lset.exists(l => BoolFalse <= Helper.HasConstruct(s._1, l))
      }      
    })
    if (!non_definite) definiteCons = definiteCons + 1
  }
  
  def countException(v_ex :Value) = {
    var exist = false
    v_ex._2.foreach((l) => {
      l match {
        case BuiltinError.ErrLoc => errEx = errEx+ 1; exist = true
        case BuiltinError.EvalErrLoc => evalEx = evalEx+ 1; exist = true
        case BuiltinError.RangeErrLoc => rangeEx = rangeEx+ 1; exist = true
        case BuiltinError.RefErrLoc => refEx = refEx+ 1; exist = true
        case BuiltinError.SyntaxErrLoc => syntaxEx = syntaxEx+ 1; exist = true
        case BuiltinError.TypeErrLoc => typeEx = typeEx+ 1; exist = true
        case BuiltinError.URIErrLoc => uriEx = uriEx+ 1; exist = true
        case _ => ()
      }})
    if (exist) totalEx = totalEx + 1
  }
  
  private def chooseMax(x: Int, y: Int) = if (x > y) x else y
  
  private def locSize(h: Heap, locs: LocSet): Int = {
    locs.filter((l: Loc) => h(l) </ Obj.bottom).size
  }
}
