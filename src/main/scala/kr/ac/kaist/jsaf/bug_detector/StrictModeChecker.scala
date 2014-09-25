/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

/**
 * Annex C (The Strict Mode of ECMAScript)
 *   O [R1] The identifiers "implements", "interface", "let", "package", "private", "protected", "public", "static", and "yield" are classified as FutureReservedWord tokens within strict mode code. (7.6.12).
 *   O [R2] A conforming implementation, when processing strict mode code, may not extend the syntax of NumericLiteral (7.8.3) to include OctalIntegerLiteral as described in B.1.1.
 *   O [R3] A conforming implementation, when processing strict mode code (see 10.1.1), may not extend the syntax of EscapeSequence to include OctalEscapeSequence as described in B.1.2.
 *   O [R4-1] Assignment to an undeclared identifier or otherwise unresolvable reference does not create a property in the global object. When a simple assignment occurs within strict mode code, its LeftHandSide must not evaluate to an unresolvable Reference. If it does a ReferenceError exception is thrown (8.7.2).
 *   O [R4-2] The LeftHandSide also may not be a reference to a data property with the attribute value {[[Writable]]:false},
 *   X [R4-3]   to an accessor property with the attribute value {[[Set]]:undefined},
 *   O [R4-4]   nor to a non-existent property of an object whose [[Extensible]] internal property has the value false. In these cases a TypeError exception is thrown (11.13.1).
 *   O [R5] The identifier eval or arguments may not appear as the LeftHandSideExpression of an Assignment operator (11.13) or of a PostfixExpression (11.3) or as the UnaryExpression operated upon by a Prefix Increment (11.4.4) or a Prefix Decrement (11.4.5) operator.
 *   X [R6] Arguments objects for strict mode functions define non-configurable accessor properties named "caller" and "callee" which throw a TypeError exception on access (10.6).
 *   X [R7] Arguments objects for strict mode functions do not dynamically share their array indexed property values with the corresponding formal parameter bindings of their functions. (10.6).
 *   X [R8] For strict mode functions, if an arguments object is created the binding of the local identifier arguments to the arguments object is immutable and hence may not be the target of an assignment expression. (10.5).
 *   O [R9] It is a SyntaxError if strict mode code contains an ObjectLiteral with more than one definition of any data property (11.1.5).
 *   O [R10] It is a SyntaxError if the Identifier "eval" or the Identifier "arguments" occurs as the Identifier in a PropertySetParameterList of a PropertyAssignment that is contained in strict code or if its FunctionBody is strict code (11.1.5).
 *   X [R11] Strict mode eval code cannot instantiate variables or functions in the variable environment of the caller to eval. Instead, a new variable environment is created and that environment is used for declaration binding instantiation for the eval code (10.4.2).
 *   X [R12] If this is evaluated within strict mode code, then the this value is not coerced to an object. A this value of null or undefined is not converted to the global object and primitive values are not converted to wrapper objects. The this value passed via a function call (including calls made using Function.prototype.apply and Function.prototype.call) do not coerce the passed this value to an object (10.4.3, 11.1.1, 15.3.4.3, 15.3.4.4).
 *   O [R13] When a delete operator occurs within strict mode code, a SyntaxError is thrown if its UnaryExpression is a direct reference to a variable, function argument, or function name(11.4.1).
 *   O [R14] When a delete operator occurs within strict mode code, a TypeError is thrown if the property to be deleted has the attribute { [[Configurable]]:false } (11.4.1).
 *   O [R15] It is a SyntaxError if a VariableDeclaration or VariableDeclarationNoIn occurs within strict code and its Identifier is eval or arguments (12.2.1).
 *   O [R16] Strict mode code may not include a WithStatement. The occurrence of a WithStatement in such a context is an SyntaxError (12.10).
 *   O [R17] It is a SyntaxError if a TryStatement with a Catch occurs within strict code and the Identifier of the Catch production is eval or arguments (12.14.1)
 *   O [R18] It is a SyntaxError if the identifier eval or arguments appears within a FormalParameterList of a strict mode FunctionDeclaration or FunctionExpression (13.1)
 *   O [R19] A strict mode function may not have two or more formal parameters that have the same name. An attempt to create such a function using a FunctionDeclaration, FunctionExpression, or Function constructor is a SyntaxError (13.1, 15.3.2).
 *   X [R20] An implementation may not extend, beyond that defined in this specification, the meanings within strict mode functions of properties named caller or arguments of function instances. ECMAScript code may not create or modify properties with these names on function objects that correspond to strict mode functions (10.6, 13.2, 15.3.4.5.3).
 *   O [R21] It is a SyntaxError to use within strict mode code the identifiers eval or arguments as the Identifier of a FunctionDeclaration or FunctionExpression or as a formal parameter name (13.1). Attempting to dynamically define such a strict mode function using the Function constructor (15.3.2) will throw a SyntaxError exception.
 *
 * 11.4.1 The delete Operator
 *   O [11.4.1] 3.a If IsStrictReference(ref) is true, throw a SyntaxError exception.
 */

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.cfg.{Block => CFGCmdBlock}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.CallContext._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes.{Node => ASTRootNode}
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.scala_src.nodes._
import scala.collection.mutable.{ArrayBuffer, HashSet => MHashSet, Stack => MStack}

object StrictModeChecker {
  ////////////////////////////////////////////////////////////////////////////////
  // Bug list
  ////////////////////////////////////////////////////////////////////////////////
  val bugList = new ArrayBuffer[BugInfo]

  def clear(): Unit = bugList.clear()
  def insertBug(span: Span, bugKind: Int, args: String*): Unit = {
    val bugInfo = args.length match {
      case 0 => new BugInfo(span, bugKind, null, null)
      case 1 => new BugInfo(span, bugKind, args(0), null)
      case _ => new BugInfo(span, bugKind, args(0), args(1))
    }
    bugList.append(bugInfo)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Check Mains
  ////////////////////////////////////////////////////////////////////////////////
  // Check AST simply
  def checkSimple(ast: Program): Unit = {
    object StrictModeCheckWalker extends Walkers {
      ////////////////////////////////////////////////////////////////////////////////
      // AST Part
      ////////////////////////////////////////////////////////////////////////////////
      // Strict mode flag stack
      val strictFlagStack = new MStack[Boolean]
      // AST Walker
      override def walkAST(parent: Any, node: Any): Unit = {
        // Push strict mode flag
        val isPushed = node match {
          case SFunDecl(info, ftn, strict) => strictFlagStack.push(strict); true
          case SVarDecl(info, id, expr, strict) => strictFlagStack.push(strict); true
          case SSourceElements(info, body, strict) => strictFlagStack.push(strict); true
          case _ => false
        }

        // If the stack is empty it's TopLevel node (nothing to check)
        if(strictFlagStack.isEmpty) {super.walkAST(parent, node); return}

        // Stirct mode check
        if(strictFlagStack.top) {
          node match {
            // [R1]
            case SId(info, text, uniqueName, _with) if NodeUtil.isFutureReserved(text) =>
              insertBug(info.getSpan, StrictModeR1, text)
            // [R2]
            case SIntLiteral(info, intVal, radix) if radix == 8 =>
              insertBug(info.getSpan, StrictModeR2, intVal.toString(radix))
            // [R3]
            case SStringLiteral(info, quote, escaped) if NodeUtil.hasOctalEscapeSequence(escaped) =>
              insertBug(info.getSpan, StrictModeR3, escaped)
            // [R5]
            case SPrefixOpApp(info1, op, SVarRef(info2, id)) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(info2.getSpan, StrictModeR5, id.getText, "UnaryExpression")
            case SUnaryAssignOpApp(info1, SVarRef(info2, id), op) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(info2.getSpan, StrictModeR5, id.getText, "PostfixExpression")
            case SAssignOpApp(info1, SVarRef(info2, id), op, right) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(info2.getSpan, StrictModeR5, id.getText, "LeftHandSideExpression")
            // [R9]
            case SObjectExpr(info, members) =>
              val idSet = new MHashSet[String]
              for(member <- members if member.isInstanceOf[Field]) {
                val field = member.asInstanceOf[Field]
                val idText = NodeUtil.prop2Str(field.getProp)
                if(idSet.contains(idText)) insertBug(info.getSpan, StrictModeR9, idText)
                else idSet.add(idText)
              }
            // [R10]
            case SPropId(info, id) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(info.getSpan, StrictModeR10, id.getText, "assignment")
            // [R10]
            case SPropStr(info, str) if NodeUtil.isEvalOrArguments(str) =>
              insertBug(info.getSpan, StrictModeR10, str, "assignment")
            // [R10] (Same to [R18])
            /*case SSetProp(info, prop, SFunctional(fds, vds, stmts, id, params)) if !params.isEmpty && NodeUtil.isEvalOrArguments(params.head) =>
              insertBug(info.getSpan, StrictModeR10, params.head.getText, "property set parameter")*/
            // [R13]
            case SPrefixOpApp(info1, op, SVarRef(info2, id)) if op.getText == "delete" =>
              insertBug(info1.getSpan, StrictModeR13, id.getText)
            // [R15]
            case SVarDecl(info, id, expr, strict) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(id.getInfo.getSpan, StrictModeR15, id.getText)
            // [R16]
            case SWith(info, expr, stmt) =>
              insertBug(info.getSpan, StrictModeR16)
            // [R17]
            case SCatch(info, id, body) if NodeUtil.isEvalOrArguments(id) =>
              insertBug(id.getInfo.getSpan, StrictModeR17, id.getText)
            // [R18], [R19], [R21]
            case SFunctional(fds, vds, stmts, id, params) =>
              val paramIdSet = new MHashSet[String]
              for(paramId <- params) {
                val paramIdText = paramId.getText
                // [R18], [R21]
                if(NodeUtil.isEvalOrArguments(paramIdText)) insertBug(paramId.getInfo.getSpan, StrictModeR18, paramIdText)
                // [R19]
                if(paramIdSet.contains(paramIdText)) insertBug(paramId.getInfo.getSpan, StrictModeR19, paramIdText)
                else paramIdSet.add(paramIdText)
              }
              // [R21]
              if(NodeUtil.isEvalOrArguments(id)) insertBug(id.getInfo.getSpan, StrictModeR21, id.getText)
            case _ =>
          }
        }

        // Walk child nodes
        super.walkAST(parent, node)

        // Pop strict mode flag
        if(isPushed) strictFlagStack.pop()
      }
    }

    // AST walk begin
    StrictModeCheckWalker.walkAST(null, ast)
  }

  // Check CFG by using analysis result (Complete! NOT Sound!)
  def checkAdvanced(ast: Program, cfg: CFG, varManager: VarManager, stateManager: StateManager): Unit = {
    object StrictModeCheckWalker extends Walkers {
      ////////////////////////////////////////////////////////////////////////////////
      // AST Part
      ////////////////////////////////////////////////////////////////////////////////
      // Strict mode flag stack
      val strictFlagStack = new MStack[Boolean]
      // Strict mode AST set
      val strictASTSet = new NodeHashMap[ASTRootNode, Boolean]

      // AST Walker
      override def walkAST(parent: Any, node: Any): Unit = {
        // Push strict mode flag
        val isPushed = node match {
          case SFunDecl(info, ftn, strict) => strictFlagStack.push(strict); true
          case SVarDecl(info, id, expr, strict) => strictFlagStack.push(strict); true
          case SSourceElements(info, body, strict) => strictFlagStack.push(strict); true
          case _ => false
        }

        // Collect strict mode code ASTs
        node match {
          case astNode: ASTRootNode if !strictFlagStack.isEmpty => strictASTSet.put(astNode, strictFlagStack.top)
          case _ =>
        }

        // Walk child nodes
        super.walkAST(parent, node)

        // Pop strict mode flag
        if(isPushed) strictFlagStack.pop()
      }

      ////////////////////////////////////////////////////////////////////////////////
      // CFG Part
      ////////////////////////////////////////////////////////////////////////////////
      // CFGInst stack
      val instStack = new MStack[CFGInst]

      // CFGNode, CState
      var cfgNode: kr.ac.kaist.jsaf.analysis.cfg.Node = null
      var mergedCState: CState = null
      def getCFGNodeAndCState(inst: CFGInst): Unit = {
        cfgNode = cfg.findEnclosingNode(inst)
        mergedCState = stateManager.getInputCState(cfgNode, inst.getInstId, CallContext._MOST_SENSITIVE)
      }

      // CFG Walker
      override def walkCFG(parent: Any, node: Any): Unit = {
        // Push CFGInst
        val isPushed = node match {
          case inst: CFGInst => instStack.push(inst); true
          case _ => false
        }

        // Get strict mode flag from AST
        val isStrict = node match {
          case cfgNode: CFGNode =>
            NodeRelation.cfg2astMap.get(cfgNode) match {
              case Some(astNode) => strictASTSet.getOrElse(astNode, false)
              case None => false //throw new RuntimeException("Error!")
            }
          case _ => false
        }

        // Strict mode check
        if(isStrict) {
          // Debug
          /*node match {
            case inst: CFGInst => println("Strict mode inst : [" + inst.getInstId + "] " + inst)
            case expr: CFGExpr => println("Strict mode expr : [" + instStack.top.getInstId + "] " + expr)
            case _ =>
          }*/

          node match {
            // [R4-1], [R4-2]
            case inst@CFGExprStmt(iid, info1, userId@CFGUserId(info2, text, kind, originalName, fromWith), expr) =>
              getCFGNodeAndCState(inst)

              // [R4-1]
              if(!NodeUtil.isInternal(text)) {
                var doesExist: AbsBool = BoolBot
                for((callContext, state) <- mergedCState) {
                  doesExist+= (kind match {
                    case PureLocalVar | CapturedVar | CapturedCatchVar => BoolTrue
                    case GlobalVar => Helper.HasProperty(state.heap, GlobalLoc, AbsString.alpha(text))
                  })
                }
                if(doesExist == BoolFalse) insertBug(info2.getSpan, StrictModeR4_1, text)
              }

              // [R4-2]
              var writable: AbsBool = BoolBot
              for((callContext, state) <- mergedCState) {
                val locSet = Helper.LookupBase(state.heap, userId)
                for(loc <- locSet) {
                  val propValue = Helper.ProtoProp(state.heap, loc, AbsString.alpha(text))
                  writable+= propValue._1.writable
                }
              }
              if(writable == BoolFalse) insertBug(info2.getSpan, StrictModeR4_2, originalName)

            // [R4-2], [R4-4]
            case inst@CFGStore(iid, info, obj, index, rhs) =>
              getCFGNodeAndCState(inst)

              for((callContext, state) <- mergedCState) {
                val objLocSet = SE.V(obj, state.heap, state.context)._1.locset
                val propValue = SE.V(index, state.heap, state.context)._1.pvalue

                // Check for each object location
                var writable: AbsBool = BoolBot
                var extensible: AbsBool = BoolBot
                for(objLoc <- objLocSet) {
                  // Check for each primitive value
                  for(absValue <- propValue) {
                    if(!absValue.isBottom) {
                      // [R4-2]
                      val propValue = Helper.ProtoProp(state.heap, objLoc, absValue.toAbsString)
                      writable+= propValue._1.writable

                      // [R4-4]
                      if(Helper.HasProperty(state.heap, objLoc, absValue.toAbsString) == BoolFalse)
                        extensible+= state.heap(objLoc)("@extensible").objval.value.pvalue.boolval
                    }
                  }
                }
                if(writable == BoolFalse || extensible == BoolFalse) {
                  // Get the object name and property name
                  val text = varManager.getUserVarAssign(obj, index) match {
                    case bv: BugVar0 => bv.toString
                    case _ => "LeftHandSide"
                  }

                  // [R4-2]
                  if(writable == BoolFalse) insertBug(info.getSpan, StrictModeR4_2, text)
                  // [R4-4]
                  if(extensible == BoolFalse) insertBug(info.getSpan, StrictModeR4_4, text)
                }
              }
            // [R6]
            case expr@CFGLoad(info, obj, index) =>
              val inst = NodeRelation.getParentCFGInst(expr)
              if(inst != null) {
                varManager.getUserVarAssign(obj) match {
                  case BugVar0(CFGUserId(info2, text, kind, originalName, fromWith), _) if originalName == "arguments" =>
                    getCFGNodeAndCState(inst)

                    var isCallerCallee: AbsBool = BoolBot
                    for((callContext, state) <- mergedCState) {
                      val propValue = SE.V(index, state.heap, state.context)._1.pvalue
                      for(absValue <- propValue) {
                        if(!absValue.isBottom) {
                          isCallerCallee+= AbsBool.alpha(absValue.toString == "\"caller\"" || absValue.toString == "\"callee\"")
                        }
                      }
                    }
                    if(isCallerCallee == BoolTrue) insertBug(info.getSpan, StrictModeR6)
                  case _ =>
                }
              }
            // [R14]
            case inst@CFGDelete(iid, info1, lhs, expr@CFGVarRef(info2, id)) =>
              getCFGNodeAndCState(inst)

              var configurable: AbsBool = BoolBot
              for((callContext, state) <- mergedCState) {
                val locSet = Helper.LookupBase(state.heap, id)
                for(loc <- locSet) {
                  val propValue = Helper.ProtoProp(state.heap, loc, AbsString.alpha(id.getText))
                  configurable+= propValue._1.configurable
                }
              }
              if(configurable == BoolFalse) {
                val text = varManager.getUserVarAssign(expr) match {
                  case bv: BugVar0 => bv.toString
                  case _ => id.getText
                }

                insertBug(info1.getSpan, StrictModeR14, text)
              }

            // [R14], [11.4.1] 3.a
            case inst@CFGDeleteProp(iid, info, lhs, obj, index) =>
              getCFGNodeAndCState(inst)

              for((callContext, state) <- mergedCState) {
                val objLocSet = SE.V(obj, state.heap, state.context)._1.locset
                val propValue = SE.V(index, state.heap, state.context)._1.pvalue

                // Check for each object location
                var configurable: AbsBool = BoolBot
                var doesExists: AbsBool = BoolBot
                for(objLoc <- objLocSet) {
                  // Check for each primitive value
                  for(absValue <- propValue) {
                    if(!absValue.isBottom) {
                      // [R14]
                      val propValue = Helper.ProtoProp(state.heap, objLoc, absValue.toAbsString)
                      configurable+= propValue._1.configurable

                      // [11.4.1] 3.a
                      doesExists+= Helper.HasProperty(state.heap, objLoc, absValue.toAbsString)
                    }
                  }
                }
                if(configurable == BoolFalse || doesExists == BoolFalse) {
                  // Get the object name and property name
                  val text = varManager.getUserVarAssign(obj, index) match {
                    case bv: BugVar0 => bv.toString
                    case _ => "LeftHandSide"
                  }

                  // [R14]
                  if(configurable == BoolFalse) insertBug(info.getSpan, StrictModeR14, text)
                  // [11.4.1] 3.a
                  if(doesExists == BoolFalse) insertBug(info.getSpan, StrictMode11_4_1_3_a, text)
                }
              }

            case expr@CFGVarRef(info, id) =>
              val inst = NodeRelation.getParentCFGInst(expr)
              if(inst != null) {
                getCFGNodeAndCState(inst)
                for((callContext, state) <- mergedCState) {
                  val h = state.heap
                  val x = id.getText
                  id.getVarKind match {
                    case GlobalVar if (h.domIn(GlobalLoc)) =>
                        /* Dead path, we believe...
                      h(GlobalLoc)("@proto")._1._1._1._2.foreach(l_proto =>
                                  if (!(BoolTrue <= Helper.HasProperty(h, l_proto, AbsString.alpha(x))) &&
                                      !(BoolTrue <= h(GlobalLoc).domIn(x))) {
                            insertBug(info.getSpan, StrictMode10_2_1_2_4, x)
                        */
                    case k =>
                  }
                }
              }

            case _ =>
          }
        }

        // Walk child nodes
        super.walkCFG(parent, node)

        // Pop CFGInst
        if(isPushed) instStack.pop()
      }
    }

    // Walk AST nodes to collect only strict mode code ASTs
    StrictModeCheckWalker.walkAST(null, ast)

    // Walk CFG nodes to check strict mode
    for(node <- cfg.getNodes) {
      cfg.getCmd(node) match {
        case CFGCmdBlock(insts) => StrictModeCheckWalker.walkCFG(null, insts)
        case _ =>
      }
    }
  }
}
