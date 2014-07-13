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
import kr.ac.kaist.jsaf.nodes.ASTNode
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, JSAstToConcrete}
import kr.ac.kaist.jsaf.widl.WIDLTypeMap
import kr.ac.kaist.jsaf.ts.TSTypeMap

object BugHelper {
  ////////////////////////////////////////////////////////////////
  // Get function argument size
  ////////////////////////////////////////////////////////////////
  def getBuiltinArgumentSize(funcName: String): (Int, Int) = {
    // Built-in function
    argSizeMap.get(funcName) match {
      case Some(as) => return as
      case None =>
    }
    // WIDL function
    WIDLTypeMap.argSizeMap.get(funcName) match {
      case Some(as) => return as
      case None =>
    }
    // println("* Unknown argument size of \"" + funcName + "\".")
    (-1, -1)
  }

  ////////////////////////////////////////////////////////////////
  // Get function name
  ////////////////////////////////////////////////////////////////

  def getFuncName(funcName: String, varManager: VarManager = null, expr: CFGNode = null): String = {
    if (funcName.startsWith("<>arguments<>")) return "arguments"
    if (!NU.isFunExprName(funcName)) return funcName
    if (varManager != null && expr != null) {
      expr match {
        case expr: CFGExpr =>
          val bugVar0 = varManager.getUserVarAssign(expr)
          if (bugVar0 != null) return bugVar0.toString
        case expr: CFGFunExpr =>
          var isFirst = true
          val funcName = new StringBuilder
          for (rhs <- varManager.getUserVarAssignR(expr.lhs)) {
            if (isFirst) isFirst = false else funcName.append(", ")
            funcName.append(rhs.toString)
          }
          if (funcName.length > 0) return funcName.toString
        case _ =>
      }
    }
    "anonymous_function"
  }



  ////////////////////////////////////////////////////////////////
  // Get [[Function]] or [[Construct]] property
  ////////////////////////////////////////////////////////////////

  def getFuncOrConstPropName(heap: Heap, funLoc: Loc, isCall: Boolean): String = {
    // Function must have [[Function]] or [[Construct]] property
    if (isCall) {
      if (BoolTrue <= Helper.IsCallable(heap, funLoc)) return "@function"
    }
    else {
      if (BoolTrue <= Helper.HasConstruct(heap, funLoc)) return "@construct"
    }
    null
  }



  ////////////////////////////////////////////////////////////////
  // Get omitted code from a AST node
  ////////////////////////////////////////////////////////////////

  def getOmittedCode(ast: ASTNode, maxLength: Int): (String, Boolean) = getOmittedCode(JSAstToConcrete.doit(ast), maxLength)
  def getOmittedCode(code: String, maxLength: Int): (String, Boolean) = {
    var newCode = ""
    var isFirst = true
    for (line <- code.split('\n')) {
      val trimedLine = line.replace('\t', ' ').trim
      if (newCode.length < maxLength && trimedLine.length > 0) {
        if (isFirst) isFirst = false else newCode+= ' '
        newCode+= trimedLine
      }
    }
    if (newCode.length > maxLength) (newCode.substring(0, maxLength), true)
    else (newCode, false)
  }



  ////////////////////////////////////////////////////////////////
  // Convert property name from AbsString
  ////////////////////////////////////////////////////////////////

  def getPropName(name: AbsString): String =
    name.getSingle match {
      case Some(propName) => propName
      case _ => "unknown_property"
    }

  ////////////////////////////////////////////////////////////////
  // IsCallable for locations
  ////////////////////////////////////////////////////////////////

  def isCallable(heap: Heap, locSet: LocSet): AbsBool = {
    if(locSet.size == 0) return BoolFalse

    var isCallable: AbsBool = BoolBot
    for(loc <- locSet) isCallable+= (if(BoolFalse == Helper.IsCallable(heap, loc)) Helper.IsBound(heap, loc) 
                                     else Helper.IsCallable(heap, loc))
    isCallable
  }



  ////////////////////////////////////////////////////////////////
  // Get a set of property names (String) from an AbsString
  ////////////////////////////////////////////////////////////////

  def props(heap: Heap, loc: Loc, absString: AbsString): Set[String] = {
    if (!heap.domIn(loc)) Set()
    else {
      absString.gamma match {
        case Some(s) => s
        case _ => absString.getAbsCase match {
          // ignore @default
          case AbsTop => heap(loc).map.keySet.filter(s => !s.take(1).equals("@"))
          case AbsBot => Set()
          case _ if absString.isAllNums =>
            heap(loc).map.keySet.filter(s => !s.take(1).equals("@") && AbsString.alpha(s) <= NumStr)
          case _ => heap(loc).map.keySet.filter(s => !s.take(1).equals("@") && AbsString.alpha(s) <= OtherStr)
        }
      }
    }
  }



  ////////////////////////////////////////////////////////////////
  // PValue to String
  ////////////////////////////////////////////////////////////////

  def pvalueToString(pvalue: PValue, concreteOnly: Boolean = true): String = {
    var result = ""
    pvalue.foreach(absValue => {
      if (!absValue.isBottom && (!concreteOnly || absValue.isConcrete)) {
        if (result.length == 0) result+= absValue.toString
        else result+= ", " + absValue.toString
      }
    })
    result
  }

  ////////////////////////////////////////////////////////////////
  // 9.6 ToUint32: (Unsigned 32 Bit Integer)
  ////////////////////////////////////////////////////////////////
  def toUint32(n: Double): Long = {
    def modulo(x: Double, y: Long): Long = {
      val result = math.abs(x.toLong) % math.abs(y)
      if(math.signum(x) < 0) return math.signum(y) * (math.abs(y) - result)
      math.signum(y) * result
    }

    // 1. Let number be the result of calling ToNumber on the input argument.
    // 2. If number is NaN, +0, -0, +INF or -INF, return +0.
    if(n.isNaN || n == 0 || n.isInfinite) return 0
    // 3. Let posInt be sign(number) * floor(abs(number))
    val posInt = math.signum(n) * math.floor(math.abs(n))
    // 4. Let int32bit be posInt modulo 2^32; that is, ...
    val int32bit = modulo(posInt, 0x100000000L)
    // 5. Return int32bit.
    int32bit.toLong
  }
}
