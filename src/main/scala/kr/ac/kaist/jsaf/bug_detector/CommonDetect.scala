/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}

class CommonDetect(bugDetector: BugDetector) {
  val cfg           = bugDetector.cfg
  val typing        = bugDetector.typing
  val bugStorage    = bugDetector.bugStorage
  val bugOption     = bugDetector.bugOption
  val varManager    = bugDetector.varManager
  val stateManager  = bugDetector.stateManager



  ////////////////////////////////////////////////////////////////
  // ConvertToNumber Check
  ////////////////////////////////////////////////////////////////

  def convertToNumberCheck(node: Node, inst: CFGInst, expr1: CFGExpr, expr2: CFGExpr, doToPrimitive: Boolean, conditionFunction: (PValue, PValue) => Boolean): Unit = {
    if(!bugOption.ConvertUndefToNum_Check) return

    // Get spans
    val expr1Span = expr1.getInfo match {
      case Some(info) => info.getSpan
      case None => inst.getInfo.get.getSpan
    }
    val expr2Span = if (expr2 == null) null else expr2.getInfo match {
      case Some(info) => info.getSpan
      case None => inst.getInfo.get.getSpan
    }

    // Get variable names
    val varId1: String = varManager.getUserVarAssign(expr1) match {
      case bv: BugVar0 if (bv.toString != "undefined") => " '" + bv.toString + "' can be undefined."
      case _ => ""
    }
    val varId2: String = {
      if (expr2 != null) {
        varManager.getUserVarAssign(expr2) match {
          case bv: BugVar0 if (bv.toString != "undefined") => " '" + bv.toString + "' can be undefined."
          case _ => ""
        }
      }
      else null
    }

    // Check for each CState
    val bugCheckInstance = new BugCheckInstance()
    val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(ConvertUndefToNum))
    for ((callContext, state) <- mergedCState) {
      // For expr1
      val value1: Value = SE.V(expr1, state.heap, state.context)._1
      val pvalue1: PValue = if (doToPrimitive) Helper.toPrimitive_better(state.heap, value1) else value1.pvalue

      // For expr2 (this can be null)
      val value2: Value = if (expr2 != null) SE.V(expr2, state.heap, state.context)._1 else null
      val pvalue2: PValue = if (expr2 != null) {if (doToPrimitive) Helper.toPrimitive_better(state.heap, value2) else value2.pvalue} else null

      if (conditionFunction == null || conditionFunction(pvalue1, pvalue2)) {
        if (!bugOption.ConvertUndefToNum_VariableMustHaveUndefinedOnly || pvalue1.typeCount == 1 && value1.locset.isEmpty) {
          // Check possibility of being undefined
          val checkInstance = bugCheckInstance.insert(pvalue1.undefval == UndefTop, expr1Span, callContext, state)
          checkInstance.pValue = pvalue1
          checkInstance.string1 = varId1
        }
        if (expr2 != null) {
          if (!bugOption.ConvertUndefToNum_VariableMustHaveUndefinedOnly || pvalue2.typeCount == 1 && value2.locset.isEmpty) {
            // Check possibility of being undefined
            val checkInstance = bugCheckInstance.insert(pvalue2.undefval == UndefTop, expr2Span, callContext, state)
            checkInstance.pValue = pvalue2
            checkInstance.string1 = varId2
          }
        }
      }
    }

    // Filter out bugs depending on options
    if (!bugOption.ConvertUndefToNum_UndefMustBeConvertedInEveryState) {
      bugCheckInstance.filter((bug, notBug) => (bug.pValue == notBug.pValue))
    }

    // Report bugs
    for (checkInstance <- bugCheckInstance.bugList) bugStorage.addMessage(checkInstance.span, ConvertUndefToNum, inst, checkInstance.callContext, checkInstance.string1)
  }



  ////////////////////////////////////////////////////////////////
  //  DefaultValue + ImplicitTypeConversion Check
  ////////////////////////////////////////////////////////////////

  /*
  def defaultValueCheck(inst: CFGInst, expr: CFGExpr, hint: String): Unit = {
    val node = cfg.findEnclosingNode(inst)
    val bugCheckInstance = new BugCheckInstance()
    val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(DefaultValueTypeError))

    for ((callContext, state) <- mergedCState) {
      val heap      = state.heap
      val context   = state.context
      val exprVal   = SE.V(expr, heap, context)._1
      val exprPVal  = exprVal.pvalue
      val exprLoc   = exprVal.locset
      val exprInfo  = expr.getInfo
      val name      = varManager.getUserVarAssign(expr) match {
        case name: BugVar0 => "'" + name.toString + "'"
        case _ => "an object"
      }
      var isBuiltin  = false
      val checkOrder = hint match {
        case "String" => Array("toString", "valueOf")
        case "Number" => Array("valueOf", "toString")
      }

      // To check ImplicitTypeConversion in built-in functions
      for(loc <- exprLoc) {
        if(!isBuiltin) isBuiltin = heap(loc)("@function")._1.funid.exists(fid => typing.builtinFset contains fid)
      }

      // Bug Detect
      if(exprLoc.exists(loc => {
        checkOrder.exists(funName => {
          val funValue = heap(loc)(funName)._1.objval.value
          //println("Loc = " + loc + ", funName = " + funName + ", funValue = " + funValue)
          if(funValue.locset.isEmpty) false
          else {
            if (isBuiltin && implicitTypeConversionCheck(funValue, funName)) true
            else if (defaultValueTypeErrorCheck(funValue)) true
            else false
          }
        })
      })) infoCheck(DefaultValueTypeError)

      ////////////////////////////////////////////////////////////////
      // DefaultValueTypeError Check
      ////////////////////////////////////////////////////////////////

      def defaultValueTypeErrorCheck(value: Value): Boolean = {
        /*for(loc <- value.locset) {
          println("    loc = " + loc + ", ObjectName = " + kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter.printLoc(loc) + ", isCallable = " + Helper.IsCallable(heap, loc))
          for(fid <- heap(loc)("@function")._1.funid) {
            println("        fid = " + fid + ", function name = " + ModelManager.getFuncName(fid))
          }
        }*/
        value.locset.exists(loc =>
          Helper.IsCallable(heap, loc).getPair match {
            case (AbsSingle, Some(b)) => b
            case _ => false // Maybe
          }
        )
      }

      ////////////////////////////////////////////////////////////////
      // ImplicitTypeConversion Check
      ////////////////////////////////////////////////////////////////

      def implicitTypeConversionCheck(value: Value, hint: String): Boolean = {
        value.locset.exists(loc =>
          heap(loc)("@function")._1.funid.exists(fid =>
            typing.builtinFset.get(fid) match {
              case Some(builtinName) => !internalMethodMap(hint).contains(builtinName) && infoCheck(ImplicitCallToString)
              case None => infoCheck(ImplicitCallValueOf)
            }
          )
        )
      }

      ////////////////////////////////////////////////////////////////
      // Report bug if info exists
      ////////////////////////////////////////////////////////////////

      def infoCheck(flag: BugKind): Boolean = {
        exprInfo match {
          case Some(info) => bugStorage.addMessage(info.getSpan, flag, null, null, name)
          case None => System.out.println("bugDetector, Bug '%d'. Expression has no info.".format(flag))
        }
        exprInfo.isDefined
      }
    }
  }
  */
}
