/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.CallContext._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.nodes_util.{NodeRelation, Walkers}
import kr.ac.kaist.jsaf.scala_src.nodes._

class ASTDetect(bugDetector: BugDetector) {
  val ast                                       = bugDetector.ast
  val cfg                                       = bugDetector.cfg
  val bugStorage                                = bugDetector.bugStorage
  val stateManager                              = bugDetector.stateManager

  def check(): Unit = {
    object walker extends Walkers {
      override def walkAST(parent: Any, node: Any): Unit = {
        node match {
          // Check parsing arguments of "JSON.parse"
          case n@SFunApp(info, SDot(_, SVarRef(_, obj), mem), args)
            if obj.getText.equals("JSON") && mem.getText.equals("parse") && args.length > 0 =>
            NodeRelation.ast2cfgMap.get(n) match {
              case Some(cfgList) =>
                for (cfgInst <- cfgList) {
                  cfgInst match {
                    case inst@CFGCall(_, _, _, _, arguments, _, _) =>
                      val cfgNode = cfg.findEnclosingNode(inst)
                      val cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
                      for ((callContext, state) <- cstate) {
                        val argLocSet = SE.V(arguments, state.heap, state.context)._1.locset
                        for (argLoc <- argLocSet) {
                          val argObj = state.heap(argLoc)
                          if (argObj != null && argObj.map != null) {
                            // Check parsing the first argument of "JSON.parse"
                            val res = argObj.map.get("0")
                            if (res.isDefined) {
                              val absStr = res.get._1.objval.value.pvalue.strval
                              absStr.gamma match {
                                case Some(vs) if !absStr.isAllNums =>
                                  val isNotBug = vs.exists(s => Parser.parseJSON(s))
                                  if(!isNotBug) bugStorage.addMessage(info.getSpan, ParseJSON, inst, callContext, absStr.toString())
                                case _ =>
                              }
                            }
                          }
                        }
                      }
                    case _ =>
                  }
                }
              case None =>
            }

          // Check parsing arguments of "new Function"
          case n@SNew(info, SFunApp(_, SVarRef(_, id), args)) if id.getText.equals("Function") && args.length > 0 =>
            NodeRelation.ast2cfgMap.get(n) match {
              case Some(cfgList) =>
                for (cfgInst <- cfgList) {
                  cfgInst match {
                    case inst@CFGConstruct(_, _, _, _, arguments, _, _) =>
                      val cfgNode = cfg.findEnclosingNode(inst)
                      val cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
                      for ((callContext, state) <- cstate) {
                        val argLocSet = SE.V(arguments, state.heap, state.context)._1.locset
                        for (argLoc <- argLocSet) {
                          val argObj = state.heap(argLoc)
                          if (argObj != null && argObj.map != null) {
                            // Check parsing arguments except the last of "new Function"
                            args.zipWithIndex.dropRight(1).foreach(p => {
                              val res = argObj.map.get(p._2.toString)
                              if (res.isDefined) {
                                for(pvalue <- res.get._1.objval.value.pvalue) {
                                  if(pvalue.isConcrete) {
                                    var str = pvalue.toString
                                    if(str.startsWith("\"") && str.endsWith("\"")) str = str.substring(1, str.length-1)
                                    if(!Parser.parseFunctionParams(str))
                                      bugStorage.addMessage(info.getSpan, ParseFunctionParams, inst, callContext, str)
                                  }
                                }
                              }
                            })

                            // Check parsing the last argument of "new Function"
                            val res = argObj.map.get((args.length-1).toString)
                            if (res.isDefined) {
                              for(pvalue <- res.get._1.objval.value.pvalue) {
                                if(pvalue.isConcrete) {
                                  var str = pvalue.toString
                                  if(str.startsWith("\"") && str.endsWith("\"")) str = str.substring(1, str.length - 1)
                                  if(!Parser.parseFunctionBody("{"+str+"}"))
                                    bugStorage.addMessage(info.getSpan, ParseFunctionBody, inst, callContext, str)
                                }
                              }
                            }
                          }
                        }
                      }
                    case _ =>
                  }
                }
              case None =>
            }
          case _ =>
        }

        // Walk child nodes
        super.walkAST(parent, node)
      }
    }

    // Walk AST nodes to collect only strict mode code ASTs
    walker.walkAST(null, ast)
  }
}
