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

package kr.ac.kaist.safe.concolic

import scala.collection.mutable.HashMap
import _root_.java.util.{ List => JList }
import kr.ac.kaist.safe.errors.error.ConcolicError
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.util._

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
class SymbolicHelper(I: Interpreter) {
  private val symbol = "s"
  private val inputSymbol = "i"
  private val thisSymbol = "this"
  private var index, inputIndex = 0

  // Mapping between object declaration and object reference
  var objectMemory = new HashMap[String, String]
  // Mapping between arguments and symbolic value
  var argumentsMemory = new HashMap[String, List[Option[SymbolicValue]]]
  private val symbolicMemory = new HashMap[String, SymbolicValue]

  var recursive: List[Option[SymbolicValue]] = null

  // Mapping symbolic helper function to environment in which the function is defined
  var environments = new HashMap[String, IRId]
  def storeEnvironment(v: IRId, env: IRId): Unit = environments(v.uniqueName) = env
  def getEnvironment(v: IRId): IRId = environments(v.uniqueName)
  def ignoreCall(f: IRId): Boolean = environments.get(f.uniqueName) match {
    case Some(e) => e.uniqueName == "<>Concolic<>Main"
    case None => false //true
  }

  private var report = List[SymbolicInfo]()
  def getReport: List[SymbolicInfo] = report
  def addToReport(info: SymbolicInfo): Unit = {
    report :+= info
  }

  var coverage: Coverage = null

  val maxDepth = 3
  var loopDepth = new HashMap[String, Int]

  // Flag for concolic testing
  var doConcolic = false

  // Estimate time
  var startTime: Long = 0

  def initialize(cov: Coverage): Unit = {
    coverage = cov

    index = 0
    inputIndex = 0

    symbolicMemory.clear()
    objectMemory.clear()

    argumentsMemory.clear()
    recursive = List()
    coverage.functions.foreach({ case (_, finfo) => finfo.initDepth })

    loopDepth.clear()

    report = List[SymbolicInfo]()

    var target = coverage.target
    if (target != null && target.contains("@")) {
      target = target.substring(0, target.indexOf("@"))
    }
    System.out.println("Current target function: " + target)
  }

  // Initialize the symbolic memorys
  def walkVarStmt(id: IRId, n: Int, env: IRId): Unit = {
    if (checkFocus(env)) {
      if (recursive.nonEmpty) {
        recursive(n) match {
          // Only when an argument of a recursive call is not concrete value.
          case Some(sv) =>
            symbolicMemory(id.uniqueName) = sv
            coverage.functions(env.uniqueName).getObjectProperties(n) match {
              case Some(props) =>
                val constructors = coverage.functions(env.uniqueName).getObjectConstructors(n)
                if (constructors.head == "Array") {
                  val length: Int = props.head.toInt
                  for (p <- 0 until length) {
                    val propValue = new SymbolicValue(obj2str(sv.getValue.get, p.toString), "Number")
                    symbolicMemory(obj2str(id.uniqueName, p.toString)) = propValue
                  }
                } else {
                  for (prop <- props) {
                    // Suppose types of properties could be only 'Number'
                    val propValue = new SymbolicValue(obj2str(sv.getValue.get, prop), "Number")
                    symbolicMemory(obj2str(id.uniqueName, prop)) = propValue
                  }
                }
              case None =>
            }
          case None =>
            symbolicMemory -= id.uniqueName
        }
      } else {
        //TODO: Handle multiple types
        val ptype = coverage.functions(env.uniqueName).getType(n).head
        val value = new SymbolicValue(symbol + index, ptype)
        symbolicMemory(id.uniqueName) = value

        val inputValue = new SymbolicValue(inputSymbol + inputIndex, ptype)
        val info = new SymbolicInfo(false, Some(value), None, Some(inputValue), None, None)
        info.setType(SymbolicInfoTypes.statement)

        addToReport(info)

        // When parameter is object
        coverage.functions(env.uniqueName).getObjectProperties(n) match {
          case Some(props) =>
            val constructors = coverage.functions(env.uniqueName).getObjectConstructors(n)
            // Consider when an argument is an empty object.
            if (constructors.nonEmpty) {
              if (constructors.head == "Array") {
                val length = props.head.toInt
                for (p <- 0 until length) {
                  val propValue = new SymbolicValue(obj2str(symbol + index, p.toString), "Number")
                  symbolicMemory(obj2str(id.uniqueName, p.toString)) = propValue

                  val propInputValue = new SymbolicValue(obj2str(inputSymbol + inputIndex, p.toString), "Number")
                  val info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
                  info.setType(SymbolicInfoTypes.statement)

                  addToReport(info)
                }
              } else {
                for (prop <- props) {
                  // Suppose types of properties could be only 'Number'
                  val propValue = new SymbolicValue(obj2str(symbol + index, prop), "Number")
                  symbolicMemory(obj2str(id.uniqueName, prop)) = propValue

                  val propInputValue = new SymbolicValue(obj2str(inputSymbol + inputIndex, prop), "Number")
                  val info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
                  info.setType(SymbolicInfoTypes.statement)

                  addToReport(info)
                }
              }
            }
          case None =>
        }
        index += 1
        inputIndex += 1
      }
    }
  }

  /* If its environment, that is the function in which the 'executeAssignment' statement is instrumented is targeted,
   * the symbolic execution proceeds and its change is reported to build symbolic execution tree.
   * Otherwise, just report symbolic variables which don't represent local variables.
   * When local variables are in that expressions, just use concrete value instead of symbolic varialbes of local variables.
   */
  def executeAssignment(
    loc: String,
    id: IRId,
    expr: IRExpr,
    c: Option[SymbolicValue],
    c1: Option[SymbolicValue],
    c2: Option[SymbolicValue],
    env: IRId
  ): Unit = {
    if (checkFocus(env)) {
      expr match {
        /* variable op varialbe */
        //TODO: extend the range to cover all expressions, first and second
        case IRBin(_, first, op, second) => op.kind match {
          //TODO: find simple way to distinguish operation type
          // op is supported by the constraint solver
          case EJSInstOf => symbolicMemory -= id.uniqueName
          case EJSIn => symbolicMemory -= id.uniqueName
          case EJSShiftLeft => symbolicMemory -= id.uniqueName
          case EJSShiftSRight => symbolicMemory -= id.uniqueName
          case EJSShiftUSRight => symbolicMemory -= id.uniqueName
          case EJSBitXor => symbolicMemory -= id.uniqueName
          case EJSBitOr => symbolicMemory -= id.uniqueName
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
                  if (symbolicMemory.contains(v1.uniqueName) || symbolicMemory.contains(v2.uniqueName))
                    context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.statement)
                  else
                    symbolicMemory -= id.uniqueName
                case _ =>
                  if (symbolicMemory.contains(v1.uniqueName)) {
                    context = (setInstanceType(symbolicMemory(v1.uniqueName), c1), c2)
                  }
              }
              case v1: IRLoad => findObjectName(v1.obj) match {
                case Some(o1) =>
                  val id1 = o1 + "." + v1.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                  second match {
                    case v2: IRId =>
                      if (symbolicMemory.contains(id1) || symbolicMemory.contains(v2.uniqueName))
                        context = makeContext(id1, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.statement)
                      else
                        symbolicMemory -= id.uniqueName
                    case v2: IRLoad => findObjectName(v2.obj) match {
                      case Some(o2) =>
                        val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolicMemory.contains(id1) || symbolicMemory.contains(id2))
                          context = makeContext(id1, id2, op, c1, c2, SymbolicInfoTypes.statement)
                        else
                          symbolicMemory -= id.uniqueName
                      case None =>
                        System.out.println("1 The object should be in object memory.")
                    }
                    case _ =>
                      if (symbolicMemory.contains(id1))
                        context = (setInstanceType(symbolicMemory(id1), c1), c2)
                  }
                case None =>
                  System.out.println("2 The object should be in object memory.")
              }
              case _ => second match {
                case v2: IRLoad => findObjectName(v2.obj) match {
                  case Some(o2) =>
                    val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                    if (symbolicMemory.contains(id2))
                      context = (c1, setInstanceType(symbolicMemory(id2), c2))
                  case None =>
                    System.out.println("3 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolicMemory.contains(v2.uniqueName))
                    context = (c1, setInstanceType(symbolicMemory(v2.uniqueName), c2))
                case _ =>
              }
            }
            if (context != null) {
              c match {
                case Some(value) =>
                  var sid = new SymbolicValue(symbol + index, value.getTypes)
                  symbolicMemory(id.uniqueName) = sid
                  index += 1
                  val (optSymVal1, optSymVal2) = context
                  val info = new SymbolicInfo(false, Some(sid), Some(op.name), optSymVal1, optSymVal2, None)
                  info.setType(SymbolicInfoTypes.statement)
                  addToReport(info)
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
        }
        case IRUn(_, op, expr) =>
        case ir @ IRLoad(info, obj, anotherIndex) => findObjectName(obj) match {
          case Some(o) =>
            val indexValue = anotherIndex match {
              case id: IRId => id.uniqueName
              case IRVal(EJSString(str)) => str
              case IRVal(EJSNumber(text, num)) => text
            }
            val v = o + "." + indexValue
            if (symbolicMemory.contains(v)) {
              c match {
                case Some(value) =>
                  val sid = new SymbolicValue(symbol + index, value.getTypes)
                  symbolicMemory(id.uniqueName) = sid
                  index += 1
                  val info = new SymbolicInfo(false, Some(sid), None, Some(symbolicMemory(v)), None, None)
                  info.setType(SymbolicInfoTypes.statement)
                  addToReport(info)
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
          case None =>
        }
        case v: IRId =>
          if (symbolicMemory.contains(v.uniqueName))
            symbolicMemory(id.uniqueName) = symbolicMemory(v.uniqueName)
          // Do not need to report because symbolic memory is replaced
          else
            symbolicMemory -= id.uniqueName
        case _: IRThis =>
        case n @ IRVal(EJSNumber(_, _)) =>
          symbolicMemory -= id.uniqueName
        case s @ IRVal(EJSString(_)) =>
        case b @ IRVal(EJSBool(_)) =>
        case IRVal(EJSUndef) =>
        case IRVal(EJSNull) =>
      }
    }
  }

  def executeCondition(
    expr: IRExpr,
    branchTaken: Option[Boolean],
    c1: Option[SymbolicValue],
    c2: Option[SymbolicValue],
    env: IRId
  ): Unit = {
    // TODO: need rewriter to modify the expressions syntactically accepted
    // to the expressions supported by symbolic helper
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
                  if (symbolicMemory.contains(v1.uniqueName) || symbolicMemory.contains(v2.uniqueName)) {
                    //context = createContext(v1.uniqueName, v2.uniqueName, op, res1, res2)
                    context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.branch)
                  }
                case _ =>
                  if (symbolicMemory.contains(v1.uniqueName))
                    //context = symbolicMemory(v1.uniqueName) + op.getText + res2
                    context = (setInstanceType(symbolicMemory(v1.uniqueName), c1), c2)
              }
              case IRLoad(_, obj, index) => findObjectName(obj) match {
                case Some(o) =>
                  val id1 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                  second match {
                    case v2: IRId =>
                      if (symbolicMemory.contains(id1) || symbolicMemory.contains(v2.uniqueName))
                        //context = createContext(id1, v2.uniqueName, op, res1, res2)
                        context = makeContext(id1, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.branch)
                    case IRLoad(_, obj, index) => findObjectName(obj) match {
                      case Some(o) =>
                        val id2 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolicMemory.contains(id1) || symbolicMemory.contains(id2))
                          //context = createContext(id1, id2, op, res1, res2)
                          context = makeContext(id1, id2, op, c1, c2, SymbolicInfoTypes.branch)

                      case None =>
                        System.out.println("5 The object should be in object memory.")
                    }
                    case _ =>
                      if (symbolicMemory.contains(id1))
                        //context = symbolicMemory(id1) + op.getText + res2
                        context = (setInstanceType(symbolicMemory(id1), c1), c2)
                  }
                case None =>
                  System.out.println("6 The object should be in object memory.")
              }
              case _ => second match {
                case IRLoad(_, obj, index) => findObjectName(obj) match {
                  case Some(o) =>
                    val id2 = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                    if (symbolicMemory.contains(id2))
                      //context = symbolicMemory(id2) + op.getText + res1
                      context = (c1, setInstanceType(symbolicMemory(id2), c2))
                  case None =>
                    System.out.println("7 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolicMemory.contains(v2.uniqueName))
                    //context = symbolicMemory(v2.uniqueName) + op.getText + res1
                    context = (c1, setInstanceType(symbolicMemory(v2.uniqueName), c2))
              }
            }
            if (context != null) {
              val (optSymVal1, optSymVal2) = context
              val info = new SymbolicInfo(true, None, Some(op.name), optSymVal1, optSymVal2, branchTaken)
              info.setType(SymbolicInfoTypes.branch)
              addToReport(info)
            }
        }
        case IRUn(_, op, e) => {
          e match {
            case v: IRId => if (symbolicMemory.contains(v.uniqueName)) {
              val operation: Option[String] = op.kind match {
                case EJSPos => Some("!=")
                case EJSNeg => Some("!=")
                case EJSLogNot => Some("==")
                case _ => None
              }
              if (operation.isDefined) {
                val c = new SymbolicValue("0", "Number")
                val info = new SymbolicInfo(true, None, operation, Some(symbolicMemory(v.uniqueName)), Some(c), branchTaken)
                info.setType(SymbolicInfoTypes.branch)
                addToReport(info)
              }
            }
          }
        }
        case v: IRId =>
          if (symbolicMemory.contains(v.uniqueName)) {
            val c = new SymbolicValue("0", "Number")
            val info = new SymbolicInfo(true, None, Some("!="), Some(symbolicMemory(v.uniqueName)), Some(c), branchTaken)
            info.setType(SymbolicInfoTypes.branch)
            addToReport(info)
          }
        case IRLoad(info, obj, index) => findObjectName(obj) match {
          case Some(o) =>
            val id = o + "." + index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
            if (symbolicMemory.contains(id)) {
              val info = new SymbolicInfo(true, None, None, Some(symbolicMemory(id)), None, branchTaken)
              info.setType(SymbolicInfoTypes.branch)
              addToReport(info)
            }
          case None =>
            System.out.println("8 The object should be in object memory.")
        }
        case IRVal(EJSBool(_)) =>
      }
    }
  }

  def endCondition(env: IRId): Unit = {
    if (checkFocus(env)) {
      val info = new SymbolicInfo(true, None, None, None, None, None)
      info.setType(SymbolicInfoTypes.endBranch)
      addToReport(info)
    }
  }

  def executeStore(
    obj: IRId,
    prop: String,
    expr: IRExpr,
    c: Option[SymbolicValue],
    c1: Option[SymbolicValue],
    c2: Option[SymbolicValue],
    env: IRId
  ): Unit = {
    if (checkFocus(env)) {
      findObjectName(obj) match {
        case Some(o) =>
          val id = o + "." + prop
          expr match {
            //TODO: extend the range to cover all expressions, first and second
            case IRBin(_, first, op, second) => op.kind match {
              //TODO: find simple way to distinguish operation type
              // op is supported by the constraint solver
              case EJSInstOf => symbolicMemory -= id
              case EJSIn => symbolicMemory -= id
              case EJSShiftLeft => symbolicMemory -= id
              case EJSShiftSRight => symbolicMemory -= id
              case EJSShiftUSRight => symbolicMemory -= id
              case EJSSEq => symbolicMemory -= id
              case EJSSNEq => symbolicMemory -= id
              case EJSBitAnd => symbolicMemory -= id
              case EJSBitXor => symbolicMemory -= id
              case EJSBitOr => symbolicMemory -= id
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
                      if (symbolicMemory.contains(v1.uniqueName) || symbolicMemory.contains(v2.uniqueName))
                        context = makeContext(v1.uniqueName, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.statement)
                      else
                        symbolicMemory -= id
                    case _ =>
                      if (symbolicMemory.contains(v1.uniqueName))
                        //context = symbolicMemory(v1.uniqueName) + op.getText + res2
                        context = (setInstanceType(symbolicMemory(v1.uniqueName), c1), c2)
                  }
                  case v1: IRLoad => findObjectName(v1.obj) match {
                    case Some(o1) =>
                      val id1 = o1 + "." + v1.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                      second match {
                        case v2: IRId =>
                          if (symbolicMemory.contains(id1) || symbolicMemory.contains(v2.uniqueName))
                            //context = createContext(id1, v2.uniqueName, op, res1, res2)
                            context = makeContext(id1, v2.uniqueName, op, c1, c2, SymbolicInfoTypes.statement)
                          else
                            symbolicMemory -= id
                        case v2: IRLoad => findObjectName(v2.obj) match {
                          case Some(o2) =>
                            val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                            if (symbolicMemory.contains(id1) || symbolicMemory.contains(id2))
                              //context = createContext(id1, id2, op, res1, res2)
                              context = makeContext(id1, id2, op, c1, c2, SymbolicInfoTypes.statement)
                            else
                              symbolicMemory -= id
                          case None =>
                            System.out.println("9 The object should be in object memory.")
                        }
                        case _ =>
                          if (symbolicMemory.contains(id1))
                            //context = symbolicMemory(id1) + op.getText + res2
                            context = (setInstanceType(symbolicMemory(id1), c1), c2)
                      }
                    case None =>
                      System.out.println("10 The object should be in object memory.")
                  }
                  case _ => second match {
                    case v2: IRLoad => findObjectName(v2.obj) match {
                      case Some(o2) =>
                        val id2 = o2 + "." + v2.index.asInstanceOf[IRVal].value.asInstanceOf[EJSString].str
                        if (symbolicMemory.contains(id2))
                          //context = symbolicMemory(id2) + op.getText + res1
                          context = (c1, setInstanceType(symbolicMemory(id2), c2))
                      case None =>
                        System.out.println("11 The object should be in object memory.")
                    }
                    case v2: IRId =>
                      if (symbolicMemory.contains(v2.uniqueName))
                        //context = symbolicMemory(v2.uniqueName) + op.getText + res1
                        context = (c1, setInstanceType(symbolicMemory(v2.uniqueName), c2))
                  }
                }
                //if (!context.isEmpty) {
                if (context != null) {
                  //val sid = symbol + index
                  c match {
                    case Some(value) =>
                      val sid = new SymbolicValue(symbol + index, value.getTypes)
                      symbolicMemory(id) = sid
                      index += 1
                      val (optSymVal1, optSymVal2) = context
                      val info = new SymbolicInfo(false, Some(sid), Some(op.name), optSymVal1, optSymVal2, None)
                      info.setType(SymbolicInfoTypes.statement)
                      addToReport(info)
                    case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
                  }
                }
            }
            case IRUn(_, op, expr) =>
            case IRLoad(_, obj, index) =>
            /* variable */
            case v: IRId =>
              if (symbolicMemory.contains(v.uniqueName))
                symbolicMemory(id) = symbolicMemory(v.uniqueName)
              // Do not need to report because symbolic memory is replaced
              else
                symbolicMemory -= id
            case _: IRThis =>
            /* constant value */
            case n @ IRVal(EJSNumber(_, _)) =>
              symbolicMemory -= id
            case s @ IRVal(EJSString(_)) =>
            case b @ IRVal(EJSBool(_)) =>
            case IRVal(EJSUndef) =>
            case IRVal(EJSNull) =>
          }
        case None =>
      }
    }
  }

  def storeArguments(args: IRId, elems: List[Option[IRExpr]]): Unit = {
    var arguments = List[Option[SymbolicValue]]()
    for (elem <- elems) {
      if (elem.isDefined) {
        val sv = symbolicMemory.get(elem.get.asInstanceOf[IRId].uniqueName)
        arguments = arguments :+ sv
      }
    }
    if (arguments.nonEmpty) {
      argumentsMemory(args.uniqueName) = arguments
    }
  }

  def storeThis(env: IRId): Unit = {
    if (checkFocus(env) && env.uniqueName.contains("prototype")) {
      // tokenize target name to generate each object and function.
      val target = env.uniqueName
      val token = target.substring(0, target.indexOf("<")).split("prototype")
      if (token.length > 2) throw new ConcolicError("Only a.prototype.x function form is supported")
      val first = token(0).substring(0, token(0).length - 1)
      val second = token(1).substring(1, token(1).length)

      val value = new SymbolicValue(symbol + index, "Object")
      symbolicMemory("this") = value

      val inputValue = new SymbolicValue(thisSymbol, "Object")

      val info = new SymbolicInfo(false, Some(value), None, Some(inputValue), None, None)
      info.setType(SymbolicInfoTypes.statement)

      addToReport(info)

      // When parameter is object
      val thisProps: List[String] = coverage.functions(env.uniqueName).getThisProperties
      for (prop <- thisProps) {
        // Suppose types of properties could be only 'Number'
        val propValue = new SymbolicValue(obj2str(symbol + index, prop), "Number")
        symbolicMemory(obj2str("this", prop)) = propValue

        val propInputValue = new SymbolicValue(obj2str(thisSymbol, prop), "Number")

        val info = new SymbolicInfo(false, Some(propValue), None, Some(propInputValue), None, None)
        info.setType(SymbolicInfoTypes.statement)

        addToReport(info)
      }
      index += 1
    }
  }

  /* Store new varaible when Global<>toNumber internally is called */
  def storeVariable(v: IRId, old: IRId): Unit = {
    if (symbolicMemory.contains(old.uniqueName)) {
      symbolicMemory(v.uniqueName) = symbolicMemory(old.uniqueName)
    }
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
    case None =>
      true
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
  def storeObject(obj: String, ref: String): Unit = objectMemory.put(obj, ref)
  def findObjectName(obj: IRId): Option[String] = {
    for (o <- objectMemory.keySet)
      if (objectMemory(o) == obj.uniqueName)
        return Some(o)
    None
  }
  def obj2str(obj: String, prop: String): String = obj + "." + prop

  def createContext(
    v1: String,
    v2: String,
    op: IROp,
    res1: String,
    res2: String
  ): String = {
    if (symbolicMemory.contains(v1) && symbolicMemory.contains(v2)) {
      // only if linear constraints supported
      if (op.kind == EJSMul ||
        op.kind == EJSDiv ||
        op.kind == EJSRem)
        symbolicMemory(v1) + op.ast.toString() + res2
      else
        symbolicMemory(v1) + op.ast.toString() + symbolicMemory(v2)
    } else if (symbolicMemory.contains(v1))
      symbolicMemory(v1) + op.ast.toString() + res2
    else
      symbolicMemory(v2) + op.ast.toString() + res1
  }
  def makeContext(
    v1: String,
    v2: String,
    op: IROp,
    c1: Option[SymbolicValue],
    c2: Option[SymbolicValue],
    contextType: SymbolicInfoTypes.Type
  ): (Option[SymbolicValue], Option[SymbolicValue]) = {
    if (symbolicMemory.contains(v1) && symbolicMemory.contains(v2)) {
      // only if linear constraints supported
      if (op.kind == EJSMul ||
        op.kind == EJSDiv ||
        op.kind == EJSRem) {
        c2 match {
          case Some(concreteValue) =>
            val info = new SymbolicInfo(false, Some(symbolicMemory(v2)), None, c2, None, None)
            info.setType(contextType)
            addToReport(info)
          case None => throw new ConcolicError("Concrete value should exist.")
        }
        (setInstanceType(symbolicMemory(v1), c1), c2)
      } else
        (setInstanceType(symbolicMemory(v1), c1), setInstanceType(symbolicMemory(v2), c2))
    } else if (symbolicMemory.contains(v1))
      (setInstanceType(symbolicMemory(v1), c1), c2)
    else
      (c1, setInstanceType(symbolicMemory(v2), c2))
  }

  // TODO: Handle multiple type instance.
  def setInstanceType(symbol: SymbolicValue, concrete: Option[SymbolicValue]): Option[SymbolicValue] = {
    var result: Option[SymbolicValue] = None
    concrete match {
      case Some(concreteValue) =>
        symbol.setInstance(concreteValue.getInstance.get)
        result = Some(symbol)
      case None =>
        System.out.println("Concrete value doesn't match given symbolic value")
    }
    return result
  }

  def isRecursiveCall(f: String): Boolean = coverage.checkTarget(f) && coverage.functions(f).isRecursive

  def checkFocus(f: IRId): Boolean = doConcolic && coverage.checkTarget(f.uniqueName)
  // Flag for symbolic execution
  def startConcolic(): Unit = {
    doConcolic = true
  }
  def endConcolic(): Unit = {
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

  def print(): Unit = {
    System.out.println("================== Symbolic Memory ===================")
    for ((key, value) <- symbolicMemory)
      System.out.println("%6s => value: %2s, type: %6s".format(key, value.getValue, value.getTypes))
    System.out.println("======================================================")
    System.out.println("================== Symbolic Report ===================")
    for (info <- getReport)
      System.out.println(info.toString)
    System.out.println("======================================================")
  }
}
