/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector.util

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.SpanInfo
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._

object Util {
  var bDebugPrint: Boolean = false
  def DebugPrint(string: String) = if(bDebugPrint) System.out.println(string)

  def name2id(node:Any):Int = node match {
    case _:AnonymousFnName =>
      DebugPrint("SAnonymousFnName")
      0
    case _:ArrayExpr =>
      DebugPrint("SArrayExpr")
      1
    case _:AssignOpApp =>
      DebugPrint("SAssignOpApp")
      2
    case _:Block =>
      DebugPrint("SBlock")
      3
    case _:Bool =>
      DebugPrint("SBool")
      4
    case _:Bracket =>
      DebugPrint("SBracket")
      5
    case _:Break =>
      DebugPrint("SBreak")
      6
    case _:Case =>
      DebugPrint("SCase")
      7
    case _:Catch =>
      DebugPrint("SCatch")
      8
    case _:Cond =>
      DebugPrint("SCond")
      9
    case _:Continue =>
      DebugPrint("SContinue")
      10
    case _:Debugger =>
      DebugPrint("SDebugger")
      11
    case _:DoWhile =>
      DebugPrint("SDoWhile")
      12
    case _:Dot =>
      DebugPrint("SDot")
      13
    case _:DoubleLiteral =>
      DebugPrint("SDoubleLiteral")
      14  
    case _:EmptyStmt =>
      DebugPrint("SEmptyStmt")
      15
    case _:ExprList =>
      DebugPrint("SExprList")
      16
    case _:ExprStmt =>
      DebugPrint("SExprStmt")
      17
    case _:Field =>
      DebugPrint("SField")
      18
    case _:For =>
      DebugPrint("SFor")
      19
    case _:ForIn =>
      DebugPrint("SForIn")
      20
    case _:ForVar =>
      DebugPrint("SForVar")
      21
    case _:ForVarIn =>
      DebugPrint("SForVarIn")
      22
    case _:FunApp =>
      DebugPrint("SFunApp")
      23
    case _:FunDecl =>
      DebugPrint("SFunDecl")
      24
    case _:FunExpr =>
      DebugPrint("SFunExpr")
      25
    case _:Functional =>
      DebugPrint("SFunctional")
      26  
    case _:GetProp =>
      DebugPrint("SGetProp")
      27
    case _:Id =>
      DebugPrint("SId")
      28
    case _:If =>
      DebugPrint("SIf")
      29
    case _:InfixOpApp =>
      DebugPrint("SInfixOpApp")
      30
    case _:IntLiteral =>
      DebugPrint("SIntLiteral")
      31
    case _:Label =>
      DebugPrint("SLabel")
      32
    case _:LabelStmt =>
      DebugPrint("SLabelStmt")
      33
    case _:New =>
      DebugPrint("SNew")
      34
    case _:Null =>
      DebugPrint("SNull")
      35
    case _:ObjectExpr =>
      DebugPrint("SObjectExpr")
      36
    case _:Op =>
      DebugPrint("SOp")
      37
    case _:Parenthesized =>
      DebugPrint("SParenthesized")
      38
    case _:PrefixOpApp =>
      DebugPrint("SPrefixOpApp")
      39
    case _:Program =>
      DebugPrint("SProgram")
      40
    case _:PropId =>
      DebugPrint("SPropId")
      41
    case _:PropNum =>
      DebugPrint("SPropNum")
      42
    case _:PropStr =>
      DebugPrint("SPropStr")
      43
    case _:RegularExpression =>
      DebugPrint("SRegularExpression")
      44
    case _:Return =>
      DebugPrint("SReturn")
      45
    case _:SetProp =>
      DebugPrint("SSetProp")
      46
    case _:SpanInfo =>
      DebugPrint("SpanInfo")
      47
    case SStringLiteral(_, _, txt) =>
      val str = NU.unescapeJava(txt)
      DebugPrint("SStringLiteral \"" + str + "\"")
      48
    case _:Switch =>
      DebugPrint("SSwitch")
      49
    case _:This =>
      DebugPrint("SThis")
      50
    case _:Throw =>
      DebugPrint("SThrow")
      51
    case _:TopLevel =>
      DebugPrint("STopLevel")
      52  
    case _:Try =>
      DebugPrint("STry")
      53
    case _:UnaryAssignOpApp =>
      DebugPrint("SUnaryAssignOpApp")
      54
    case _:VarDecl =>
      DebugPrint("SVarDecl")
      55
    case _:VarRef =>
      DebugPrint("SVarRef")
      56
    case _:VarStmt =>
      DebugPrint("SVarStmt")
      57
    case _:While =>
      DebugPrint("SWhile")
      58
    case _:With =>
      DebugPrint("SWith")
      59
    case xs:List[_] => 60
    case xs:Option[_] => 61
    case _ => 62
  }

  def isRelevant(node:Any):Boolean = node match {
    case _:AnonymousFnName =>
      DebugPrint("SAnonymousFnName")
      true
    case _:ArrayExpr =>
      DebugPrint("SArrayExpr")
      true
    case _:AssignOpApp =>
      DebugPrint("SAssignOpApp")
      true
    case _:Block =>
      DebugPrint("SBlock")
      true
    case _:Bool =>
      DebugPrint("SBool")
      true
    case _:Bracket =>
      DebugPrint("SBracket")
      false
    case _:Break =>
      DebugPrint("SBreak")
      true
    case _:Case =>
      DebugPrint("SCase")
      true
    case _:Catch =>
      DebugPrint("SCatch")
      true
    case _:Cond =>
      DebugPrint("SCond")
      true
    case _:Continue =>
      DebugPrint("SContinue")
      true
    case _:Debugger =>
      DebugPrint("SDebugger")
      false
    case _:DoWhile =>
      DebugPrint("SDoWhile")
      true
    case _:Dot =>
      DebugPrint("SDot")
      true
    case _:EmptyStmt =>
      DebugPrint("SEmptyStmt")
      false
    case _:ExprList =>
      DebugPrint("SExprList")
      true
    case _:ExprStmt =>
      DebugPrint("SExprStmt")
      true
    case _:Field =>
      DebugPrint("SField")
      true
    case _:DoubleLiteral =>
      DebugPrint("SDoubleLiteral")
      true
    case _:For =>
      DebugPrint("SFor")
      true
    case _:ForIn =>
      DebugPrint("SForIn")
      true
    case _:ForVar =>
      DebugPrint("SForVar")
      true
    case _:ForVarIn =>
      DebugPrint("SForVarIn")
      true
    case _:FunApp =>
      DebugPrint("SFunApp")
      true
    case _:FunDecl =>
      DebugPrint("SFunDecl")
      true
    case _:FunExpr =>
      DebugPrint("SFunExpr")
      true
    case _:Functional =>
      DebugPrint("SFunctional")
      true
    case _:GetProp =>
      DebugPrint("SGetProp")
      true
    case SId(_, text, _, _) =>
      DebugPrint("SId \"" + text + "\"")
      true
    case _:If =>
      DebugPrint("SIf")
      true
    case _:InfixOpApp =>
      DebugPrint("SInfixOpApp")
      true
    case _:IntLiteral =>
      DebugPrint("SIntLiteral")
      true
    case _:Label =>
      DebugPrint("SLabel")
      true
    case _:LabelStmt =>
      DebugPrint("SLabelStmt")
      true
    case _:New =>
      DebugPrint("SNew")
      true
    case _:Null =>
      DebugPrint("SNull")
      true
    case _:ObjectExpr =>
      DebugPrint("SObjectExpr")
      true
    case _:Op =>
      DebugPrint("SOp")
      true
    case _:Parenthesized =>
      DebugPrint("SParenthesized")
      false
    case _:PrefixOpApp =>
      DebugPrint("SPrefixOpApp")
      true
    case _:Program =>
      DebugPrint("SProgram")
      true
    case _:PropId =>
      DebugPrint("SPropId")
      true
    case _:PropNum =>
      DebugPrint("SPropNum")
      true
    case _:PropStr =>
      DebugPrint("SPropStr")
      true
    case _:RegularExpression =>
      DebugPrint("SRegularExpression")
      true
    case _:Return =>
      DebugPrint("SReturn")
      true
    case _:SetProp =>
      DebugPrint("SSetProp")
      true
    case _:SpanInfo =>
      DebugPrint("SpanInfo")
      false
    case SStringLiteral(_, _, txt) =>
      val str = NU.unescapeJava(txt)
      DebugPrint("SStringLiteral \"" + str + "\"")
      true
    case _:Switch =>
      DebugPrint("SSwitch")
      true
    case _:This =>
      DebugPrint("SThis")
      true
    case _:Throw =>
      DebugPrint("SThrow")
      true
    case _:TopLevel =>
      DebugPrint("STopLevel")
      true  
    case _:Try =>
      DebugPrint("STry")
      true
    case _:UnaryAssignOpApp =>
      DebugPrint("SUnaryAssignOpApp")
      true
    case _:VarDecl =>
      DebugPrint("SVarDecl")
      true
    case _:VarRef =>
      DebugPrint("SVarRef")
      true
    case _:VarStmt =>
      DebugPrint("SVarStmt")
      true
    case _:While =>
      DebugPrint("SWhile")
      true
    case _:With =>
      DebugPrint("SWith")
      true
    case xs:List[_] => false
    case xs:Option[_] => false
    case _ => false
  }

  def isSignificant(node:Any):Boolean = node match {
    case _:AnonymousFnName =>
      DebugPrint("SAnonymousFnName")
      false
    case _:ArrayExpr =>
      DebugPrint("SArrayExpr")
      false
    case _:AssignOpApp =>
      DebugPrint("SAssignOpApp")
      false
    case _:Block =>
      DebugPrint("SBlock")
      true
    case _:Bool =>
      DebugPrint("SBool")
      false
    case _:Bracket =>
      DebugPrint("SBracket")
      false
    case _:Break =>
      DebugPrint("SBreak")
      false
    case _:Case =>
      DebugPrint("SCase")
      true
    case _:Catch =>
      DebugPrint("SCatch")
      false
    case _:Cond =>
      DebugPrint("SCond")
      false
    case _:Continue =>
      DebugPrint("SContinue")
      false
    case _:Debugger =>
      DebugPrint("SDebugger")
      false
    case _:DoWhile =>
      DebugPrint("SDoWhile")
      true
    case _:Dot =>
      DebugPrint("SDot")
      false
    case _:EmptyStmt =>
      DebugPrint("SEmptyStmt")
      false
    case _:ExprList =>
      DebugPrint("SExprList")
      false
    case _:ExprStmt =>
      DebugPrint("SExprStmt")
      false
    case _:Field =>
      DebugPrint("SField")
      false
    case _:DoubleLiteral =>
      DebugPrint("SDoubleLiteral")
      false
    case _:For =>
      DebugPrint("SFor")
      true
    case _:ForIn =>
      DebugPrint("SForIn")
      true
    case _:ForVar =>
      DebugPrint("SForVar")
      true
    case _:ForVarIn =>
      DebugPrint("SForVarIn")
      true
    case _:FunApp =>
      DebugPrint("SFunApp")
      false
    case _:FunDecl =>
      DebugPrint("SFunDecl")
      true
    case _:FunExpr =>
      DebugPrint("SFunExpr")
      true
    case _:GetProp =>
      DebugPrint("SGetProp")
      false
    case SId(_, text, _, _) =>
      DebugPrint("SId \"" + text + "\"")
      false
    case _:If =>
      DebugPrint("SIf")
      true
    case _:InfixOpApp =>
      DebugPrint("SInfixOpApp")
      false
    case _:IntLiteral =>
      DebugPrint("SIntLiteral")
      false
    case _:Label =>
      DebugPrint("SLabel")
      false
    case _:LabelStmt =>
      DebugPrint("SLabelStmt")
      false
    case _:New =>
      DebugPrint("SNew")
      false
    case _:Null =>
      DebugPrint("SNull")
      false
    case _:ObjectExpr =>
      DebugPrint("SObjectExpr")
      false
    case _:Op =>
      DebugPrint("SOp")
      false
    case _:Parenthesized =>
      DebugPrint("SParenthesized")
      false
    case _:PrefixOpApp =>
      DebugPrint("SPrefixOpApp")
      false
    case _:Program =>
      DebugPrint("SProgram")
      false
    case _:PropId =>
      DebugPrint("SPropId")
      false
    case _:PropNum =>
      DebugPrint("SPropNum")
      false
    case _:PropStr =>
      DebugPrint("SPropStr")
      false
    case _:RegularExpression =>
      DebugPrint("SRegularExpression")
      false
    case _:Return =>
      DebugPrint("SReturn")
      false
    case _:SetProp =>
      DebugPrint("SSetProp")
      false
    case _:SpanInfo =>
      DebugPrint("SpanInfo")
      false
    case SStringLiteral(_, _, txt) =>
      val str = NU.unescapeJava(txt)
      DebugPrint("SStringLiteral \"" + str + "\"")
      false
    case _:Switch =>
      DebugPrint("SSwitch")
      true
    case _:This =>
      DebugPrint("SThis")
      false
    case _:Throw =>
      DebugPrint("SThrow")
      false
    case _:TopLevel =>
      DebugPrint("STopLevel")
      false
    case _:Try =>
      DebugPrint("STry")
      true
    case _:UnaryAssignOpApp =>
      DebugPrint("SUnaryAssignOpApp")
      false
    case _:VarDecl =>
      DebugPrint("SVarDecl")
      false
    case _:VarRef =>
      DebugPrint("SVarRef")
      false
    case _:VarStmt =>
      DebugPrint("SVarStmt")
      false
    case _:While =>
      DebugPrint("SWhile")
      true
    case _:With =>
      DebugPrint("SWith")
      false
    case xs:List[_] => false
    case xs:Option[_] => false
    case _ => false
  }
}