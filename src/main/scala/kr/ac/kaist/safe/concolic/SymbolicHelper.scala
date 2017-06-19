/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.concolic

import scala.collection.mutable.HashMap
import _root_.java.util.{ List => JList }
import kr.ac.kaist.safe.errors.error.ConcolicError
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.useful.Options._

/* Now, consider only binary operation and integer type.
 * Reference:
 * s    ::= begin
          | x = e
          | x = x(x, x)
          | x = function f (x, x) {s*}
          | function f (x, x) {s*}
          | return e?
          | var x
          | s*
          | if (e) then s (else s)?
          | while (e) s
 * e    ::= e op e
          | uop e
          | x
          | num
          | this
          | true
          | false
 * op   ::= + | - | * | / | % | < | <= | > | >= | == | != | === | !==
 * uop  ::= !
 */
class SymbolicHelper(I: InterpreterMain) {
  val symbol = "s"
  val input_symbol = "i"
  val this_symbol = "this"
  var index, input_index = 0

  // Mapping between object declaration and object reference
  var objectMemory = new HashMap[String, String]
  // Mapping between arguments and symbolic value
  var argumentsMemory = new HashMap[String, List[Option[SymbolicValue]]]
  val symbolic_memory = new HashMap[String, SymbolicValue]

  var recursive: List[Option[SymbolicValue]] = null

  // Mapping symbolic helper function to environment in which the function is defined
  var environments = new HashMap[String, IRId]
  def storeEnvironment(v: IRId, env: IRId) = environments(v.uniqueName) = env
  def getEnvironment(v: IRId) = environments(v.uniqueName)
  def ignoreCall(f: IRId) = environments.get(f.uniqueName) match {
    case Some(e) => e.uniqueName == "<>Concolic<>Main"
    case None => false //true
  }

  var report = List[SymbolicInfo]()
  // Type of information
  val STATEMENT = 1
  val BRANCH = 2
  val ENDBRANCH = 3

  var coverage: Coverage = null

  val maxDepth = 3
  var loopDepth = new HashMap[String, Int]

  // Flag for concolic testing
  var doConcolic = false

  // Estimate time
  var startTime: Long = 0

  def initialize(cov: Coverage) = {
    coverage = cov

    index = 0
    input_index = 0

    symbolic_memory.clear()
    objectMemory.clear()

    argumentsMemory.clear()
    recursive = List()
    coverage.functions.map(_._2.initDepth)

    loopDepth.clear()

    report = List[SymbolicInfo]()

    System.out.println
    var target = coverage.target
    if (target != null && target.contains("@"))
      target = target.substring(0, target.indexOf("@"))
    System.out.println("Current target function: " + target)
  }

  // Initialize the symbolic memorys
  def walkVarStmt(id: IRId, n: Int, env: IRId): Unit = {
    if (checkFocus(env)) {
      if (recursive.nonEmpty) {
        recursive(n) match {
          // Only when an argument of a recursive call is not concrete value.
          case Some(sv) =>
            symbolic_memory(id.uniqueName) = sv
            coverage.functions(env.uniqueName).getObjectProperties(n) match {
              case Some(props) =>
                val constructors = coverage.functions(env.uniqueName).getObjectConstructors(n)
                if (constructors(0) == "Array") {
                  val length = props(0).toInt
                  for (p <- 0 until length) {
                    var propValue = new SymbolicValue
                    propValue.makeSymbolicValue(obj2str(sv.getValue, p.toString), "Number")
                    symbolic_memory(obj2str(id.uniqueName, p.toString)) = propValue
                  }
                } else {
                  for (prop <- props) {
                    var propValue = new SymbolicValue
                    // Suppose types of properties could be only 'Number'
                    propValue.makeSymbolicValue(obj2str(sv.getValue, prop), "Number")
                    symbolic_memory(obj2str(id.uniqueName, prop)) = propValue
                  }
                }
              case None =>
            }
          case None =>
            symbolic_memory -= id.uniqueName
        }
      } else {
        //TODO: Handle multiple types
        val ptype = coverage.functions(env.uniqueName).getType(n)(0)
        var value = new SymbolicValue
        value.makeSymbolicValue(symbol + index, ptype)
        symbolic_memory(id.uniqueName) = value

        var inputValue = new SymbolicValue
        inputValue.makeSymbolicValue(input_symbol + input_index, ptype)
        var info = new SymbolicInfo(false, Some(value), None, Some(inputValue), None, None)
        info.setType(STATEMENT)

        report = report :+ info

        // When parameter is object
        coverage.functions(env.uniqueName).getObjectProperties(n) match {
          case Some(props) =>
            val constructors = coverage.functions(env.uniqueName).getObjectConstructors(n)
            // Consider when an argument is an empty object.
            if (constructors.nonEmpty) {
              if (constructors(0) == "Array") {
                val length = props(0).toInt
                for (p <- 0 until length) {
                  var propValue = new SymbolicValue
                  propValue.makeSymbolicValue(obj2str(symbol + index, p.toString), "Number")
                  symbolic_memory(obj2str(id.uniqueName, p.toString)) = propValue

                  var propInputValue = new SymbolicValue
                  propInputValue.makeSymbolicValue(obj2str(input_symbol + input_index, p.toString), "Number")
                  var info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
                  info.setType(STATEMENT)

                  report = report :+ info
                }
              } else {
                for (prop <- props) {
                  // Suppose types of properties could be only 'Number'
                  var propValue = new SymbolicValue
                  propValue.makeSymbolicValue(obj2str(symbol + index, prop), "Number")
                  symbolic_memory(obj2str(id.uniqueName, prop)) = propValue

                  var propInputValue = new SymbolicValue
                  propInputValue.makeSymbolicValue(obj2str(input_symbol + input_index, prop), "Number")
                  var info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
                  info.setType(STATEMENT)

                  report = report :+ info
                }
              }
            }
          case None =>
        }
        index += 1
        input_index += 1
      }
    }
  }

  /* If its environment, that is the function in which the 'executeAssignment' statement is instrumented is targeted,
   * the symbolic execution proceeds and its change is reported to build symbolic execution tree.
   * Otherwise, just report symbolic variables which don't represent local variables.
   * When local variables are in that expressions, just use concrete value instead of symbolic varialbes of local variables.
   */
  def executeAssignment(loc: String, id: IRId, expr: IRExpr, c: Option[SymbolicValue], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = {
    if (checkFocus(env)) {
      expr match {
        /* variable op varialbe */
        //TODO: extend the range to cover all expressions, first and second
        case IRBin(_, first, op, second) => op.kind match {
          //TODO: find simple way to distinguish operation type
          // op is supported by the constraint solver
          case EJSInstOf => symbolic_memory -= id.uniqueName
          case EJSIn => symbolic_memory -= id.uniqueName
          case EJSShiftLeft => symbolic_memory -= id.uniqueName
          case EJSShiftSRight => symbolic_memory -= id.uniqueName
          case EJSShiftUSRight => symbolic_memory -= id.uniqueName
          case EJSBitXor => symbolic_memory -= id.uniqueName
          case EJSBitOr => symbolic_memory -= id.uniqueName
          case _ =>
            // BIN_ARITH_MUL_MULTIPLICATION
            // BIN_ARITH_MUL_DIVISION
            // BIN_ARITH_MUL_REMINDER
            // ETC_PLUS
            // ETC_MINUS

            // BIN_BIT_BIT_AND

            // BIN_COMP_REL_LESS
            // BIN_COMP_REL_GREATER
            // BIN_COMP_REL_LESSEQUAL
            // BIN_COMP_REL_GREATEREQUAL
            // BIN_COMP_EQ_EQUAL
            // BIN_COMP_EQ_NEQUAL
            // BIN_COMP_EQ_SEQUAL
            // BIN_COMP_EQ_SNEQUAL
            //TODO: When c2 or c1 are None, we have to error reporting.
            var context: (Option[SymbolicValue], Option[SymbolicValue]) = null
            first match {
              case v1: IRId => second match {
                case v2: IRId =>
                  if (symbolic_memory.contains(v1.uniqueName) || symbolic_memory.contains(v2.uniqueName))
                    context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, STATEMENT)
                  else
                    symbolic_memory -= id.uniqueName
                case _ =>
                  if (symbolic_memory.contains(v1.uniqueName)) {
                    context = (setInstanceType(symbolic_memory(v1.uniqueName), c1), c2)
                  }
              }
              case v1: IRLoad => findObjectName(v1.obj) match {
                case Some(o1) =>
                  val id1 = o1 + "." + v1.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                  second match {
                    case v2: IRId =>
                      if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.uniqueName))
                        context = makeContext(id1, v2.uniqueName, op, c1, c2, STATEMENT)
                      else
                        symbolic_memory -= id.uniqueName
                    case v2: IRLoad => findObjectName(v2.obj) match {
                      case Some(o2) =>
                        val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                          context = makeContext(id1, id2, op, c1, c2, STATEMENT)
                        else
                          symbolic_memory -= id.uniqueName
                      case None =>
                        System.out.println("1 The object should be in object memory.")
                    }
                    case _ =>
                      if (symbolic_memory.contains(id1))
                        context = (setInstanceType(symbolic_memory(id1), c1), c2)
                  }
                case None =>
                  System.out.println("2 The object should be in object memory.")
              }
              case _ => second match {
                case v2: IRLoad => findObjectName(v2.obj) match {
                  case Some(o2) =>
                    val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                    if (symbolic_memory.contains(id2))
                      context = (c1, setInstanceType(symbolic_memory(id2), c2))
                  case None =>
                    System.out.println("3 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolic_memory.contains(v2.uniqueName))
                    context = (c1, setInstanceType(symbolic_memory(v2.uniqueName), c2))
                case _ =>
              }
            }
            if (context != null) {
              c match {
                case Some(value) =>
                  var sid = new SymbolicValue
                  sid.makeSymbolicValue(symbol + index, value.getTypes)
                  symbolic_memory(id.uniqueName) = sid
                  index += 1
                  val info = new SymbolicInfo(false, Some(sid), Some(op.ast.toString()), context._1, context._2, None)
                  info.setType(STATEMENT)
                  report = report :+ info
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
        }
        case IRUn(_, op, expr) =>
        case ir @ IRLoad(info, obj, _index) => findObjectName(obj) match {
          case Some(o) =>
            val indexValue = _index match {
              case id: IRId => id.uniqueName
              case IRVal(EJSString(str)) => str
              case IRVal(EJSNumber(text, num)) => text
            }
            val v = o + "." + indexValue
            if (symbolic_memory.contains(v)) {
              c match {
                case Some(value) =>
                  var sid = new SymbolicValue
                  sid.makeSymbolicValue(symbol + index, value.getTypes)
                  symbolic_memory(id.uniqueName) = sid
                  index += 1
                  val info = new SymbolicInfo(false, Some(sid), None, Some(symbolic_memory(v)), None, None)
                  info.setType(STATEMENT)
                  report = report :+ info
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
          case None =>
        }
        case v: IRId =>
          if (symbolic_memory.contains(v.uniqueName))
            symbolic_memory(id.uniqueName) = symbolic_memory(v.uniqueName)
          // Do not need to report because symbolic memory is replaced
          else
            symbolic_memory -= id.uniqueName
        case _: IRThis =>
        case n @ IRVal(EJSNumber(_, _)) =>
          symbolic_memory -= id.uniqueName
        case s @ IRVal(EJSString(_)) =>
        case b @ IRVal(EJSBool(_)) =>
        case IRVal(EJSUndef) =>
        case IRVal(EJSNull) =>
      }
    }
  }

  def executeCondition(expr: IRExpr, branchTaken: Option[Boolean], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = {
    //TODO: need rewriter to modify the expressions syntatically accepted to the expressions supported by symbolic helper
    if (checkFocus(env)) {
      expr match {
        case IRBin(_, first, op, second) => op.kind match {
          //TODO: find simple way to distinguish operation type
          case EJSInstOf =>
          case EJSIn =>
          case EJSMul =>
          case EJSDiv =>
          case EJSRem =>
          case EJSPos =>
          case EJSNeg =>
          case EJSShiftLeft =>
          case EJSShiftSRight =>
          case EJSShiftUSRight =>
          case EJSBitAnd =>
          case EJSBitXor =>
          case EJSBitOr =>
          //TODO: construct branch bitvector
          case _ =>
            //TODO: construct branch bitvector
            var context: (Option[SymbolicValue], Option[SymbolicValue]) = null
            first match {
              case v1: IRId => second match {
                case v2: IRId =>
                  if (symbolic_memory.contains(v1.uniqueName) || symbolic_memory.contains(v2.uniqueName)) {
                    //context = createContext(v1.uniqueName, v2.uniqueName, op, res1, res2)
                    context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, BRANCH)
                  }
                case _ =>
                  if (symbolic_memory.contains(v1.uniqueName))
                    //context = symbolic_memory(v1.uniqueName) + op.getText + res2
                    context = (setInstanceType(symbolic_memory(v1.uniqueName), c1), c2)
              }
              case IRLoad(_, obj, index) => findObjectName(obj) match {
                case Some(o) =>
                  val id1 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                  second match {
                    case v2: IRId =>
                      if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.uniqueName))
                        //context = createContext(id1, v2.uniqueName, op, res1, res2)
                        context = makeContext(id1, v2.uniqueName, op, c1, c2, BRANCH)
                    case IRLoad(_, obj, index) => findObjectName(obj) match {
                      case Some(o) =>
                        val id2 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                          //context = createContext(id1, id2, op, res1, res2)
                          context = makeContext(id1, id2, op, c1, c2, BRANCH)

                      case None =>
                        System.out.println("5 The object should be in object memory.")
                    }
                    case _ =>
                      if (symbolic_memory.contains(id1))
                        //context = symbolic_memory(id1) + op.getText + res2
                        context = (setInstanceType(symbolic_memory(id1), c1), c2)
                  }
                case None =>
                  System.out.println("6 The object should be in object memory.")
              }
              case _ => second match {
                case IRLoad(_, obj, index) => findObjectName(obj) match {
                  case Some(o) =>
                    val id2 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                    if (symbolic_memory.contains(id2))
                      //context = symbolic_memory(id2) + op.getText + res1
                      context = (c1, setInstanceType(symbolic_memory(id2), c2))
                  case None =>
                    System.out.println("7 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolic_memory.contains(v2.uniqueName))
                    //context = symbolic_memory(v2.uniqueName) + op.getText + res1
                    context = (c1, setInstanceType(symbolic_memory(v2.uniqueName), c2))
              }
            }
            if (context != null) {
              val info = new SymbolicInfo(true, None, Some(op.ast.toString()), context._1, context._2, branchTaken)
              info.setType(BRANCH)
              report = report :+ info
            }
        }
        case IRUn(_, op, e) => e match {
          case v: IRId => if (symbolic_memory.contains(v.uniqueName)) {
            val operation: Option[String] = op.kind match {
              case EJSPos => Some("!=")
              case EJSNeg => Some("!=")
              case EJSLogNot => Some("==")
              case _ => None
            }
            if (operation.isSome) {
              val c = new SymbolicValue
              c.makeSymbolicValue("0", "Number")
              val info = new SymbolicInfo(true, None, operation, Some(symbolic_memory(v.uniqueName)), Some(c), branchTaken)
              info.setType(BRANCH)
              report = report :+ info
            }
          }
        }
        case v: IRId =>
          if (symbolic_memory.contains(v.uniqueName)) {
            val c = new SymbolicValue
            c.makeSymbolicValue("0", "Number")
            val info = new SymbolicInfo(true, None, Some("!="), Some(symbolic_memory(v.uniqueName)), Some(c), branchTaken)
            info.setType(BRANCH)
            report = report :+ info
          }
        case IRLoad(info, obj, index) => findObjectName(obj) match {
          case Some(o) =>
            val id = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
            if (symbolic_memory.contains(id)) {
              val info = new SymbolicInfo(true, None, None, Some(symbolic_memory(id)), None, branchTaken)
              info.setType(BRANCH)
              report = report :+ info
            }
          case None =>
            System.out.println("8 The object should be in object memory.")
        }
        case IRVal(EJSBool(_)) =>
      }
    }
  }

  def endCondition(env: IRId) = {
    if (checkFocus(env)) {
      val info = new SymbolicInfo(true, None, None, None, None, None)
      info.setType(ENDBRANCH)
      report = report :+ info
    }
  }

  def executeStore(obj: IRId, prop: String, expr: IRExpr, c: Option[SymbolicValue], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = {
    if (checkFocus(env)) {
      findObjectName(obj) match {
        case Some(o) =>
          val id = o + "." + prop
          expr match {
            //TODO: extend the range to cover all expressions, first and second
            case IRBin(_, first, op, second) => op.kind match {
              //TODO: find simple way to distinguish operation type
              // op is supported by the constraint solver
              case EJSInstOf => symbolic_memory -= id
              case EJSIn => symbolic_memory -= id
              case EJSShiftLeft => symbolic_memory -= id
              case EJSShiftSRight => symbolic_memory -= id
              case EJSShiftUSRight => symbolic_memory -= id
              case EJSSEq => symbolic_memory -= id
              case EJSSNEq => symbolic_memory -= id
              case EJSBitAnd => symbolic_memory -= id
              case EJSBitXor => symbolic_memory -= id
              case EJSBitOr => symbolic_memory -= id
              case _ =>
                // BIN_ARITH_MUL_MULTIPLICATION
                // BIN_ARITH_MUL_DIVISION
                // BIN_ARITH_MUL_REMINDER
                // ETC_PLUS
                // ETC_MINUS

                // BIN_COMP_REL_LESS
                // BIN_COMP_REL_GREATER
                // BIN_COMP_REL_LESSEQUAL
                // BIN_COMP_REL_GREATEREQUAL
                // BIN_COMP_EQ_EQUAL
                // BIN_COMP_EQ_NEQUAL
                var context: (Option[SymbolicValue], Option[SymbolicValue]) = null
                first match {
                  case v1: IRId => second match {
                    case v2: IRId =>
                      if (symbolic_memory.contains(v1.uniqueName) || symbolic_memory.contains(v2.uniqueName))
                        context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, STATEMENT)
                      else
                        symbolic_memory -= id
                    case _ =>
                      if (symbolic_memory.contains(v1.uniqueName))
                        //context = symbolic_memory(v1.uniqueName) + op.getText + res2
                        context = (setInstanceType(symbolic_memory(v1.uniqueName), c1), c2)
                  }
                  case v1: IRLoad => findObjectName(v1.obj) match {
                    case Some(o1) =>
                      val id1 = o1 + "." + v1.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                      second match {
                        case v2: IRId =>
                          if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.uniqueName))
                            //context = createContext(id1, v2.uniqueName, op, res1, res2)
                            context = makeContext(id1, v2.uniqueName, op, c1, c2, STATEMENT)
                          else
                            symbolic_memory -= id
                        case v2: IRLoad => findObjectName(v2.obj) match {
                          case Some(o2) =>
                            val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                            if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                              //context = createContext(id1, id2, op, res1, res2)
                              context = makeContext(id1, id2, op, c1, c2, STATEMENT)
                            else
                              symbolic_memory -= id
                          case None =>
                            System.out.println("9 The object should be in object memory.")
                        }
                        case _ =>
                          if (symbolic_memory.contains(id1))
                            //context = symbolic_memory(id1) + op.getText + res2
                            context = (setInstanceType(symbolic_memory(id1), c1), c2)
                      }
                    case None =>
                      System.out.println("10 The object should be in object memory.")
                  }
                  case _ => second match {
                    case v2: IRLoad => findObjectName(v2.obj) match {
                      case Some(o2) =>
                        val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolic_memory.contains(id2))
                          //context = symbolic_memory(id2) + op.getText + res1
                          context = (c1, setInstanceType(symbolic_memory(id2), c2))
                      case None =>
                        System.out.println("11 The object should be in object memory.")
                    }
                    case v2: IRId =>
                      if (symbolic_memory.contains(v2.uniqueName))
                        //context = symbolic_memory(v2.uniqueName) + op.getText + res1
                        context = (c1, setInstanceType(symbolic_memory(v2.uniqueName), c2))
                  }
                }
                //if (!context.isEmpty) {
                if (context != null) {
                  //val sid = symbol + index
                  c match {
                    case Some(value) =>
                      var sid = new SymbolicValue
                      sid.makeSymbolicValue(symbol + index, value.getTypes)
                      symbolic_memory(id) = sid
                      index += 1
                      val info = new SymbolicInfo(false, Some(sid), Some(op.ast.toString()), context._1, context._2, None)
                      info.setType(STATEMENT)
                      report = report :+ info
                    case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
                  }
                }
            }
            case IRUn(_, op, expr) =>
            case IRLoad(_, obj, index) =>
            /* variable */
            case v: IRId =>
              if (symbolic_memory.contains(v.uniqueName))
                symbolic_memory(id) = symbolic_memory(v.uniqueName)
              // Do not need to report because symbolic memory is replaced
              else
                symbolic_memory -= id
            case _: IRThis =>
            /* constant value */
            case n @ IRVal(EJSNumber(_, _)) =>
              symbolic_memory -= id
            case s @ IRVal(EJSString(_)) =>
            case b @ IRVal(EJSBool(_)) =>
            case IRVal(EJSUndef) =>
            case IRVal(EJSNull) =>
          }
        case None =>
      }
    }
  }

  def storeArguments(args: IRId, elems: List[Option[IRExpr]]) = {
    var arguments = List[Option[SymbolicValue]]()
    for (elem <- elems)
      if (elem.isSome) {
        val sv = symbolic_memory.get(elem.unwrap.asInstanceOf[IRId].uniqueName)
        arguments = arguments :+ sv
      }
    if (arguments.nonEmpty)
      argumentsMemory(args.uniqueName) = arguments
  }

  def storeThis(env: IRId) = {
    if (checkFocus(env)) {
      if (env.uniqueName.contains("prototype")) {
        // tokenize target name to generate each object and function.
        val target = env.uniqueName
        val token = target.substring(0, target.indexOf("<")).split("prototype")
        if (token.length > 2) throw new ConcolicError("Only a.prototype.x function form is supported")
        val first = token(0).substring(0, token(0).length - 1)
        val second = token(1).substring(1, token(1).length)

        var value = new SymbolicValue
        value.makeSymbolicValue(symbol + index, "Object")
        symbolic_memory("this") = value

        var inputValue = new SymbolicValue
        inputValue.makeSymbolicValue(this_symbol, "Object")

        var info = new SymbolicInfo(false, Some(value), None, Some(inputValue), None, None)
        info.setType(STATEMENT)

        report = report :+ info

        // When parameter is object
        val thisProps = coverage.functions(env.uniqueName).getThisProperties
        for (prop <- thisProps) {
          var propValue = new SymbolicValue
          // Suppose types of properties could be only 'Number'
          propValue.makeSymbolicValue(obj2str(symbol + index, prop), "Number")
          symbolic_memory(obj2str("this", prop)) = propValue

          var propInputValue = new SymbolicValue
          propInputValue.makeSymbolicValue(obj2str(this_symbol, prop), "Number")

          var info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
          info.setType(STATEMENT)

          report = report :+ info
        }
        index += 1
      }
    }
  }

  /* Store new varaible when Global<>toNumber internally is called */
  def storeVariable(v: IRId, old: IRId) = {
    if (symbolic_memory.contains(old.uniqueName))
      symbolic_memory(v.uniqueName) = symbolic_memory(old.uniqueName)
  }

  def checkRecursiveCall(f: String, args: IRId): Boolean = coverage.functions.get(f) match {
    case Some(fun) =>
      val res = fun.checkRecursive(maxDepth)
      if (isRecursiveCall(f)) {
        recursive = argumentsMemory.get(args.uniqueName) match {
          case Some(sv) => sv
          case None => List()
        }
      }
      res
    case None => true
  }

  def checkLoop(loop: String): Boolean = {
    var res = true
    var depth = 0
    loopDepth.get(loop) match {
      case Some(n) =>
        if (n < maxDepth) depth = n + 1
        else res = false
      case None =>
    }
    loopDepth.put(loop, depth)
    res
  }

  /* HELPER FUNCTIONS */
  def storeObject(obj: String, ref: String) = objectMemory.put(obj, ref)
  def findObjectName(obj: IRId): Option[String] = {
    for (o <- objectMemory.keySet)
      if (objectMemory(o) == obj.uniqueName)
        return Some(o)
    return None
  }
  def obj2str(obj: String, prop: String): String = obj + "." + prop

  def createContext(v1: String, v2: String, op: IROp, res1: String, res2: String): String = {
    if (symbolic_memory.contains(v1) && symbolic_memory.contains(v2)) {
      // only if linear constraints supported
      if (op.kind == EJSMul ||
        op.kind == EJSDiv ||
        op.kind == EJSRem)
        return symbolic_memory(v1) + op.ast.toString() + res2
      else
        return symbolic_memory(v1) + op.ast.toString() + symbolic_memory(v2)
    } else if (symbolic_memory.contains(v1))
      return symbolic_memory(v1) + op.ast.toString() + res2
    else
      return symbolic_memory(v2) + op.ast.toString() + res1
  }
  def makeContext(v1: String, v2: String, op: IROp, c1: Option[SymbolicValue], c2: Option[SymbolicValue], contextType: Int): (Option[SymbolicValue], Option[SymbolicValue]) = {
    if (symbolic_memory.contains(v1) && symbolic_memory.contains(v2)) {
      // only if linear constraints supported
      if (op.kind == EJSMul ||
        op.kind == EJSDiv ||
        op.kind == EJSRem) {
        c2 match {
          case Some(concreteValue) =>
            val info = new SymbolicInfo(false, Some(symbolic_memory(v2)), None, c2, None, None)
            info.setType(contextType)
            report = report :+ info
          case None => throw new ConcolicError("Concrete value should exist.")
        }
        return (setInstanceType(symbolic_memory(v1), c1), c2)
      } else
        return (setInstanceType(symbolic_memory(v1), c1), setInstanceType(symbolic_memory(v2), c2))
    } else if (symbolic_memory.contains(v1))
      return (setInstanceType(symbolic_memory(v1), c1), c2)
    else
      return (c1, setInstanceType(symbolic_memory(v2), c2))
  }

  // TODO: Handle multiple type instance.
  def setInstanceType(symbol: SymbolicValue, concrete: Option[SymbolicValue]): Option[SymbolicValue] = {
    var result: Option[SymbolicValue] = None
    concrete match {
      case Some(concreteValue) =>
        symbol.setInstance(concreteValue.getInstance)
        result = Some(symbol)
      case None =>
        System.out.println("Concrete value doesn't match given symbolic value")
    }
    return result
  }

  def isRecursiveCall(f: String) = coverage.checkTarget(f) && coverage.functions(f).isRecursive

  def checkFocus(f: IRId) = coverage.checkTarget(f.uniqueName) && doConcolic
  // Flag for symbolic exeuction
  def startConcolic() = {
    doConcolic = true
  }
  def endConcolic() = {
    doConcolic = false
  }

  def toStr(expr: IRExpr): String = expr match {
    case IRBin(_, first, op, second) =>
      op.ast.toString() + toStr(expr)
    case IRLoad(_, obj, index) =>
      obj.originalName + "[" + toStr(index) + "]"
    case id: IRId => id.originalName
    case _: IRThis => "this"
    case n @ IRVal(EJSNumber(text, _)) => text
    case s @ IRVal(EJSString(str)) => str
    case b @ IRVal(EJSBool(bool)) => bool.toString
    case IRVal(EJSUndef) => "undefined"
    case IRVal(EJSNull) => "null"
  }

  def print() = {
    System.out.println("================== Symbolic Memory ===================")
    for ((key, value) <- symbolic_memory)
      System.out.println("%6s => value: %2s, type: %6s".format(key, value.getValue, value.getTypes))
    System.out.println("======================================================")
    System.out.println("================== Symbolic Report ===================")
    for (info <- report)
      System.out.println(info.toString)
    System.out.println("======================================================")
  }
}
