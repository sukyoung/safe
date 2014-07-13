/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.mutable.{HashSet => MHashSet}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.ModelManager
import kr.ac.kaist.jsaf.nodes_util.JSAstToConcrete
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.ShellParameters

class FinalDetect(bugDetector: BugDetector) {
  val cfg           = bugDetector.cfg
  val typing        = bugDetector.typing
  val callGraph     = bugDetector.callGraph
  val shadowings    = bugDetector.shadowings
  val semantics     = bugDetector.semantics
  val bugStorage    = bugDetector.bugStorage
  val bugOption     = bugDetector.bugOption
  val varManager    = bugDetector.varManager
  val libMode       = bugDetector.libMode
  val locclone      = bugDetector.locclone
  val analyzeMode   = bugDetector.analyzeMode
  val env           = bugDetector.env
  val quiet         = bugDetector.quietFlag
  val dtv           = bugDetector.params.command == ShellParameters.CMD_DTV_APP



  ////////////////////////////////////////////////////////////////
  // Bug Detection Main (final check)
  ////////////////////////////////////////////////////////////////

  def check(): Unit = {
    if (!dtv) {
      callConstFuncCheck
      conditionalBranchCheck
      shadowingCheck
    }
    if (!dtv && !bugDetector.params.opt_jQuery) {
      unreachableCodeCheck
      unusedFunctionCheck
      unusedVarPropCheck
    }
    varyingTypeArgumentsCheck
  }



  ////////////////////////////////////////////////////////////////
  // CallConstFunc Check 
  ////////////////////////////////////////////////////////////////

  private def callConstFuncCheck(): Unit = {
    // callMap   : set of fids used as a function
    // constMap  : set of fids used as a constructor
    val callMap = new MHashMap[FunctionId, CFGExpr]
    val constMap = new MHashMap[FunctionId, CFGExpr]
    for ((inst, fidSet) <- callGraph) {
      inst match {
        case CFGCall(_, _, constExpr, _, _, _) => fidSet.foreach(fid => callMap.put(fid, constExpr))
        case CFGConstruct(_, _, funExpr, _, _, _) => fidSet.foreach(fid => constMap.put(fid, funExpr))
      }
    }
    val commonFidSet = callMap.keySet & constMap.keySet
    commonFidSet.foreach((fid) => bugStorage.addMessage(cfg.getFuncInfo(fid).getSpan, CallConstFunc, null, null, BugHelper.getFuncName(cfg.getFuncName(fid), varManager, callMap(fid))))
  }



  ////////////////////////////////////////////////////////////////
  //  ConditionalBranch Check
  ////////////////////////////////////////////////////////////////

  def conditionalBranchCheck(): Unit = {
    // Insert left CFGAsserts
    bugDetector.traverseInsts((node, inst) => {
      inst match {
        case inst: CFGAssert => bugStorage.insertConditionMap(node, inst, BoolBot, false)
        case _ =>
      }
    })

    for ((astStmt, resultSet) <- bugStorage.conditionMap) {
      val condExpr = astStmt match {
        case SCond(_, condExpr, _, _) => condExpr
        case SIf (_, condExpr, _, _) => condExpr
        case _ => null
      }
      if (condExpr != null) {
        //System.out.println(astStmt + ", (" + JSAstToConcrete.doit(condExpr) + ")")
        for (((node, assert), result) <- resultSet) {
          // Get a CFGAssert instruction in node
          def getAssertInst(node: Node): CFGAssert = {
            cfg.getCmd(node) match {
              case Block(insts) if (insts.length > 0 && insts.head.isInstanceOf[CFGAssert]) =>
                val assert = insts.head.asInstanceOf[CFGAssert]
                if (bugStorage.getASTNodeFromCFGAssert(assert) == astStmt) assert else null
              case _ => null
            }
          }

          // Check whether the node is leaf or not
          val level1Succ = new MHashSet[Node] ++ cfg.getAllSucc(node)
          val level2Succ = new MHashSet[Node]
          for (node <- level1Succ) level2Succ++= (cfg.getAllSucc(node))
          val isLeaf = !(level1Succ ++ level2Succ).exists(succNode => getAssertInst(succNode) != null)
          //System.out.println(assert + " = " + result + ", isLeaf = " + isLeaf)
          //System.out.println(node + "'s level1Succ = " + level1Succ)
          //System.out.println(node + "'s level2Succ = " + (level2Succ -- level1Succ))

          if (isLeaf) {
            var rootAssertInst: CFGAssert = null
            def followUp(node: Node, level: Int): Unit = {
              val assertInst = getAssertInst(node)
              if (assertInst == null) {
                val predNodes = cfg.getAllPred(node)
                if (level == 1 && predNodes.size > 0) for (predNode <- predNodes) followUp(predNode, 2)
                else bugStorage.addMessage(condExpr.getInfo.getSpan, CondBranch, rootAssertInst, null, JSAstToConcrete.doit(condExpr), rootAssertInst.flag.toString)
                return
              }
              else rootAssertInst = assertInst
              //System.out.println("  > node = " + node + ", " + assertInst + " = " + resultSet.get((node, assertInst)))

              resultSet.get((node, assertInst)) match {
                case Some(result) => if (result != BoolTrue) return
                case None => return
              }

              for (predNode <- cfg.getAllPred(node)) followUp(predNode, 1)
            }
            followUp(node, 1)
          }
        }
      }
    }
  }



  ////////////////////////////////////////////////////////////////
  // Shadowing Check
  ////////////////////////////////////////////////////////////////

  private def shadowingCheck(): Unit = shadowings.foreach((bug) => bugStorage.addMessage(bug.span, bug.bugKind, null, null, bug.arg1, bug.arg2))



  ////////////////////////////////////////////////////////////////
  // UnreachableCode Check
  ////////////////////////////////////////////////////////////////

  private def unreachableCodeCheck(): Unit = {
    val astList = bugStorage.getUnreachableASTList
    var (prevSpan, prevCode): (Span, String) = (null, null)
    for (ast <- astList) {
      // Span, Code
      val span = ast.getInfo.getSpan
      val code = BugHelper.getOmittedCode(ast)

      // Debug
      //println(span + " : " + code)

      if(prevSpan == null || prevCode == null) {prevSpan = span; prevCode = code}
      else if(equals(prevSpan, span)) {if(prevCode.length < code.length) {prevSpan = span; prevCode = code}}
      else if(contains(prevSpan, span)) Unit
      else if(contains(span, prevSpan)) {prevSpan = span; prevCode = code}
      else if(overlaps(prevSpan, span)) {prevSpan = new Span(prevSpan.begin, span.end)}
      else if(overlaps(span, prevSpan)) {prevSpan = new Span(span.begin, prevSpan.end); prevCode = code}
      // else TODO: continuous ASTs!
      else {report; prevSpan = span; prevCode = code}
    }
    report

    def report(): Unit = {
      if(prevSpan == null || prevCode == null) return
      bugStorage.addMessage(prevSpan, UnreachableCode, null, null, prevCode)
      prevSpan = null; prevCode = null
    }
    def equals(a: Span, b: Span): Boolean = a.getBegin == b.getBegin && a.getEnd == b.getEnd
    def contains(a: Span, b: Span): Boolean = {
      val (aS, aE, bS, bE) = (a.getBegin, a.getEnd, b.getBegin, b.getEnd)
      val (aSL, aSC, aEL, aEC) = (aS.getLine, aS.column, aE.getLine, aE.column) // Start(Line, Column) ~ End(Line ,Column)
      val (bSL, bSC, bEL, bEC) = (bS.getLine, bS.column, bE.getLine, bE.column) // Start(Line, Column) ~ End(Line ,Column)
      (aSL < bSL || aSL == bSL && aSC <= bSC) && (aEL > bEL || aEL == bEL && aEC >= bEC) // a contains b
    }
    def overlaps(a: Span, b: Span): Boolean = {
      val (aS, aE, bS) = (a.getBegin, a.getEnd, b.getBegin)
      val (aSL, aSC, aEL, aEC) = (aS.getLine, aS.column, aE.getLine, aE.column) // Start(Line, Column) ~ End(Line ,Column)
      val (bSL, bSC) = (bS.getLine, bS.column) // Start(Line, Column)
      (aSL < bSL || aSL == bSL && aSC <= bSC) && (aEL > bSL || aEL == bSL && aEC >= bSC) // a ... [b ... a] ...  b
    }
  }



  ////////////////////////////////////////////////////////////////
  // UnusedFunction Check
  ////////////////////////////////////////////////////////////////

  private def unusedFunctionCheck(): Unit = {
    val fidSet = cfg.getFunctionIds.filterNot(fid => typing.builtinFset.contains(fid))
    for (fid <- bugStorage.filterUsedFunctions(fidSet.toList)) {
      if (typing.getStateAtFunctionEntry(fid).isEmpty) {
        val funExpr: CFGFunExpr = bugStorage.getFunExpr(fid)
        val bugKind = if (libMode) UnreferencedFunction else UncalledFunction
        bugStorage.addMessage(cfg.getFuncInfo(fid).getSpan, bugKind, null, null, BugHelper.getFuncName(cfg.getFuncName(fid), varManager, funExpr))
        bugStorage.appendUnusedFunction(fid)
      }
    }

    /* Previous code
    val funcCount = cfg.getFuncCount
    val fidSet    = (0 until funcCount toSet) filterNot (typing.builtinFset contains)

    for (fid <- bugStorage.filterUsedFunctions(fidSet toList)) {
      if (typing.getStateAtFunctionEntry(fid).isEmpty) {
        bugStorage.addMessage(cfg.getFuncInfo(fid).getSpan, (if (libMode) UnreferencedFunction else UncalledFunction), null, null, getFuncName(cfg.getFuncName(fid)))
        bugStorage.appendUnusedFunction(fid)
      }
    }
    */
  }



  ////////////////////////////////////////////////////////////////
  // UnusedVarProp Check
  ////////////////////////////////////////////////////////////////

  private def unusedVarPropCheck(): Unit = {
    var worklist: List[Node] = List()
    var fixpoint: RWMap = Map()
    var count = 0

    worklist = getSuccs((cfg.getGlobalFId, LEntry)) 
    while (!worklist.isEmpty) {
      count = count + 1
      val curr = worklist.head
      worklist = worklist.tail
      val curStat = getStat(curr)
      val newStat = findUnread(curStat, curr)
      getSuccs(curr).foreach((node) => {
        if (newStat != getStat(node)) {
          if (!(worklist contains node)) worklist = worklist :+ node
          addStat(node, newStat)
        }
      })
    }
    getStat((cfg.getGlobalFId, LExit)).foreach((e) => if (e._1) bugStorage.unreadReport(e._5, e._2, e._4, e._3))
    
    def addStat(node: Node, list: List[RWEntry]) = fixpoint.get(node) match {
      case Some(entries) => fixpoint += (node -> (entries intersect list))
      case None => fixpoint += (node -> list)
    }
    def getStat(node: Node): List[RWEntry] = fixpoint.get(node) match {
      case Some(entry) => entry
      case None => List()
    }

    def findUnread(curStat: List[RWEntry], current: Node): List[RWEntry] = {
      val currRW: List[RWEntry]  = bugStorage.getRWEntry(current)
      if (currRW.isEmpty) return curStat

      var rwSeq: List[RWEntry]   = curStat ++ currRW
      var retStat: List[RWEntry] = List()
      for (rw <- rwSeq) {
        rwSeq = rwSeq.tail
        rwSeq.find((e) => (e._2 == rw._2) && (e._3 == rw._3) && (e._4 == rw._4)) match {
          case Some(matched) => (matched._1, rw._1) match {
            case (true, true) => bugStorage.unreadReport(rw._5, rw._2, rw._4, rw._3)
            case _ => // pass
          }
          case None => if (rw._1) retStat = retStat :+ rw
        }
      }
      retStat
    }

    def getSuccs(current: Node): List[Node] = {
      typing.getStateAfterNode(current).keys.foldLeft[List[Node]](cfg.getSucc(current) toList)((list, callContext) =>
        semantics.getIPSucc((current, callContext)) match {
          case Some(ccMap) => list ++ ccMap.keys.map(e => e._1)
          case None => list
    })}
  }

/*
    // version 2 Using existing Worklist
    println("succMap := " + semantics.ipSuccMap)
    var fixpoint: RWMap = Map()
    var count = 0
    val worklist: Worklist = analyzeMode match {
      case 17 => Worklist.computesSparse(env.asInstanceOf[SparseEnv].getInterDDG, quiet)
      case 20 => Worklist.computesSparse(env.asInstanceOf[GSparseEnv].getInterDDG, quiet)
      case 19 | 21 | 25 => Worklist.computesSparse(env.asInstanceOf[DSparseEnv].getInterDDG, quiet)
      case _ => Worklist.computes(cfg, quiet)
    }

    cfg.getSucc((cfg.getGlobalFId, LEntry)).foreach((node) => worklist.add((node, CallContext.globalCallContext)))
    while (!worklist.isEmpty) {
      System.out.print("\r  Unused Iteration: "+count+"   ")
      worklist.dump()
      count = count + 1

      val cp = worklist.getHead
      println("current := " + cp + "\tcommands := " + cfg.getCmd(cp._1))

      val curStat = getStat(cp._1)
      println("cur state :"); printer(curStat)
      val newStat = findUnread(curStat, cp._1)
      println("new state :"); printer(newStat)

      val succs = cfg.getSucc(cp._1)
      val esucc = cfg.getExcSucc.get(cp._1)
      
      //println("succ := " + succs)
      succs.foreach(node => if (newStat != getStat(node)) {
        worklist.add((node, cp._2))
        addStat(node, newStat)
      })

      esucc match {
        case Some(node) =>
          if (newStat != getStat(node)) {
            worklist.add((node, cp._2))
            addStat(node, newStat)
          }
        case None => ()
      } 

      //println("ipsucc := " + semantics.getIPSucc(cp))
      semantics.getIPSucc(cp) match {
        case None => ()
        case Some(succMap) => 
          succMap.foreach(kv => {
          // bypassing if IP edge is exception flow.
          val cp_succ = kv._1
          if (newStat != getStat(cp_succ._1)) {
            worklist.add(cp_succ)
            addStat(cp_succ._1, newStat)
          }
        })
      }
      if (!worklist.isEmpty) {
        print("\tleft := ")
        print(worklist.head._2._1)
        worklist.tail.foreach(e => print(", " + e._2._1))
      }
      println()
    }
*/

/*
      // version 1
      retStat = rwList.foldLeft(List())((unread, rw) => list.find((e) => (e._2 == rw._2) && (e._3 == rw._3) && (e._4 == rw._4)) match {
        case Some(entry) => (entry._1, rw._1) match {
          case (true, true) => bugStorage.unreadReport(entry._5, entry._2, entry._4, entry._3)
          case _ => // pass 
        }; list.filterNot(_ == entry) :+ rw
        case None => list :+ rw
      })
      retStat  

    def findUnread(curStat: List[RWEntry], current: Node): List[RWEntry] = {
      val currRW = bugStorage.getRWEntry(current)
      val retStat = currRW.foldLeft[List[RWEntry]](curStat)((list, rw) => list.find((e) => (e._2 == rw._2) && (e._3 == rw._3) && (e._4 == rw._4)) match {
        case Some(entry) => (entry._1, rw._1) match {
          case (true, true) => bugStorage.unreadReport(entry._5, entry._2, entry._4, entry._3)
          case _ => // pass 
        }; list.filterNot(_ == entry) :+ rw
        case None => list :+ rw
      })
      retStat  
    }

    def getSuccs(current: Node): List[Node] = {
      typing.getStateAfterNode(current).keys.foldLeft[List[Node]](cfg.getSucc(current) toList)((list, callContext) =>
        semantics.getIPSucc((current, callContext)) match {
          case Some(ccMap) => ccMap.keys.foldLeft(list)((l, k) => l :+ k._1)
          case None => list
    })}
*/




  ////////////////////////////////////////////////////////////////
  // VaryingTypeArguments Check 
  ////////////////////////////////////////////////////////////////

  private def varyingTypeArgumentsCheck(): Unit = {
    for ((fid, (funExpr, argObjSet, span)) <- bugStorage.detectedFuncMap) {
      // function name, expected maximum argument length
      val funcName = cfg.getFuncName(fid)
      val funcArgList = cfg.getArgVars(fid)
      val (funcArgLen, isBuiltinFunc) = ModelManager.getFIdMap("Builtin").get(fid) match {
        case Some(builtinFuncName) => (argSizeMap(builtinFuncName)._2, true)
        case None => (funcArgList.length, false)
      }
      //println("- " + funcName + "(" + funcArgLen + ") #" + fid)

      // maximum argument length
      var maxArgObjLength = 0
      val argObjLengthMap = new MHashMap[Obj, Int]()
      for (argObj <- argObjSet) {
        val (propValue, _) = argObj("length")
        propValue.objval.value.pvalue.numval.getConcreteValue match {
          case Some(lengthPropDouble) =>
            val lengthPropInt = lengthPropDouble.toInt
            if (maxArgObjLength < lengthPropInt) maxArgObjLength = lengthPropInt
            argObjLengthMap.put(argObj, lengthPropInt)
          case None => // TODO?
        }
      }
      //println("  maximum argument length = " + maxArgObjLength)

      //println("funcName = " + funcName)
      //println("funcArgList.length = " + funcArgList.length)
      //println("maxArgObjLength = " + maxArgObjLength)
      // for each argument index 0, 1, 2, ...
      for (i <- 0 until maxArgObjLength) {
        var joinedValue: Value = ValueBot
        for ((argObj, argObjLength) <- argObjLengthMap) {
          if (argObjLength <= maxArgObjLength) {
            val (propValue, _) = argObj(i.toString)
            joinedValue+= propValue.objval.value
            //println("  argObj[" + i + "] = " + propValue.objval.value)
          }
        }
        var joinedValueTypeCount = joinedValue.pvalue.typeCount
        if (!bugOption.VaryingTypeArguments_CheckUndefined && joinedValueTypeCount > 1 && joinedValue.pvalue.undefval != UndefBot) joinedValueTypeCount-= 1
        val isBug: Boolean = (joinedValueTypeCount > 1 || joinedValueTypeCount == 1 && joinedValue.locset.size > 0)
        if (isBug) {
          var typeKinds: String = joinedValue.typeKinds
          if (isBuiltinFunc || i >= funcArgList.length) {
            // Built-in function
            val ordinal = i + 1 match {
              case 1 => "1st"
              case 2 => "2nd"
              case _ => i + "th"
            }
            bugStorage.addMessage(span, VaryingTypeArguments, null, null, BugHelper.getFuncName(funcName, varManager, funExpr), ordinal + " ", "", typeKinds)
          }
          else {
            // User function
            val argName = funcArgList(i) match {
              case CFGUserId(_, _, _, originalName, _) => originalName
              case CFGTempId(text, _) => text
            }
            bugStorage.addMessage(span, VaryingTypeArguments, null, null, BugHelper.getFuncName(funcName, varManager, funExpr), "", "'" + argName + "' ", typeKinds)
          }
          //println("    joined argObj[" + i + "] = " + joinedValue + ", isBug = " + isBug)
        }
      }
    }
    /* Previous code
    bugStorage.applyToDetectedFuncMap((fid: FunctionId, obj: Obj, spanSet: Set[Span]) => {
      val arglen = cfg.getArgVars(fid).size
      val isBug = (0 until arglen).foldLeft(false)((b, i) => b || (1 != obj(i.toString)._1._1._1.typeCount))
      if (isBug) bugStorage.addMessage(cfg.getFuncInfo(fid).getSpan, VaryingTypeArguments, null, null, getFuncName(cfg.getFuncName(fid)))
    })
    */
  }
}
