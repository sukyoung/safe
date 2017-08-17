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

package kr.ac.kaist.safe.interpreter

import edu.rice.cs.plt.tuple.{ Option => JOption }
import kr.ac.kaist.safe.concolic._
import kr.ac.kaist.safe.interpreter.{ InterpreterDebug => ID, InterpreterPredefine => IP }
import kr.ac.kaist.safe.interpreter.objects._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.phase.InterpretConfig
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.{ EJSCompletionType => CT, NodeUtil => NU }

class Interpreter(config: InterpretConfig) extends IRWalker {
  /*
   * TODO:
   * - Regular expressions
   * - eval and with
   * - strict mode
   * - Host objects
   * - 9.3.1 ToNumber applied to the String type
   * - 9.8.1 ToString applied to the Number type
   * - [[Call]]
   * - InterpreterError
   */

  ////////////////////////////////////////////////////////////////////////////////
  // Interpreter classes
  ////////////////////////////////////////////////////////////////////////////////
  val IH: InterpreterHelper = new InterpreterHelper(this)
  val IS: InterpreterState = new InterpreterState(this, config)
  val SH: SymbolicHelper = new SymbolicHelper(this)
  IS.init

  ////////////////////////////////////////////////////////////////////////////////
  // Type Conversion
  ////////////////////////////////////////////////////////////////////////////////

  implicit def ValError2Completion(in: ValError): Unit = IH.valError2NormalCompletion(in)

  ////////////////////////////////////////////////////////////////////////////////
  // Run
  ////////////////////////////////////////////////////////////////////////////////

  def doit(program: IRRoot, coverage: Option[Coverage], printComp: Boolean = true): Completion = {
    def tsLab(l: IRId): String = l.uniqueName

    // Set InterpreterState
    IS.coverage = coverage

    val IRRoot(_, fds, vds, irs) = program

    if (IS.coverage.isDefined) {
      val coverage = IS.coverage.get

      SH.initialize(coverage)

      val inputIR: List[IRStmt] = coverage.inputIR match { case Some(ir) => List(ir); case None => List() }
      /*println("Input IR!!!!!!!!")
      if (coverage.inputIR.isSome)
        System.out.println(new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(coverage.inputIR.unwrap).doit)
      System.out.println*/

      walkIRs(vds ++ fds ++ irs.filterNot(_.isInstanceOf[IRNoOp]) ++ inputIR)

      coverage.report = SH.getReport

      //if (coverage.debug) 
      //SH.print
    } else {
      println(IS.env)
      walkIRs(vds ++ fds ++ irs.filterNot(_.isInstanceOf[IRNoOp]))
    }
    if (printComp) IS.lastComp.Type match {
      case CT.NORMAL =>
        if (IS.coverage.isEmpty) {
          if (IS.lastComp.value != null) System.out.println("Normal(" + IH.toString(IS.lastComp.value) + ")")
          else System.out.println("Normal(empty)")
        }
      case CT.BREAK =>
        System.out.println("Break(" + tsLab(IS.lastComp.label) + ")")
      case CT.RETURN =>
        //if (!IS.coverage.isDefined)
        System.out.println("Return(" + IH.toString(IS.lastComp.value) + ")")
      case CT.THROW =>
        IS.lastComp.error match {
          case err: Error => System.out.println("Throw(Error) @ " + IS.lastComp.span)
          case err: EvalError => System.out.println("Throw(EvalError) @ " + IS.lastComp.span)
          case err: RangeError => System.out.println("Throw(RangeError) @ " + IS.lastComp.span)
          case err: ReferenceError => System.out.println("Throw(ReferenceError:" + err.x + ") @ " + IS.lastComp.span)
          case err: SyntaxError => System.out.println("Throw(SyntaxError) @ " + IS.lastComp.span)
          case err: TypeError => System.out.println("Throw(TypeError) @ " + IS.lastComp.span)
          case err: NYIError => System.out.println("Not Yet Implemented!!!")

          case IS.ErrorConstructor => System.out.println("Throw(Error) @ " + IS.lastComp.span)
          case IS.EvalErrorConstructor => System.out.println("Throw(EvalError) @ " + IS.lastComp.span)
          case IS.RangeErrorConstructor => System.out.println("Throw(RangeError) @ " + IS.lastComp.span)
          case IS.ReferenceErrorConstructor => System.out.println("Throw(ReferenceError) @ " + IS.lastComp.span)
          case IS.SyntaxErrorConstructor => System.out.println("Throw(SyntaxError) @ " + IS.lastComp.span)
          case IS.TypeErrorConstructor => System.out.println("Throw(TypeError) @ " + IS.lastComp.span)
          case IS.URIErrorConstructor => System.out.println("Throw(URIError) @ " + IS.lastComp.span)

          case _ => System.out.println("Throw(" + IS.lastComp.error.toString + ") @ " + IS.lastComp.span)
        }
    }
    if (config.ECMASpecTest) {
      assert(checkResults, "Value of a computed result does not equal corresponding expected value")
    }
    IS.lastComp

  }

  def checkResults: Boolean = {
    val resultPrefix = "__result"
    val expectPrefix = "__expect"
    @scala.annotation.tailrec
    def loopEnvs(env: Env): Boolean = {
      env match {
        case EmptyEnv() => true
        case ConsEnv(first, rest) => first match {
          case DeclEnvRec(store) =>
            val set: Set[String] = store.keySet.toSet
            val resultsNames = set.filter(_.startsWith(resultPrefix))
            val resultsZipped: Set[(String, StoreValue, String, StoreValue)] = resultsNames.map((resultName: String) => {
              val resultValue = store(resultName)
              val expectedName: String = expectPrefix + resultName.stripPrefix(resultPrefix)
              val expectedValue = store(expectedName)
              (resultName, resultValue, expectedName, expectedValue)
            })
            val firstEnvMatches = resultsZipped.forall({
              case (_, resultValue, _, expectedValue) =>
                resultValue.value == expectedValue.value
            })
            firstEnvMatches && loopEnvs(rest)
        }
      }
    }
    loopEnvs(IS.env)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Walk Functions
  ////////////////////////////////////////////////////////////////////////////////

  def walkId(id: IRId): ValError = try {
    if (debug > 0) System.out.println("\nIRId " + id.uniqueName + " base=" + IH.lookup(id))
    if (IH.isUndef(id)) IP.undefV
    else IH.getBindingValue(IH.lookup(id), id.originalName)
  } catch {
    case e: DefaultValueError => return e.err
  }

  def walkExpr(e: IRExpr): ValError = try {
    e match {
      case IRBin(_, first, op, second) => walkExpr(first) match {
        case v1: Val => walkExpr(second) match {
          case v2: Val => op.kind match {
            /*
           * 11.8.6 The instanceof operator
           * 15.3.5.3 [[HasInstance]](V)
           *   Instead of checking whether v2 has a [[HasInstance]] internal method,
           *   we check whether v2 is a function object.
           */
            case EJSInstOf => v2 match {
              case o2: JSObject =>
                if (IH.isCallable(v2)) {
                  val (l2prototype, _) = o2.getProperty("prototype");
                  val l2prototypeValue = l2prototype.getValueOrDefault
                  v1 match {
                    case o1: JSObject if l2prototypeValue.isInstanceOf[JSObject] =>
                      PVal(IH.getIRBool(IH.inherit(o1.proto, l2prototypeValue.asInstanceOf[JSObject])))
                    case _ =>
                      IP.falsePV
                  }
                } else IP.typeError
              case _ => IP.typeError
            }
            /*
           * 11.8.7 The in operator
           */
            case EJSIn => v2 match {
              case o2: JSObject => PVal(IH.getIRBool(o2.hasProperty(IH.toString(v1))))
              case _ => IP.typeError
            }
            // 11.5.1 Applying the * Operator
            case EJSMul =>
              PVal(IRVal(IH.applyMultiplication(IH.toNumber(v1), IH.toNumber(v2))))
            // 11.5.2 Applying the / Operator
            case EJSDiv =>
              PVal(IRVal(IH.applyDivision(IH.toNumber(v1), IH.toNumber(v2))))
            // 11.5.3 Applying the % Operator
            case EJSRem =>
              PVal(IRVal(IH.applyReminder(IH.toNumber(v1), IH.toNumber(v2))))
            // 11.6.1 The Addition Operator (+)
            case EJSAdd =>
              // TODO: NOTE 1 ... Host objects may handle the absence of a hint in some other manner.
              val lPrim = IH.toPrimitive(v1, "") // 5. Let lprim be ToPrimitive(lval). NOTE 1 ...
              val rPrim = IH.toPrimitive(v2, "") // 6. Let rprim be ToPrimitive(rval). NOTE 1 ...
              // 7. If Type(lprim) is String or Type(rprim) is String, then
              if (IH.typeOf(lPrim) == EJSType.STRING || IH.typeOf(rPrim) == EJSType.STRING)
                // a. Return the String that is the result of concatenating ToString(lprim) followed by ToString(rprim).
                PVal(IH.mkIRStrIR(IH.toString(lPrim) + IH.toString(rPrim)))
              // 8. Return the result of applying the additoin operation to ToNumber(lprim) and ToNumber(rprim).
              else PVal(IRVal(IH.applyAddition(IH.toNumber(lPrim), IH.toNumber(rPrim))))
            // 11.6.2 The Subtraction Operator (-)
            case EJSSub =>
              // 7. Return the result of applying the subtraction operation to lnum and rnum.
              PVal(IRVal(IH.applyAddition(IH.toNumber(v1), IH.negate(IH.toNumber(v2))))) // lnum + (-rnum)
            // 11.7.1 The Left Shift Operator (<<)
            case EJSShiftLeft =>
              PVal(IRVal(IH.mkIRNum(IH.toInt32(v1) << (IH.toUint32(v2) & 0x1F))))
            // 11.7.2 The mkIRNumIR Right Shift Operator (>>)
            case EJSShiftSRight =>
              PVal(IH.mkIRNumIR(IH.toInt32(v1) >> (IH.toUint32(v2) & 0x1F)))
            // 11.7.3 The Unsigned Right Shift Operator (>>>)
            case EJSShiftUSRight =>
              PVal(IH.mkIRNumIR(IH.toUint32(v1) >>> (IH.toUint32(v2) & 0x1F)))
            // 11.8.1 The Less-than Operator (<)
            case EJSLt => IH.abstractRelationalComparison(v1, v2) match {
              case PVal(IRVal(r: EJSBool)) => PVal(IRVal(r))
              case PVal(IRVal(EJSUndef)) => IP.falsePV
            }
            // 11.8.2 The Greater-than Operator (>)
            case EJSGt => IH.abstractRelationalComparison(v2, v1, false) match {
              case PVal(IRVal(r: EJSBool)) => PVal(IRVal(r))
              case PVal(IRVal(EJSUndef)) => IP.falsePV
            }
            // 11.8.3 The Less-than-or-equal Operator (<=)
            case EJSLte => IH.abstractRelationalComparison(v2, v1, false) match {
              case PVal(IRVal(EJSBool(b))) if b => IP.falsePV
              case PVal(IRVal(EJSUndef)) => IP.falsePV
              case _ => IP.truePV
            }
            // 11.8.4 The Greater-than-or-equal Operator (>=)
            case EJSGte => IH.abstractRelationalComparison(v1, v2) match {
              case PVal(IRVal(EJSBool(b))) if b => IP.falsePV
              case PVal(IRVal(EJSUndef)) => IP.falsePV
              case _ => IP.truePV
            }
            // 11.9.1 The Equals Operator (==)
            case EJSEq => PVal(IH.getIRBool(IH.abstractEqualityComparison(v2, v1)))
            // 11.9.2 The Does-not-equals Operator (!=)
            case EJSNEq => PVal(IH.getIRBool(!IH.abstractEqualityComparison(v2, v1)))
            // 11.9.4 The Strict Equals Operator (===)
            case EJSSEq => PVal(IH.getIRBool(IH.abstractStrictEqualityComparison(v2, v1)))
            // 11.9.5 The Strict Equals Operator (!==)
            case EJSSNEq => PVal(IH.getIRBool(!IH.abstractStrictEqualityComparison(v2, v1)))
            // 11.10 Binary Bitwise Operators
            case EJSBitAnd => PVal(IH.mkIRNumIR(IH.toInt32(v1) & IH.toInt32(v2)))
            case EJSBitXor => PVal(IH.mkIRNumIR(IH.toInt32(v1) ^ IH.toInt32(v2)))
            case EJSBitOr => PVal(IH.mkIRNumIR(IH.toInt32(v1) | IH.toInt32(v2)))
            // 11.11 Binary Logical Operators
            /*
           * The && and || operators are desugared away by Translator.
           *
          case EJSOp.BIN_LOG_AND =>
          case EJSOp.BIN_LOG_OR =>
          */
          }
          case e2: JSError => e2
        }
        case e1: JSError => e1
      }

      case IRUn(_, op, expr) => walkExpr(expr) match {
        case v: Val => op.kind match {
          // 11.4.2 The void Operator
          case EJSVoid => IP.undefV
          // 11.4.3 The typeof Operator
          case EJSTypeOf => PVal(IH.mkIRStrIR(IH.typeTag(v)))
          // 11.4.6 Unary + Operator
          case EJSPos => PVal(IRVal(IH.toNumber(v)))
          // 11.4.7 Unary - Operator
          case EJSNeg => PVal(IRVal(IH.negate(IH.toNumber(v))))
          // 11.4.8 Bitwise NOT Operator (~)
          case EJSBitNot => PVal(IH.mkIRNumIR(~IH.toInt32(v)))
          // 11.4.9 Logical NOT Operator (!)
          case EJSLogNot => PVal(IH.getIRBool(!IH.toBoolean(v)))
        }
        case e: JSError => op.kind match {
          // Implementation dependent : typeof JSError -> undefined
          case EJSTypeOf => IP.undefV
          case _ => e
        }
      }

      /*
     * 11.2.1 Property Accessors
     */
      case IRLoad(_, obj, index) => walkId(obj) match {
        case v1: Val => walkExpr(index) match {
          case v2: Val =>
            IH.toObject(v1) match {
              case err: TypeError => err
              case o: JSObject =>
                o.get(IH.toString(v2))
              case _ =>
                throw new InterpreterError("The result of toObject should be a location.", IS.span)
            }
          case e2: JSError => e2
        }
        case e1: JSError => e1
      }
      case id: IRId => walkId(id)
      case _: IRThis => IS.tb
      case v: IRVal => PVal(v)
    }
  } catch {
    case e: DefaultValueError => return e.err
  }

  def walkMember(m: IRMember): Any = {
    val oldSpan = IS.span
    IS.span = m.ast.span
    try {
      m match {
        case IRField(ast, id, expr) =>
          IS.span = ast.span
          walkExpr(expr) match {
            case v: Val => (id.originalName, IH.mkDataProp(v, true, true, true))
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        case IRGetProp(ast, ftn @ IRFunctional(_, _, id, params, args, fds, vds, body)) =>
          IS.span = ast.span
          val o = IH.createFunctionObject(ftn, IS.env, IS.strict)
          (id.originalName, IH.mkAccessorProp(Some(o), None, true, true))

        case IRSetProp(ast, ftn @ IRFunctional(_, _, id, params, args, vds, fds, body)) =>
          IS.span = ast.span
          val o = IH.createFunctionObject(ftn, IS.env, IS.strict)
          (id.originalName, IH.mkAccessorProp(None, Some(o), true, true))
      }
    } catch {
      case e: DefaultValueError => return e.err
    } finally {
      IS.span = oldSpan
    }
  }

  def walkIRs(irs: List[IRStmt]): Unit = {
    try {
      for (ir <- irs) {
        walk(ir)
        if (CT.isAbrupt(IS.comp.Type)) return
      }
    } catch {
      case e: DefaultValueError => IS.comp.setThrow(e.err, IS.span)
    }
  }

  def walkStmtUnit(irs: List[IRStmt]): Unit = {
    if (irs.isEmpty) IS.comp.setNormal()
    else walkIRs(irs)

    IS.lastComp.setLastCompletion(IS.comp)
    /*System.out.println("last   IRStmt completion: Type=" + IS.comp.Type + ", value=" + IS.comp.value);
    System.out.println("last StmtUnit completion: Type=" + IS.lastComp.Type + ", value=" + IS.lastComp.value);
    System.out.println();*/
  }

  /*
   * H,A,tb,ir --> H,A,ct
   */
  override def walk(node: IRStmt): IRStmt = {
    val oldSpan = IS.span
    try {
      node match {
        /*
         * AST statement unit
         */
        case u @ IRStmtUnit(ast, stmts) =>
          IS.span = ast.span
          if (IS.coverage.isDefined) {
            val cov = IS.coverage.get
            val uid = u.getUID
            if (!cov.execSet.contains(uid)) {
              if (covDebug) System.out.println("    executing.." + uid + " @ " + ast.span)
              cov.executed = cov.executed + 1
              cov.execSet += uid
            }
          }
          //System.out.println("uid="+uid + " at"+getSpan(ast))
          walkStmtUnit(stmts)

        /*
         * 12.2 Variable Statement
         *
         */
        case IRVarStmt(ast, id: IRId, fromParam) =>
          IS.span = ast.span
          if (!IH.hasBinding(id.originalName)) {
            if (fromParam) IH.createBinding(id, true, false)
            else IH.createBinding(id, true, IS.eval)
          }
          IS.comp.setNormal()

        /*
         * 12.4 Expression Statement
         * 11.13 Assignment Operators: PutValue(lref, rval)
         */
        // Check ReferenceError only for identifiers from the original JavaScript program
        // that is, only when isRef is true
        case IRExprStmt(ast, lhs, right, isRef) =>
          IS.span = ast.span
          walkExpr(right) match {
            case v: Val =>
              if (debug > 1)
                System.out.println("\nExprStmt: lhs=" + lhs.uniqueName + " right=" + ID.prExpr(right) + " v=" + v)
              IH.valError2NormalCompletion(IH.putValue(lhs, v, IS.strict))
            case e: ReferenceError =>
              if (debug > 1)
                System.out.println("\nExprStmt: lhs=" + lhs.uniqueName + " right=" + ID.prExpr(right) + " isRef=" + isRef)
              if (isRef) IS.comp.setThrow(e, ast.span)
              else IH.valError2NormalCompletion(IH.putValue(lhs, IP.undefV, IS.strict))
            case e: JSError =>
              IS.comp.setThrow(e, ast.span)
          }

        /*
         * 11.4.1 The delete Operator
         */
        case IRDelete(ast, lhs, id) =>
          IS.span = ast.span
          walkId(id) match {
            case v: Val => IH.delete(id.originalName, IS.strict) match {
              case res: Val => IH.valError2NormalCompletion(IH.putValue(lhs, res, IS.strict))
              case err: JSError => IS.comp.setThrow(err, ast.span)
            }
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        case IRDeleteProp(ast, lhs, obj, index) =>
          IS.span = ast.span
          walkId(obj) match {
            case o: JSObject => walkExpr(index) match {
              case v2: Val =>
                o.delete(IH.toString(v2), IS.strict) match {
                  case res: Val => IH.valError2NormalCompletion(IH.putValue(lhs, res, IS.strict))
                  case err: JSError => IS.comp.setThrow(err, ast.span)
                }
              case e2: JSError => IS.comp.setThrow(e2, ast.span)
            }
            case pv: PVal =>
              throw new InterpreterError("Translator should have inserted toObject already.", ast.span)
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        /*
         * 11.2.1 Property Accessors
         * 11.13 Assignment Operators: PutValue(lref, rval)
         */
        case IRStore(ast, obj, index, rhs) =>
          IS.span = ast.span
          walkId(obj) match {
            // 8.7.2 PutValue(V, W)
            // 1. If Type(V) is not Reference, throw a ReferenceError exception. Happens only when simple assignment.
            //    Processed during compile time. Look like "1 = 1"
            // 2. Let base be the result of calling GetBase(V).
            case v1: Val => walkExpr(index) match {
              case v2: Val =>
                val v2str = IH.toString(v2)
                IH.toObject(v1) match {
                  // * IRStore 3
                  case _: TypeError => IS.comp.setThrow(IP.typeError, ast.span)
                  // 3. If IsUnresolvableReference(V), then
                  // a. If IsStrictReference(V) is true, then
                  // i. Throw ReferenceError exception.
                  // b.Call the [[Put]] internal method of the global object,
                  //   passing GetReferencedName(V) for the property name, W for the value, and false for the Throw flag.
                  // IsUnresolvableReference just check whether V is undefined or not.
                  // If we consider strict mode, we should check that V is undefined.
                  // Because we do not consider strict mode currently, we omitted it.
                  // 4. Else if IsPropertyReference(V), then
                  case o: JSObject => walkExpr(rhs) match {
                    case v3: Val => v1 match {
                      // * IRStore 5
                      case _: PVal if (o.canPut(v2str) == false) =>
                        // a. If Throw is true, then throw a TypeError exception.
                        if (IS.strict) { IS.comp.setThrow(IP.typeError, ast.span); return node }
                        else { IS.comp.setNormal(v3); return node }
                      case _: PVal =>
                        val ownDesc = o.getOwnProperty(v2str)
                        if (ownDesc != null && IH.isDataDescriptor(ownDesc)) {
                          // * IRStore 6
                          // a. If Throw is true, then throw a TypeError exception.
                          if (IS.strict) { IS.comp.setThrow(IP.typeError, ast.span); return node }
                          else { IS.comp.setNormal(v3); return node }
                        }
                        // 5. Let desc be the result of calling the [[GetProperty]] internal method of O with argument P.
                        //    This may be either an own or inherited accessor property descriptor
                        //    or an inherited data property descriptor.
                        val (desc, _) = o.getProperty(v2str)
                        // 6. If IsAccessorDescriptor(desc) is true, then
                        if (desc != null && IH.isAccessorDescriptor(desc)) {
                          // * IRStore 7
                          // * IRStore 8
                          // desc.[[Set]].[[Call]](v1, [v3])
                          desc.set.get match {
                            // a. Let setter be desc.[[Set]] (see 8.10) which cannot be undefined.
                            case setter: JSFunction =>
                              // b. Call the [[Call]] internal method of setter
                              //    providing base as the this value  and an argument list containing only W.
                              // TODO:
                              val args: JSArray = IS.ArrayConstructor.apply(IP.plusOneV, "Arguments")
                              args.defineOwnProperty("0", IH.mkDataProp(v3, true, true, true), false)
                              val oldEnv = IS.env
                              val oldTb = IS.tb
                              IH.call(IS.info, o, args, setter)
                              IS.env = oldEnv
                              IS.tb = oldTb
                              return node
                            case _ => throw new InterpreterError(o.className + "._set(" + v2str + ") error.", IS.span)
                          }
                        }
                        // 7. Else, this is a request to create an own property on the transient object O
                        // * IRStore 9
                        // a. If Throw is true, then throw a TypeError exception.
                        if (IS.strict) { IS.comp.setThrow(IP.typeError, ast.span); return node }
                        else { IS.comp.setNormal(v3); return node }
                      // a.-1 If HasPrimitiveBase(V) is false, then let put be the [[Put]] internal method of base
                      //      which an object is not an array
                      case _ if (o.className != "Array") =>
                        o.put(v2str, v3, IS.strict) match {
                          // * IRStore 10
                          case err: JSError =>
                            IS.comp.setThrow(err, ast.span); return node
                          // * IRStore 11
                          case _ => IS.comp.setNormal(v3); return node
                        }
                      // a.-1 If HasPrimitiveBase(V) is false, then let put be the [[Put]] internal method of base
                      //      which an object is an array
                      case _ =>
                        // 8.12.5 [[Put]] ( P, V, Throw )
                        // 1. If the result of calling the [[CanPut]] internal method of O with argument P is false, then
                        if (!o.canPut(v2str)) {
                          // * IRStore 12
                          // a. If Throw is true, then throw a TypeError exception.
                          if (IS.strict) { IS.comp.setThrow(IP.typeError, ast.span); return node }
                          // b. Else return.
                          else { IS.comp.setNormal(v3); return node }
                        }
                        // 2. Let ownDesc be the result of calling the [[GetOwnProperty]] internal method of O with argument P.
                        val ownDesc = o.getOwnProperty(v2str)
                        // 3. If IsDataDescriptor(ownDesc) is true, then
                        if (ownDesc != null && IH.isDataDescriptor(ownDesc)) {
                          // a. Let valueDesc be the Property Descriptor {[[Value]]: V}.
                          val valueDesc = new ObjectProp(Some(v3), None, None, None, None, None)
                          // b. Call the [[DefineOwnProperty]] internal method of O passing P, valueDesc, and Throw as arguments.
                          // c. Return.
                          o.defineOwnProperty(v2str, valueDesc, IS.strict) match {
                            // * IRStore 13
                            case err: JSError =>
                              IS.comp.setThrow(err, ast.span); return node
                            // * IRStore 14
                            case _ => IS.comp.setNormal(v3); return node
                          }
                        }
                        // 4. Let desc be the result of calling the [[GetProperty]] internal method of O with argument P.
                        //    This may be either an own or inherited accessor property descriptor
                        //    or an inherited data property descriptor.
                        val (desc, _) = o.getProperty(v2str)
                        // 5. If IsAccessorDescriptor(desc) is true, then
                        if (desc != null && IH.isAccessorDescriptor(desc)) {
                          // * IRStore 15
                          // * IRStore 16
                          // desc.[[Set]].[[Call]](v1, [v3])
                          desc.set.get match {
                            // a. Let setter be desc.[[Set]] (see 8.10) which cannot be undefined.
                            case setter: JSFunction =>
                              // b. Call the [[Call]] internal method of setter
                              //    providing base as the this value  and an argument list containing only W.
                              // TODO:
                              val args = IS.ArrayConstructor.apply(IP.plusOneV, "Arguments")
                              args.defineOwnProperty("0", IH.mkDataProp(v3, true, true, true), false)
                              val oldEnv = IS.env
                              val oldTb = IS.tb
                              IH.call(IS.info, o, args, setter)
                              IS.env = oldEnv
                              IS.tb = oldTb
                            case _ => throw new InterpreterError(o.className + "._set(" + v2str + ") error.", IS.span)
                          }
                        }
                        // 6. Else, this is a request to create an own property on the transient object O
                        // a. Let newDesc be the Property Descriptor
                        //    {[[Value]]: V, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}.
                        val newDesc = IH.mkDataProp(v3, true, true, true)
                        o.defineOwnProperty(v2str, newDesc, IS.strict) match {
                          // * IRStore 17
                          case err: JSError =>
                            IS.comp.setThrow(err, ast.span); return node
                          // * IRStore 18
                          case _ => IS.comp.setNormal(v3)
                        }
                    }
                    // * IRStore 4
                    case e3: JSError => IS.comp.setThrow(e3, ast.span)
                  }
                  case _ =>
                    throw new InterpreterError("The result of toObject should be a location.", ast.span)
                }
              // * IRStore 2
              case e2: JSError => IS.comp.setThrow(e2, ast.span)
            }
            // 8.7.2 PutValue(V, W)
            // 1. If Type(V) is not Reference, throw a ReferenceError exception. Happens only when simple assignment.
            //    Processed during compile time. Look like "1 = 1"
            // * IRStore 1
            case e1: JSError => IS.comp.setThrow(e1, ast.span)
          }

        /*
         * 12.1 Block
         */
        case IRSeq(ast, stmts) =>
          IS.span = ast.span
          walkIRs(stmts)

        /*
         * 12.5 The if Statement
         */
        case IRIf(ast, expr, trueB, falseBOpt) =>
          IS.span = ast.span
          walkExpr(expr) match {
            case v: Val =>
              if (IH.toBoolean(v)) walk(trueB)
              else if (falseBOpt.isDefined) walk(falseBOpt.get)
              else IS.comp.setNormal()
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        /*
         * 12.6.2 The while Statement
         */
        case IRWhile(ast, cond, body, breakLabel, contLabel) =>
          IS.span = ast.span
          var cont: Boolean = false
          do {
            // evaluate SH.checkLoop before evaluating the body
            if (IS.coverage.isDefined && cont) {
              cont = SH.checkLoop(ast.span.toString)
              if (!cont) return node
            }
            walkExpr(cond) match {
              case v: Val =>
                cont = IH.toBoolean(v)
                if (!cont) return node
              case err: JSError =>
                IS.comp.setThrow(err, ast.span)
                return node
            }
            walk(body)
            if (CT.isAbrupt(IS.comp.Type)) return node
          } while (cont)

        /*
         * 12.8 The break Statement
         */
        case IRBreak(ast, label) =>
          IS.span = ast.span
          IS.comp.setBreak(label)

        /*
         * 12.9 The return Statement
         */
        case IRReturn(ast, exprOpt) =>
          IS.span = ast.span
          exprOpt match {
            case None => IS.comp.setReturn(IP.undefV)
            case Some(expr) => walkExpr(expr) match {
              case v: Val => IS.comp.setReturn(v)
              case e: JSError => IS.comp.setThrow(e, ast.span)
            }
          }

        /*
         * 12.10 The with Statement
         */
        case IRWith(ast, id, stmt) =>
          IS.span = ast.span
          walkId(id) match {
            case v: Val => IH.toObject(v) match {
              case o: JSObject =>
                val oldEnv = IH.copyEnv(IS.env)
                IS.env = ConsEnv(ObjEnvRec(o), oldEnv)
                walk(stmt)
                IS.env = oldEnv
              case e: JSError => IS.comp.setThrow(e, ast.span)
            }
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        /*
       * 12.12 Labelled Statement
       */
        case IRLabelStmt(ast, id, stmt) =>
          IS.span = ast.span
          walk(stmt)
          if (IS.comp.Type == CT.BREAK && id.originalName == IS.comp.label.originalName) IS.comp.setNormal()

        /*
       * 12.13 The throw Statement
       */
        case IRThrow(ast, expr) =>
          IS.span = ast.span
          walkExpr(expr) match {
            case v: Val => IS.comp.setThrow(v, ast.span)
            case e: JSError => IS.comp.setThrow(e, ast.span)
          }

        /*
       * 12.14 The try Statement
       */
        case IRTry(ast, body, nameOpt, catchBOpt, finallyBOpt) =>
          IS.span = ast.span
          (nameOpt, catchBOpt, finallyBOpt) match {
            case (Some(id: IRId), Some(catchB), None) =>
              walk(body)
              if (IS.comp.Type == CT.THROW) {
                val newDeclEnv = IH.newDeclEnv()
                IH.createBindingToDeclarative(newDeclEnv, id, true, false)
                IH.setBindingToDeclarative(newDeclEnv, id, exnLoc(this, IS.comp.error), false, false) match {
                  case v: Val =>
                    walk(catchB)
                    IH.popEnv()
                  case e: JSError =>
                    IH.popEnv()
                    IS.comp.setThrow(e, ast.span)
                }
              }
            case (None, None, Some(finallyB)) =>
              walk(body)
              val oldComp = IS.comp.copy()
              walk(finallyB)
              if (IS.comp.Type == CT.NORMAL) IS.comp = oldComp
            case (Some(id: IRId), Some(catchB), Some(finallyB)) =>
              walk(body)
              if (IS.comp.Type == CT.THROW) {
                val newDeclEnv = IH.newDeclEnv()
                IH.createBindingToDeclarative(newDeclEnv, id, true, false)
                IH.setBindingToDeclarative(newDeclEnv, id, exnLoc(this, IS.comp.error), false, false) match {
                  case v: Val =>
                    walk(catchB)
                    IH.popEnv()
                    val oldComp = IS.comp.copy()
                    walk(finallyB)
                    if (IS.comp.Type == CT.NORMAL) IS.comp = oldComp
                  case e: JSError =>
                    IH.popEnv()
                    IS.comp.setThrow(e, ast.span)
                }
              } else {
                val oldComp = IS.comp.copy()
                walk(finallyB)
                if (IS.comp.Type == CT.NORMAL) IS.comp = oldComp
              }
            case _ => IS.comp.setNormal()
          }

        /*
       * 13 Function Definition
       * 10.5 Declaration Binding Instantiation - Step 5
       */
        case IRFunDecl(ast, ftn @ IRFunctional(_, _, id: IRId, params, args, vds, fds, body)) =>
          IS.span = ast.span
          val f = id.originalName
          val o = IH.createFunctionObject(ftn, IS.env, IS.strict)
          IH.hasBinding(f) match {
            case true => IS.env match {
              // Function Definition 2
              case EmptyEnv() =>
                val (p, _) = IS.GlobalObject.getProperty(f)
                if (p == null) { IS.comp.setThrow(IP.typeError, ast.span); return node } // If _getProperty() returns undefined.
                p.isConfigurable match {
                  // Function Definition 2-1
                  case true =>
                    IS.GlobalObject.defineOwnProperty(f, IH.mkDataProp(IP.undefV, true, true, IS.eval), true) match {
                      case v: Val =>
                        IH.setBinding(id, o, false, IS.strict) match {
                          case vv: Val => IS.comp.setNormal(vv)
                          case err: JSError => IS.comp.setThrow(err, ast.span)
                        }
                      case err: JSError => IS.comp.setThrow(err, ast.span)
                    }
                  // Function Definition 2-2
                  case false =>
                    if (IH.isDataDescriptor(p) && p.isWritable && p.isEnumerable) {
                      IH.setBinding(id, o, false, IS.strict) match {
                        case v: Val => IS.comp.setNormal()
                        case err: JSError => IS.comp.setThrow(err, ast.span)
                      }
                    } else IS.comp.setThrow(IP.typeError, ast.span)
                }
              // Function Definition 3
              case ConsEnv(first, rest) =>
                IH.setBinding(id, o, false, IS.strict) match {
                  case v: Val => IS.comp.setNormal()
                  case err: JSError => IS.comp.setThrow(err, ast.span)
                }
            }
            // Function Definition 1
            case false =>
              IH.createBinding(id, true, IS.eval)
              IH.setBinding(id, o, false, IS.strict) match {
                case v: Val => IS.comp.setNormal()
                case err: JSError => IS.comp.setThrow(err, ast.span)
              }
          }

        /*
       * 11.2.3 Function Calls
       * 11.2.4 Argument Lists
       */
        case IRNew(ast, lhs: IRId, fun: IRId, args) =>
          IS.span = ast.span
          walkId(fun) match {
            case f: JSFunction if f.const && args.size == 2 =>
              val oldEnv = IS.env
              val oldTb = IS.tb
              val (thisP, argsP) = (args.head, args.last)
              walkId(thisP) match {
                case v1: Val => walkId(argsP) match {
                  case v2: Val => v2 match {
                    case argsObj: JSObject =>
                      val obj: JSObject = try {
                        f.construct(argsObj)
                      } catch {
                        case e: ErrorException =>
                          IS.comp.setThrow(IP.error, ast.span); return node
                        case e: EvalErrorException =>
                          IS.comp.setThrow(IP.evalError, ast.span); return node
                        case e: RangeErrorException =>
                          IS.comp.setThrow(IP.rangeError, ast.span); return node
                        // TODO:
                        // case e:ReferenceError => return throwErr(referenceError, ast)
                        case e: SyntaxErrorException =>
                          IS.comp.setThrow(IP.syntaxError, ast.span); return node
                        case e: TypeErrorException =>
                          IS.comp.setThrow(IP.typeError, ast.span); return node
                        case e: URIErrorException =>
                          IS.comp.setThrow(IP.uriError, ast.span); return node
                        case e: NYIErrorException => IS.comp.setThrow(IP.nyiError, ast.span); return node
                      }
                      // TODO:
                      obj.proto match {
                        case IS.ErrorPrototype => IS.comp.setThrow(IP.error, ast.span)
                        case IS.EvalErrorPrototype => IS.comp.setThrow(IP.evalError, ast.span)
                        case IS.RangeErrorPrototype => IS.comp.setThrow(IP.rangeError, ast.span)
                        // case IS.ReferenceError => throwErr(referenceError, ast)
                        case IS.SyntaxErrorPrototype => IS.comp.setThrow(IP.syntaxError, ast.span)
                        case IS.TypeErrorPrototype => IS.comp.setThrow(IP.typeError, ast.span)
                        case IS.URIErrorPrototype => IS.comp.setThrow(IP.uriError, ast.span)
                        case _ =>
                          IH.valError2NormalCompletion(IH.putValue(lhs, obj, IS.strict))
                      }
                    case _ =>
                      System.out.println("_arguments_ is expected but got " + v2)
                      IS.env = oldEnv
                      IS.tb = oldTb
                      IS.comp.setThrow(IP.typeError, ast.span)
                  }
                  case err: JSError =>
                    IS.env = oldEnv
                    IS.tb = oldTb
                    IS.comp.setThrow(err, ast.span)
                }
                case err: JSError =>
                  IS.env = oldEnv
                  IS.tb = oldTb
                  IS.comp.setThrow(err, ast.span)
              }
            case f: JSFunction if args.size == 2 =>
              val obj: JSObject = walkId(args.head) match {
                case o: JSObject => o
                case _ => throw new InterpreterError("This binding for function call should be an object!", ast.span)
              }
              f.get("prototype") match {
                case o: JSObject => obj.proto = o
                case pv: PVal => obj.proto = IS.ObjectPrototype
              }
              walk(IRCall(ast, lhs, fun, args(0), args(1)))
            case _ if args.size != 2 => throw new InterpreterError("SIRNew should take two arguments!", ast.span)
            case _ =>
              IS.comp.setThrow(TypeError(), ast.span)
          }

        // Internal Function Calls
        case IRInternalCall(ast, lhs: IRId, fun: String, args) =>
          IS.span = ast.span
          fun match {
            case "<>Concolic<>StartConcolic" =>
              SH.startConcolic()
            case "<>Concolic<>EndConcolic" =>
              SH.endConcolic()
            case "<>Concolic<>StoreEnvironment" =>
              SH.storeEnvironment(args.head.asInstanceOf[IRId], args(1).asInstanceOf[IRId])
            case "<>Concolic<>StoreVariable" =>
              SH.storeVariable(args.head.asInstanceOf[IRId], args(1).asInstanceOf[IRId])
            case "<>Concolic<>ExecuteAssignment" =>
              val env = SH.getEnvironment(args(1).asInstanceOf[IRId])
              val bs = IH.lookup(args(1).asInstanceOf[IRId])
              var loc = "Variable"
              bs match {
                case der: DeclEnvRec => loc = "LocalVariable"
                case _ =>
              }
              val c: Option[SymbolicValue] = walkExpr(args(1).asInstanceOf[IRId]) match {
                case v: Val =>
                  var x = new SymbolicValue
                  v match {
                    case PVal(IRVal(EJSUndef)) => x.makeSymbolicValueFromConcrete("Undefined")
                    case PVal(IRVal(EJSNull)) => x.makeSymbolicValueFromConcrete("Null")
                    case PVal(IRVal(_: EJSBool)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                    case PVal(IRVal(_: EJSNumber)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                    case PVal(IRVal(_: EJSString)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                    case o: JSObject => x.makeSymbolicValueFromConcrete("Object")
                  }
                  Some(x)
                case _: JSError => None
              }
              args.head match {
                case IRBin(_, first, op, second) =>
                  val c1: Option[SymbolicValue] = walkExpr(first) match {
                    case v: Val =>
                      var v1 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v1.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v1.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v1.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v1)
                    case _: JSError => None
                  }
                  val c2: Option[SymbolicValue] = walkExpr(second) match {
                    case v: Val =>
                      val v2 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v2.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v2.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v2.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v2)
                    case _: JSError => None
                  }
                  SH.executeAssignment(loc, args(1).asInstanceOf[IRId], args.head, c, c1, c2, env)
                case _ =>
                  SH.executeAssignment(loc, args(1).asInstanceOf[IRId], args.head, c, None, None, env)
              }
            case "<>Concolic<>ExecuteStore" =>
              val env = SH.getEnvironment(lhs)
              val c: Option[SymbolicValue] = walkExpr(args.head) match {
                case v: Val =>
                  var x = new SymbolicValue
                  v match {
                    case PVal(IRVal(EJSUndef)) => x.makeSymbolicValueFromConcrete("Undefined")
                    case PVal(IRVal(EJSNull)) => x.makeSymbolicValueFromConcrete("Null")
                    case PVal(IRVal(_: EJSBool)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                    case PVal(IRVal(_: EJSNumber)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                    case PVal(IRVal(_: EJSString)) => x.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                    case o: JSObject => x.makeSymbolicValueFromConcrete("Object")
                  }
                  Some(x)
                case _: JSError => None
              }
              args.head match {
                case IRBin(_, first, op, second) =>
                  val c1: Option[SymbolicValue] = walkExpr(first) match {
                    case v: Val =>
                      var v1 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v1.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v1.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v1.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v1)
                    case _: JSError => None
                  }
                  val c2: Option[SymbolicValue] = walkExpr(second) match {
                    case v: Val =>
                      var v2 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v2.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v2.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v2.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v2)
                    case _: JSError => None
                  }
                  SH.executeStore(lhs, args(1).asInstanceOf[IRId].uniqueName, args.head, c, c1, c2, env)
                case _ =>
                  SH.executeStore(lhs, args(1).asInstanceOf[IRId].uniqueName, args.head, c, None, None, env)
              }
            case "<>Concolic<>ExecuteCondition" => {
              val argHead = args.head
              val branchTaken = walkExpr(args.head) match {
                case v: Val => Some(IH.toBoolean(v))
                case _: JSError => None
              }
              args.head match {
                case IRBin(_, first, op, second) =>
                  val c1: Option[SymbolicValue] = walkExpr(first) match {
                    case v: Val =>
                      val v1 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v1.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v1.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v1.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v1.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v1)
                    case _: JSError => None
                  }
                  val c2: Option[SymbolicValue] = walkExpr(second) match {
                    case v: Val =>
                      var v2 = new SymbolicValue
                      v match {
                        case PVal(IRVal(EJSUndef)) => v2.makeSymbolicValueFromConcrete("Undefined")
                        case PVal(IRVal(EJSNull)) => v2.makeSymbolicValueFromConcrete("Null")
                        case PVal(IRVal(_: EJSBool)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Boolean")
                        case PVal(IRVal(_: EJSNumber)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "Number")
                        case PVal(IRVal(_: EJSString)) => v2.makeSymbolicValueFromConcrete(IH.toString(v), "String")
                        case o: JSObject => v2.makeSymbolicValueFromConcrete("Object")
                      }
                      Some(v2)
                    case _: JSError => None
                  }
                  SH.executeCondition(args.head, branchTaken, c1, c2, args(1).asInstanceOf[IRId])
                case IRUn(_, op, e) => SH.executeCondition(args.head, branchTaken, None, None, args(1).asInstanceOf[IRId])
                case v: IRId => SH.executeCondition(args.head, branchTaken, None, None, args(1).asInstanceOf[IRId])
                case _ => SH.executeCondition(args.head, None, None, None, args(1).asInstanceOf[IRId])
              }
            }
            case "<>Concolic<>EndCondition" =>
              SH.endCondition(args(1).asInstanceOf[IRId])
            case "<>Concolic<>WalkVarStmt" =>
              SH.walkVarStmt(lhs, args.head.asInstanceOf[IRVal].value.asInstanceOf[EJSNumber].num.toInt, args(1).asInstanceOf[IRId])
            case "<>Concolic<>StoreThis" =>
              SH.storeThis(args(1).asInstanceOf[IRId])

            case NU.INTERNAL_TO_OBJ =>
              if (IS.coverage.isDefined) {
                args.head match {
                  case obj: IRId => SH.storeObject(obj.uniqueName, lhs.uniqueName)
                  case obj: IRThis => SH.storeObject("this", lhs.uniqueName)
                  case _ =>
                }
              }
              walkExpr(args.head) match {
                case v: Val => IH.toObject(v) match {
                  // (H', A, tb), x = l
                  case o: JSObject => IH.valError2NormalCompletion(IH.putValue(lhs, o, IS.strict))
                  case err: JSError => IS.comp.setThrow(err, ast.span)
                }
                case err: JSError => IS.comp.setThrow(err, ast.span)
              }
            case NU.INTERNAL_IS_OBJ => walkExpr(args.head) match {
              // (H, A, tb), x = true
              case o: JSObject => IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IH.getIRBool(true)), IS.strict))
              // (H, A, tb), x = false
              case pv: PVal => IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IH.getIRBool(false)), IS.strict))
              case err: JSError => IS.comp.setThrow(err, ast.span)
            }
            case NU.INTERNAL_TO_NUM => walkExpr(args.head) match {
              // (H, A, tb), x = ToNumber(H, v)
              case v: Val => IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IRVal(IH.toNumber(v))), IS.strict))
              case err: JSError => IS.comp.setThrow(err, ast.span)
            }
            case NU.INTERNAL_GET_BASE =>
              val bs = IH.lookup(args.head.asInstanceOf[IRId])
              // (H, A, tb), x = bs
              bs match {
                case oer: ObjEnvRec => IH.valError2NormalCompletion(IH.putValue(lhs, oer.o, IS.strict))
                case _: DeclEnvRec => IH.valError2NormalCompletion(IH.putValue(lhs, IS.GlobalObject, IS.strict))
              }
            case NU.INTERNAL_PRINT => walkExpr(args.head) match {
              case v: Val =>
                System.out.println(IH.toString(v))
                IS.comp.setNormal(null)
              case err: JSError => IS.comp.setThrow(err, ast.span)
            }
            case NU.INTERNAL_PRINT_IS =>
              //ID.prHeapEnv(IS)
              IH.valError2NormalCompletion(IH.putValue(lhs, IP.truePV, IS.strict))
            case NU.INTERNAL_GET_TICK_COUNT =>
              IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IH.mkIRNumIR(System.currentTimeMillis())), IS.strict))
            case NU.INTERNAL_ITER_INIT => walkExpr(args.head) match {
              // TODO case for null or undefined
              case v: JSObject =>
                // (H', A, tb), x = l
                IH.valError2NormalCompletion(IH.putValue(lhs, IH.iteratorInit(IH.collectProps(v)), IS.strict))
              case err: JSError => IS.comp.setThrow(err, ast.span)
              case _ => IS.comp.setThrow(IP.typeError, ast.span)
            }
            case NU.INTERNAL_ITER_HAS_NEXT =>
              val (e1, e2) = (args.head, args(1))
              walkExpr(e1) match {
                case v1: JSObject => walkId(e2.asInstanceOf[IRId]) match {
                  case v2: JSObject => v2.isInDomO(IH.next(v2, IH.toInt(v2.property.get("@i").get), v1).toString) match {
                    // (H, A, tb), x = true
                    case true => IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IH.getIRBool(true)), IS.strict))
                    // (H, A, tb), x = true
                    case false => IH.valError2NormalCompletion(IH.putValue(lhs, PVal(IH.getIRBool(false)), IS.strict))
                  }
                  case err: JSError => IS.comp.setThrow(err, ast.span)
                  case _ => IS.comp.setThrow(IP.typeError, ast.span)
                }
                case err: JSError => IS.comp.setThrow(err, ast.span)
                case _ => IS.comp.setThrow(IP.typeError, ast.span)
              }
            case NU.INTERNAL_ITER_NEXT =>
              val (e1, e2) = (args.head, args(1))
              walkExpr(e1) match {
                case v1: JSObject => walkId(e2.asInstanceOf[IRId]) match {
                  case v2: JSObject =>
                    val i = IH.next(v2, IH.toInt(v2.property.get("@i").get), v1)
                    v2.putProp("@i", IH.numProp(i + 1))
                    // (H', A, tb), x = H(v2).@property("i")
                    IH.valError2NormalCompletion(IH.putValue(lhs, IH.toVal(v2.property.get(i.toString).get), IS.strict))
                  case err: JSError => IS.comp.setThrow(err, ast.span)
                  case _ => IS.comp.setThrow(IP.typeError, ast.span)
                }
                case err: JSError => IS.comp.setThrow(err, ast.span)
                case _ => IS.comp.setThrow(IP.typeError, ast.span)
              }
            case x =>
              println(s"x = $x") //TODO MV Debugging: remove print
              IS.comp.setThrow(IP.nyiError, ast.span)
          }

        case IRCall(ast, lhs: IRId, fun: IRId, thisB, args) =>
          if (IS.coverage.isDefined) {
            walkId(fun) match {
              case v: Val => v match {
                case f: JSFunction =>
                  if (!SH.checkRecursiveCall(f.code.name.uniqueName, args)) return node
              }
              case err: JSError =>
            }
          }
          IS.span = ast.span
          val oldEnv = IS.env
          val oldTb = IS.tb
          var valueToAssign: Val = null
          if (debug > 0)
            System.out.println("\nIRCall:lhs=" + ID.getE(lhs.uniqueName) + " fun=" + ID.getE(fun.uniqueName) +
              " thisB=" + ID.getE(thisB.uniqueName) + " args=" + ID.getE(args.uniqueName))
          walkId(fun) match {
            case err: JSError => IS.comp.setThrow(err, ast.span)
            case v: Val => walkExpr(thisB) match {
              case v1: Val => walkId(args) match {
                case v2: Val => v match {
                  case f: JSFunction if IH.isCallable(f) => v2 match {
                    case o2: JSObject =>
                      /*
                       * 10.4.3 Entering Function Code
                       */
                      IH.call(ast.info, v1, o2, f)
                      if (IS.comp.Type == CT.NORMAL) valueToAssign = IP.undefV
                      else if (IS.comp.Type == CT.RETURN) valueToAssign = IS.comp.value
                    case _ =>
                      System.out.println("_arguments_ is expected but got " + v2)
                      IS.comp.setThrow(IP.typeError, ast.span)
                  }
                  case pv =>
                    System.out.println("Function is expected for " + fun.uniqueName + " but got " + pv)
                    IS.comp.setThrow(IP.typeError, ast.span)
                }
                case err: JSError =>
                  IS.comp.setThrow(err, ast.span)
              }
              case err: JSError =>
                IS.comp.setThrow(err, ast.span)
            }
          }
          IS.env = oldEnv
          IS.tb = oldTb
          if (valueToAssign != null) IH.valError2NormalCompletion(IH.putValue(lhs, valueToAssign, IS.strict))

        /*
         * 11.2.5 Function Expressions
         * 13 Function Definition
         */
        case IRFunExpr(ast, lhs, ftn @ IRFunctional(_, _, id, params, args, vds, fds, body)) =>
          IS.span = ast.span
          IH.newDeclEnv()
          val o = IH.createFunctionObject(ftn, IS.env, IS.strict)
          IH.createAndSetBinding(id, o, false, IS.eval)
          // (Hf, A, tb), x = l
          IH.popEnv()
          IH.valError2NormalCompletion(IH.putValue(lhs, o, IS.strict))

        /*
         * 11.1.4 Array Initializer
         * Speically treat when it has more than 1,000 integer elements.
         */
        case IRArrayNumber(ast, lhs, elements) =>
          IS.span = ast.span
          val arr: JSArray = IS.ArrayConstructor.construct(PVal(IH.mkIRNumIR(elements.size)))
          val vs = new Array[PVal](elements.size)
          var i: Int = 0
          elements.foreach(e => {
            vs(i) = PVal(IF.makeNumberIR(e.toString, e))
            i += 1
          })
          for (i <- 0 until vs.size) {
            arr.defineOwnProperty(i.toString, IH.mkDataProp(vs(i), true, true, true), false)
          }
          // (H'', A', tb), x = l
          IH.valError2NormalCompletion(IH.putValue(lhs, arr, IS.strict))

        /*
         * 11.1.4 Array Initializer
         */
        case IRArray(ast, lhs, elements) =>
          IS.span = ast.span
          // TODO:
          // IS.heap.put(l, IH.newArrObject(l, elements.size))
          val arr: JSArray = IS.ArrayConstructor.construct(PVal(IH.mkIRNumIR(elements.size)))
          val ves: List[Option[ValError]] = for (element <- elements) yield element match {
            case Some(element) => Some(walkExpr(element))
            case None => None
          }
          for (Some(err) <- ves if err.isInstanceOf[JSError]) {
            IS.comp.setThrow(err, ast.span)
            return node
          }
          // Now, there is no error.
          val vs: List[Option[Val]] = for (v <- ves) yield v.asInstanceOf[Option[Val]]
          for ((Some(vi), i) <- vs.view.zipWithIndex) {
            arr.defineOwnProperty(i.toString, IH.mkDataProp(vi, true, true, true), false)
          }
          // (H'', A', tb), x = l
          IH.valError2NormalCompletion(IH.putValue(lhs, arr, IS.strict))

        case IRArgs(ast, lhs, elements) =>
          if (IS.coverage.isDefined) SH.storeArguments(lhs, elements)
          IS.span = ast.span
          // TODO:
          // IS.heap.put(l, IH.newArrObject(l, elements.size))
          // TODO:
          val o = IS.ArrayConstructor.apply(PVal(IH.mkIRNumIR(elements.size)), "Arguments")
          val ves: List[Option[ValError]] = for (element <- elements) yield element match {
            case Some(element) => Some(walkExpr(element))
            case None => None
          }
          for (Some(err) <- ves if err.isInstanceOf[JSError]) {
            IS.comp.setThrow(err, ast.span)
            return node
          }
          // Now, there is no error.
          val vs: List[Option[Val]] = for (v <- ves) yield v.asInstanceOf[Option[Val]]
          for ((Some(vi), i) <- vs.view.zipWithIndex) {
            o.defineOwnProperty(i.toString, IH.mkDataProp(vi, true, true, true), false)
          }
          // (H'', A', tb), x = l
          IH.valError2NormalCompletion(IH.putValue(lhs, o, IS.strict))

        /*
       * 11.1.5 Object Initializer
       */
        case IRObject(ast, lhs, members, proto) =>
          IS.span = ast.span
          val o = IH.newObj()
          for (member <- members) walkMember(member) match {
            case err: JSError =>
              IS.comp.setThrow(err, ast.span); return node
            case (x: PName, op: ObjectProp) =>
              o.defineOwnProperty(x, op, false)
          }
          // (H', A, tb), x = l
          IH.valError2NormalCompletion(IH.putValue(lhs, o, IS.strict))

        case IREval(ast, lhs, arg) =>
          IS.span = ast.span
          walkExpr(arg) match {
            case v: Val =>
              IS.GlobalObject.eval(v, true)
              if (IS.comp.Type == CT.RETURN) IH.valError2NormalCompletion(IH.putValue(lhs, IS.comp.value, IS.strict))
            case err: JSError => IS.comp.setThrow(err, ast.span)
          }

        case n: IRNode =>
          System.out.println(n.toString: String) // for debugging
          IS.comp.setThrow(IP.nyiError, n.ast.span)

        case _ =>
          System.out.println(node.toString: String) // for debugging
          IS.comp.setThrow(IP.nyiError, IS.span)
      }
      node
    } catch {
      // TODO: Update Span
      case e: DefaultValueError => {
        IS.comp.setThrow(e.err, IS.span)
        node
      }
    } finally {
      IS.span = oldSpan
    }
  }
}
