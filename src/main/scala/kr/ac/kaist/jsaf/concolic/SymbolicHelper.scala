/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.math.BigInteger
import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.exceptions.ConcolicError
import kr.ac.kaist.jsaf.interpreter.Interpreter
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{Coverage, EJSOp, NodeRelation}
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF, NodeUtil => NU, NodeFactory => NF}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

import scala.collection.mutable.HashMap
import scala.util.Random
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
  val symbol = "s"
  val input_symbol = "i"
  val this_symbol = "this"
  var index, input_index = 0

  // Mapping between object declaration and object reference
  var objectMemory = new HashMap[String, List[String]] 
  //val symbolic_memory = new HashMap[String, String]
  val symbolic_memory = new HashMap[String, SymbolicValue]
  
  // Mapping symbolic helper function to environment in which the function is defined
  var environments = new HashMap[String, IRId]
  def storeEnvironment(v: IRId, env: IRId) = environments(v.getUniqueName) = env
  def getEnvironment(v: IRId) = environments(v.getUniqueName)
  def ignoreCall(f: IRId) = {println("ignoreCall "+f.getUniqueName);environments.get(f.getUniqueName) match { 
    case Some(e) => println(" environment "+e.getUniqueName); e.getUniqueName == "<>Concolic<>Main"  
    case None => println(" no env");false//true 
  }
  }
  
  var report = List[Info]()
  var coverage: Coverage = null
  
  var max_depth = 3
  var depth = 0

  // Flag for concolic testing
  var doConcolic = false

  def initialize(cov: Coverage) = {
    index = 0
    input_index = 0
    symbolic_memory.clear()
    report = List[Info]()

    coverage = cov
    System.out.println
    var target = coverage.target
    if (target != null && target.contains("@"))
      target = target.substring(0, target.indexOf("@"))
    System.out.println("Current target function: "+ target)
  }

  def storeThis(env: IRId) = {
    if (checkFocus(env)) {
      if (env.getUniqueName.contains("prototype")) {
        // tokenize target name to generate each object and function.
        val target = env.getUniqueName
        val token = target.substring(0, target.indexOf("<")).split("prototype")
        if (token.length > 2) throw new ConcolicError("Only a.prototype.x function form is supported") 
        val first = token(0).substring(0, token(0).length-1)
        val second = token(1).substring(1, token(1).length) 

        var value = new SymbolicValue
        value.makeSymbolicValue(symbol+index, "Object")
        symbolic_memory("this") = value 

        var inputValue = new SymbolicValue
        inputValue.makeSymbolicValue(this_symbol, "Object")

        var info = new Info(false, Some(value), None, Some(inputValue), None, None)
      
        report = report:+info

      // When parameter is object
        val thisProps = coverage.functions(env.getUniqueName).thisObject
        for (prop <- thisProps) { 
          var propValue = new SymbolicValue
          propValue.makeSymbolicValue(obj2str(symbol+index, prop._1), prop._2)
          symbolic_memory(obj2str("this", prop._1)) = propValue 

          var propInputValue = new SymbolicValue
          propInputValue.makeSymbolicValue(obj2str(this_symbol, prop._1), prop._2)

          var info = new Info(false, Some(propValue), None, Some(propInputValue), None, None)

          report = report:+info
        }
        index += 1
      }
    }
  }

  // Initialize the symbolic memorys
  def walkVarStmt(id: IRId, n: Int, env: IRId) = {
    if (checkFocus(env)) {
      var params = coverage.functions(env.getUniqueName).params
      var value = new SymbolicValue
      value.makeSymbolicValue(symbol+index, params(n))
      symbolic_memory(id.getUniqueName) = value 

      var inputValue = new SymbolicValue
      inputValue.makeSymbolicValue(input_symbol+input_index, params(n))
      var info = new Info(false, Some(value), None, Some(inputValue), None, None)
      
      report = report:+info

      // When parameter is object
      val objs = coverage.functions(env.getUniqueName).objects
      if (objs.contains(n)) {
        if (objs(n)._1 == "Array") {
          val elemType = objs(n)._2(0)._2
          val length = objs(n)._2(0)._1.toInt
          for (p <- 0 until length) {
            var propValue = new SymbolicValue
            propValue.makeSymbolicValue(obj2str(symbol+index, p.toString), elemType)
            symbolic_memory(obj2str(id.getUniqueName, p.toString)) = propValue 

            var propInputValue = new SymbolicValue
            propInputValue.makeSymbolicValue(obj2str(input_symbol+input_index, p.toString), elemType)
            var info = new Info(false, Some(propValue), None, Some(propInputValue), None, None)

            report = report:+info
          }
        }
        else {
          for (prop <- objs(n)._2) { 
            var propValue = new SymbolicValue
            propValue.makeSymbolicValue(obj2str(symbol+index, prop._1), prop._2)
            symbolic_memory(obj2str(id.getUniqueName, prop._1)) = propValue 

            var propInputValue = new SymbolicValue
            propInputValue.makeSymbolicValue(obj2str(input_symbol+input_index, prop._1), prop._2)
            var info = new Info(false, Some(propValue), None, Some(propInputValue), None, None)

            report = report:+info
          }
        }
      }   

      index += 1
      input_index += 1
    }
  }

  /* Store new varaible when Global<>toNumber internally is called */
  def storeVariable(v: IRId, old: IRId) = {
    if (symbolic_memory.contains(old.getUniqueName))
      symbolic_memory(v.getUniqueName) = symbolic_memory(old.getUniqueName)
  }
    
  /* If its environment, that is the function in which the 'executeAssignment' statement is instrumented is targeted, 
   * the symbolic execution proceeds and its change is reported to build symbolic execution tree. 
   * Otherwise, just report symbolic variables which don't represent local variables. 
   * When local variables are in that expressions, just use concrete value instead of symbolic varialbes of local variables. 
   */
  def executeAssignment(loc: String, id: IRId, expr: IRExpr, c: Option[SymbolicValue], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = { 
    if (checkFocus(env)) { 
      //System.out.println("Symbolic Helper, assignment: "+new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(expr).doit);
      expr match {
        /* variable op varialbe */
        //TODO: extend the range to cover all expressions, first and second
        case SIRBin(_, first, op, second) => op.getKind match {
          //TODO: find simple way to distinguish operation type 
          // op is supported by the constraint solver
          case EJSOp.BIN_COMP_REL_INSTANCEOF => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_COMP_REL_IN => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_BIT_SHIFT_LEFT => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_BIT_SHIFT_SRIGHT => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_BIT_SHIFT_USRIGHT => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_BIT_BIT_XOR => symbolic_memory -= id.getUniqueName
          case EJSOp.BIN_BIT_BIT_OR => symbolic_memory -= id.getUniqueName
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
                  if (symbolic_memory.contains(v1.getUniqueName) || symbolic_memory.contains(v2.getUniqueName))
                    context = makeContext(v1.getUniqueName, v2.getUniqueName, op, c1, c2) 
                  else
                    symbolic_memory -= id.getUniqueName
                case _ =>
                  if (symbolic_memory.contains(v1.getUniqueName)) {
                    context = (setInstanceType(symbolic_memory(v1.getUniqueName), c1), c2)
                  }
              }
              case v1: IRLoad => findObjectName(v1.getObj) match {
                case Some(o1) =>
                  val id1 = o1 + "." + v1.getIndex.asInstanceOf[IRString].getStr
                  second match {
                    case v2: IRId =>
                      if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.getUniqueName))
                        context = makeContext(id1, v2.getUniqueName, op, c1, c2) 
                      else
                        symbolic_memory -= id.getUniqueName
                    case v2: IRLoad => findObjectName(v2.getObj) match {
                      case Some(o2) => 
                        val id2 = o2 + "." + v2.getIndex.asInstanceOf[IRString].getStr
                        if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                          context = makeContext(id1, id2, op, c1, c2) 
                        else
                          symbolic_memory -= id.getUniqueName
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
                case v2: IRLoad => findObjectName(v2.getObj) match {
                  case Some(o2) => 
                    val id2 = o2 + "." + v2.getIndex.asInstanceOf[IRString].getStr
                    if (symbolic_memory.contains(id2))
                      context = (c1, setInstanceType(symbolic_memory(id2), c2))
                  case None =>
                    System.out.println("3 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolic_memory.contains(v2.getUniqueName)) 
                    context = (c1, setInstanceType(symbolic_memory(v2.getUniqueName), c2))
              }
            }
            if (context != null) {
              c match {
                case Some(value) =>
                  var sid = new SymbolicValue
                  sid.makeSymbolicValue(symbol+index, value.getTypes)
                  symbolic_memory(id.getUniqueName) = sid
                  index += 1
                  val info = new Info(false, Some(sid), Some(op.getText), context._1, context._2, None) 
                  report = report:+info
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
        }
        case SIRUn(_, op, expr) =>
        case ir@SIRLoad(info, obj, _index) => findObjectName(obj) match {
          case Some(o) =>
            val indexValue = _index match {
              case id: IRId => id.getUniqueName
              case SIRString(_, str) => str
              case SIRNumber(_, text, num) => text
            }
            val v = o + "." + indexValue
            if (symbolic_memory.contains(v)) {
              c match {
                case Some(value) =>
                  var sid = new SymbolicValue
                  sid.makeSymbolicValue(symbol+index, value.getTypes)
                  symbolic_memory(id.getUniqueName) = sid
                  index += 1
                  val info = new Info(false, Some(sid), None, Some(symbolic_memory(v)), None, None)
                  report = report:+info
                case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
              }
            }
          case None => 
        }
        case v: IRId =>
          if (symbolic_memory.contains(v.getUniqueName)) 
            symbolic_memory(id.getUniqueName) = symbolic_memory(v.getUniqueName)
            // Do not need to report because symbolic memory is replaced
          else
            symbolic_memory -= id.getUniqueName
        case _:IRThis =>
        case n:IRNumber =>
          symbolic_memory -= id.getUniqueName
        case s:IRString =>
        case b:IRBool =>
        case _:IRUndef =>
        case _:IRNull =>
      }
    }
  }
  
  def executeCondition(expr: IRExpr, branchTaken: Option[Boolean], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = {
  //TODO: need rewriter to modify the expressions syntatically accepted to the expressions supported by symbolic helper
    if (checkFocus(env)) {
      //System.out.println("Symbolic Helper, condition: "+new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(expr).doit);
      expr match {
        case SIRBin(_, first, op, second) => op.getKind match {
          //TODO: find simple way to distinguish operation type 
          case EJSOp.BIN_COMP_REL_INSTANCEOF => 
          case EJSOp.BIN_COMP_REL_IN =>
          case EJSOp.BIN_ARITH_MUL_MULTIPLICATION => 
          case EJSOp.BIN_ARITH_MUL_DIVISION => 
          case EJSOp.BIN_ARITH_MUL_REMINDER => 
          case EJSOp.ETC_PLUS => 
          case EJSOp.ETC_MINUS => 
          case EJSOp.BIN_BIT_SHIFT_LEFT => 
          case EJSOp.BIN_BIT_SHIFT_SRIGHT => 
          case EJSOp.BIN_BIT_SHIFT_USRIGHT => 
          case EJSOp.BIN_BIT_BIT_AND => 
          case EJSOp.BIN_BIT_BIT_XOR => 
          case EJSOp.BIN_BIT_BIT_OR => 
          //TODO: construct branch bitvector
          case _ => 
            //TODO: construct branch bitvector
            var context: (Option[SymbolicValue], Option[SymbolicValue]) = null
            first match {
              case v1: IRId => second match {
                case v2: IRId => 
                  if (symbolic_memory.contains(v1.getUniqueName) || symbolic_memory.contains(v2.getUniqueName)) {
                    //context = createContext(v1.getUniqueName, v2.getUniqueName, op, res1, res2)
                    context = makeContext(v1.getUniqueName, v2.getUniqueName, op, c1, c2) 
                  }
                case _ =>
                  if (symbolic_memory.contains(v1.getUniqueName))
                    //context = symbolic_memory(v1.getUniqueName) + op.getText + res2
                    context = (setInstanceType(symbolic_memory(v1.getUniqueName), c1), c2)
              }
              case SIRLoad(_, obj, index) => System.out.println("hi");findObjectName(obj) match {
                case Some(o) =>
                  val id1 = o + "." + index.asInstanceOf[IRString].getStr
                  second match {
                    case v2: IRId =>
                      if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.getUniqueName))
                        //context = createContext(id1, v2.getUniqueName, op, res1, res2)
                        context = makeContext(id1, v2.getUniqueName, op, c1, c2) 
                    case SIRLoad(_, obj, index) => findObjectName(obj) match {
                      case Some(o) =>
                        val id2 = o + "." + index.asInstanceOf[IRString].getStr
                        if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                          //context = createContext(id1, id2, op, res1, res2)
                          context = makeContext(id1, id2, op, c1, c2) 
                          
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
                case SIRLoad(_, obj, index) => findObjectName(obj) match {
                  case Some(o) =>
                    val id2 = o + "." + index.asInstanceOf[IRString].getStr
                    if (symbolic_memory.contains(id2))
                      //context = symbolic_memory(id2) + op.getText + res1
                      context = (c1, setInstanceType(symbolic_memory(id2), c2))
                  case None =>
                    System.out.println("7 The object should be in object memory")
                }
                case v2: IRId =>
                  if (symbolic_memory.contains(v2.getUniqueName)) 
                    //context = symbolic_memory(v2.getUniqueName) + op.getText + res1
                    context = (c1, setInstanceType(symbolic_memory(v2.getUniqueName), c2))
              }
            }
            //if (!context.isEmpty) {
            if (context != null) {
              //val info = new Info(true, "", Some(op.getText), context, branchTaken)
              val info = new Info(true, None, Some(op.getText), context._1, context._2, branchTaken)
              report = report:+info
            }
        }
        case SIRUn(_, op, e) => e match {
          case v: IRId => if (symbolic_memory.contains(v.getUniqueName)) {
            val operation: Option[String] = op.getKind match {
              case EJSOp.ETC_PLUS => Some("!=")
              case EJSOp.ETC_MINUS => Some("!=")
              case EJSOp.UN_BIT_LOG_NOT => Some("==")
              case _ => None
            }
            if (operation.isSome) {
              val c = new SymbolicValue
              c.makeSymbolicValue("0","Number")
              val info = new Info(true, None, operation, Some(symbolic_memory(v.getUniqueName)), Some(c), branchTaken)
              report = report:+info
            }
          }
        }
        case v: IRId =>
          if (symbolic_memory.contains(v.getUniqueName)) {
            //val info = new Info(true, "", None, symbolic_memory(v.getUniqueName), branchTaken)
            val c = new SymbolicValue
            c.makeSymbolicValue("0","Number")
            val info = new Info(true, None, Some("!="), Some(symbolic_memory(v.getUniqueName)), Some(c), branchTaken)
            //val info = new Info(true, None, None, Some(symbolic_memory(v.getUniqueName)), None, branchTaken)
            report = report:+info
          }
        case SIRLoad(info, obj, index) => findObjectName(obj) match {
          case Some(o) =>
            val id = o+"."+index.asInstanceOf[IRString].getStr
            if (symbolic_memory.contains(id)) {
              //val info = new Info(true, "", None, symbolic_memory(id), branchTaken)
              val info = new Info(true, None, None, Some(symbolic_memory(id)), None, branchTaken)
              report = report:+info
            }
          case None =>
            System.out.println("8 The object should be in object memory.")
        }
      }
    }
  }

  def executeStore(obj: IRId, prop: String, expr: IRExpr, c: Option[SymbolicValue], c1: Option[SymbolicValue], c2: Option[SymbolicValue], env: IRId) = {
    if (checkFocus(env)) {
      findObjectName(obj) match {
        case Some(o) =>
          val id = o+"."+prop
          expr match {
            //TODO: extend the range to cover all expressions, first and second
            case SIRBin(_, first, op, second) => op.getKind match {
              //TODO: find simple way to distinguish operation type 
              // op is supported by the constraint solver
              case EJSOp.BIN_COMP_REL_INSTANCEOF => symbolic_memory -= id
              case EJSOp.BIN_COMP_REL_IN => symbolic_memory -= id
              case EJSOp.BIN_BIT_SHIFT_LEFT => symbolic_memory -= id
              case EJSOp.BIN_BIT_SHIFT_SRIGHT => symbolic_memory -= id
              case EJSOp.BIN_BIT_SHIFT_USRIGHT => symbolic_memory -= id
              case EJSOp.BIN_COMP_EQ_SEQUAL => symbolic_memory -= id
              case EJSOp.BIN_COMP_EQ_SNEQUAL => symbolic_memory -= id
              case EJSOp.BIN_BIT_BIT_AND => symbolic_memory -= id
              case EJSOp.BIN_BIT_BIT_XOR => symbolic_memory -= id
              case EJSOp.BIN_BIT_BIT_OR => symbolic_memory -= id
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
                      if (symbolic_memory.contains(v1.getUniqueName) || symbolic_memory.contains(v2.getUniqueName))
                        //context = createContext(v1.getUniqueName, v2.getUniqueName, op, res1, res2)
                        context = makeContext(v1.getUniqueName, v2.getUniqueName, op, c1, c2) 
                      else
                        symbolic_memory -= id
                    case _ => 
                      if (symbolic_memory.contains(v1.getUniqueName))
                        //context = symbolic_memory(v1.getUniqueName) + op.getText + res2
                        context = (setInstanceType(symbolic_memory(v1.getUniqueName), c1), c2)
                  }
                  case v1: IRLoad => findObjectName(v1.getObj) match {
                    case Some(o1) => 
                      val id1 = o1 + "." + v1.getIndex.asInstanceOf[IRString].getStr
                      second match {
                        case v2: IRId =>
                          if (symbolic_memory.contains(id1) || symbolic_memory.contains(v2.getUniqueName))
                            //context = createContext(id1, v2.getUniqueName, op, res1, res2)
                            context = makeContext(id1, v2.getUniqueName, op, c1, c2) 
                          else
                            symbolic_memory -= id
                        case v2: IRLoad => findObjectName(v2.getObj) match {
                          case Some(o2) => 
                            val id2 = o2 + "." + v2.getIndex.asInstanceOf[IRString].getStr
                            if (symbolic_memory.contains(id1) || symbolic_memory.contains(id2))
                              //context = createContext(id1, id2, op, res1, res2)
                              context = makeContext(id1, id2, op, c1, c2) 
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
                    case v2: IRLoad => findObjectName(v2.getObj) match {
                      case Some(o2) => 
                        val id2 = o2 + "." + v2.getIndex.asInstanceOf[IRString].getStr
                        if (symbolic_memory.contains(id2))
                          //context = symbolic_memory(id2) + op.getText + res1
                          context = (c1, setInstanceType(symbolic_memory(id2), c2))
                      case None => 
                        System.out.println("11 The object should be in object memory.")
                    }
                    case v2: IRId =>
                      if (symbolic_memory.contains(v2.getUniqueName))
                        //context = symbolic_memory(v2.getUniqueName) + op.getText + res1
                        context = (c1, setInstanceType(symbolic_memory(v2.getUniqueName), c2))
                  }
                }
                //if (!context.isEmpty) {
                if (context != null) {
                  //val sid = symbol + index
                  c match {
                    case Some(value) =>
                      var sid = new SymbolicValue
                      sid.makeSymbolicValue(symbol+index, value.getTypes)
                      symbolic_memory(id) = sid
                      index += 1
                      //val info = new Info(false, sid, Some(op.getText), context, None)
                      val info = new Info(false, Some(sid), Some(op.getText), context._1, context._2, None) 
                      report = report:+info
                    case None => throw new ConcolicError("Symbolic value doesn't match with concrete value.")
                  }
                }
            }
            case SIRUn(_, op, expr) =>
            case SIRLoad(_, obj, index) =>
            /* variable */
            case v:IRId =>
              if (symbolic_memory.contains(v.getUniqueName)) 
                symbolic_memory(id) = symbolic_memory(v.getUniqueName)
                // Do not need to report because symbolic memory is replaced
              else
                symbolic_memory -= id
            case _:IRThis =>
            /* constant value */
            case n:IRNumber =>
              symbolic_memory -= id
            case s:IRString =>
            case b:IRBool =>
            case _:IRUndef =>
            case _:IRNull =>
          }
        case None =>
      }
    }
  }

  /* HELPER FUNCTIONS */
  def storeObject(obj: String, ref: String) = objectMemory.get(obj) match {
    case Some(list) => objectMemory.put(obj, list:+ref)
    case None => objectMemory.put(obj, List(ref))
  }
  def findObjectName(obj: IRId):Option[String] = {
    for (o <- objectMemory.keySet)
      if (objectMemory(o).contains(obj.getUniqueName))
        return Some(o)
    return None
  }
  def obj2str(obj: String, prop: String): String = obj + "." + prop
  
  def createContext(v1: String, v2: String, op: IROp, res1: String, res2: String): String = {
    if (symbolic_memory.contains(v1) && symbolic_memory.contains(v2)) {
      // only if linear constraints supported
      if (op.getKind == EJSOp.BIN_ARITH_MUL_MULTIPLICATION ||
          op.getKind == EJSOp.BIN_ARITH_MUL_DIVISION ||
          op.getKind == EJSOp.BIN_ARITH_MUL_REMINDER) 
        return symbolic_memory(v1) + op.getText + res2 
      else 
        return symbolic_memory(v1) + op.getText + symbolic_memory(v2)
    }
    else if (symbolic_memory.contains(v1)) 
      return symbolic_memory(v1) + op.getText + res2
    else 
      return symbolic_memory(v2) + op.getText + res1 
  }
  def makeContext(v1: String, v2: String, op: IROp, c1: Option[SymbolicValue], c2: Option[SymbolicValue]): (Option[SymbolicValue], Option[SymbolicValue]) = {
    if (symbolic_memory.contains(v1) && symbolic_memory.contains(v2)) {
      // only if linear constraints supported
      if (op.getKind == EJSOp.BIN_ARITH_MUL_MULTIPLICATION ||
          op.getKind == EJSOp.BIN_ARITH_MUL_DIVISION ||
          op.getKind == EJSOp.BIN_ARITH_MUL_REMINDER) { 
        c2 match {
          case Some(concreteValue) => 
            val info = new Info(false, Some(symbolic_memory(v2)), None, c2, None, None)
            report = report:+info
          case None => throw new ConcolicError("Concrete value should exist.")
        }
        return (setInstanceType(symbolic_memory(v1), c1), c2) 
      }
      else 
        return (setInstanceType(symbolic_memory(v1), c1), setInstanceType(symbolic_memory(v2), c2)) 
    }
    else if (symbolic_memory.contains(v1)) 
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

  def checkFocus(f: IRId) = coverage.checkTarget(f.getUniqueName) && doConcolic
  def startConcolic() = doConcolic = true
  def endConcolic() = doConcolic = false 

  
  def checkLoop():Boolean = {
    if (depth < max_depth) {
      depth = depth + 1
      return true
    }
    else {
      depth = 0
      return false
    }
  }

  def toStr(expr: IRExpr): String = expr match {
    case SIRBin(_, first, op, second) =>
      op.getText + toStr(expr)
    case SIRLoad(_, obj, index) =>
      obj.getOriginalName + "[" + toStr(index) + "]"
    case id:IRId => id.getOriginalName
    case _:IRThis => "this"
    case n:IRNumber => n.getText
    case s:IRString => s.getStr
    case b:IRBool => b.isBool.toString
    case _:IRUndef => "undefined"
    case _:IRNull => "null"
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
    //System.out.println("Symbolic memory = " + symbolic_memory map {case (key, value) => (key, value.toString)})
    //System.out.println("Symbolic report = " + report.map(_.toString))
    //System.out.println("Input = " + input.toString)
  }
}
