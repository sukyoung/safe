/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.ModelManager
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.nodes_util.NodeRelation
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.nodes_util.EJSType
import kr.ac.kaist.jsaf.ShellParameters

class InstDetect(bugDetector: BugDetector) {
  val cfg           = bugDetector.cfg
  val typing        = bugDetector.typing
  val bugStorage    = bugDetector.bugStorage
  val bugOption     = bugDetector.bugOption
  val varManager    = bugDetector.varManager
  val stateManager  = bugDetector.stateManager
  val CommonDetect  = bugDetector.CommonDetect
  val libMode       = bugDetector.libMode
  val dtv           = bugDetector.params.command == ShellParameters.CMD_DTV_APP



  ////////////////////////////////////////////////////////////////
  // Bug Detection Main (check CFGInst)
  ////////////////////////////////////////////////////////////////

  def check(inst: CFGInst, cstate: CState): Unit = {
    val node      = cfg.findEnclosingNode(inst)
    val state     = typing.mergeState(cstate)
    val heap      = state._1
    val context   = state._2
    var readSet   = List[RWEntry]()
    var writeSet  = List[RWEntry]()

    // UnreachableCode check
    unreachableCodeCheck(node, inst, cstate)
    if (cstate.size == 0) return

    //println("NODE: " + node + "\tINSTRUNCTION := " + inst)
    inst match {
      case CFGAlloc(_, info, id, proto, _) =>
        readSet = proto match {
          case Some(p) => unreadExprCheck(info.getSpan, p)
          case None => List[RWEntry]() // pass
        }
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGAllocArg(_, info, id, _, _) =>
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGAllocArray(_, info, id, _, _) =>
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGAssert(_, info, expr, isOriginalCondExpr) =>
        conditionalBranchCheck(info, expr, isOriginalCondExpr)
        readSet = unreadExprCheck(info.getSpan, expr)   
      case CFGCatch(_, info, name) =>
      case CFGCall(_, info, fun, thisArg, args, _) =>
        callNonFunctionCheck(info.getSpan, fun)
        //defaultValueCheck(CALL_NORMAL, fun, thisArg, args)
        functionCheck(info.getSpan, CALL_NORMAL, fun, args)
        if (libMode) unreferencedFunctionCheck(info.getSpan, args)
        unusedFunctionCheck(info.getSpan, fun)
        readSet = List(fun, thisArg, args).foldLeft[List[RWEntry]](List())((list, e) => list ++ unreadExprCheck(info.getSpan, e))
        varyingTypeArgumentsCheck(info.getSpan, CALL_NORMAL, fun, args)
        wrongThisTypeCheck(info.getSpan, fun, thisArg)
      case CFGConstruct(_, info, cons, thisArg, args, _) =>
        callNonConstructorCheck(info.getSpan, cons)
        //defaultValueCheck(CALL_CONST, cons, thisArg, args)
        functionCheck(info.getSpan, CALL_CONST, cons, args)
        if (libMode) unreferencedFunctionCheck(info.getSpan, args)
        unusedFunctionCheck(info.getSpan, cons)
        readSet = List(cons, thisArg, args).foldLeft[List[RWEntry]](List())((list, e) => list ++ unreadExprCheck(info.getSpan, e))
        varyingTypeArgumentsCheck(info.getSpan, CALL_CONST, cons, args)
      case CFGDelete(_, info, id, expr) =>
        readSet = unreadExprCheck(info.getSpan, expr)
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGDeleteProp(_, info, id, obj, index) =>
        readSet = unreadExprCheck(info.getSpan, obj) ++ unreadExprCheck(info.getSpan, index)
        writeSet = unreadVariableCheck(info.getSpan, id, write)
        unreadPropertyReadCheck(info.getSpan, obj, index)
      case CFGExprStmt(_, info, id, expr) =>
        unreadExprFuncCheck(id, expr)
        readSet = unreadExprCheck(info.getSpan, expr)
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGFunExpr(_, info, id, name, fid, _, _, _) =>
        bugStorage.insertFunExpr(fid, inst.asInstanceOf[CFGFunExpr])
      case CFGInternalCall(_, info, id, fun, args, loc) =>
        (fun.toString, args, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) =>
            if (!id.getText.contains("<>fun<>")) accessingNullOrUndefCheck(info.getSpan, expr)
            primitiveToObjectCheck(info.getSpan, expr)
          case ("<>Global<>toNumber", List(expr), None) =>
            CommonDetect.convertToNumberCheck(node, inst, expr, null, false, null)
          case _ => Unit
        }
        readSet = args.foldLeft[List[RWEntry]](List())((list, arg) => list ++ unreadExprCheck(info.getSpan, arg))
        writeSet = unreadVariableCheck(info.getSpan, id, write)
      case CFGReturn(_, info, optExpr) =>
        readSet = optExpr match {
          case Some(expr) => unreadExprCheck(info.getSpan, expr)
          case None => List[RWEntry]() // pass
        }
      case CFGStore(_, info, obj, index, expr) =>
        readSet = unreadExprCheck(info.getSpan, expr)
        writeSet = unreadPropertyWriteCheck(info.getSpan, obj, index)
      case CFGThrow(_, info, expr) =>
        readSet = unreadExprCheck(info.getSpan, expr)
      case _ => Unit
    }
    //println("READ SET := " + readSet)
    //println("WRITESET := " + writeSet)
    bugStorage.updateRWMap(node, readSet, writeSet)



    ////////////////////////////////////////////////////////////////
    //  AccessingNullOrUndef Check
    ////////////////////////////////////////////////////////////////

    def accessingNullOrUndefCheck(span: Span, expr: CFGExpr): Unit = if (!dtv) {
      // Get the object name
      val objName = varManager.getUserVarAssign(expr) match {
        case name: BugVar0 => "'" + name.toString + "'"
        case _ => "an object"
      }

      // Check for each CState
      val bugCheckInstance = new BugCheckInstance
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(ObjectNullOrUndef))
      for ((callContext, state) <- mergedCState) {
        val objValue = SE.V(expr, state.heap, state.context)._1
        val pValue = objValue.pvalue

        val isBug1 = !bugOption.NullOrUndefined_OnlyWhenPrimitive || objValue.locset.isEmpty
        val isBug2 = if (bugOption.NullOrUndefined_OnlyNullOrUndefined) isOnlyNullUndef(pValue) else !isNotNullUndef(pValue)
        val checkInstance = bugCheckInstance.insert(isBug1 && isBug2, span, callContext, state)
        checkInstance.pValue = pValue

        //println(objName + " = " + objValue + " => " + isBug1 + " && " + isBug2 + ")")
      }

      // Filter out bugs depending on options
      if (!bugOption.NullOrUndefined_BugMustExistInEveryState) bugCheckInstance.filter((bug, notBug) => (bug.pValue == notBug.pValue))

      // Report bugs
      for (b <- bugCheckInstance.bugList) {
        (b.pValue.undefval, b.pValue.nullval) match {
          case (UndefTop, NullTop) => bugStorage.addMessage(span, ObjectNullOrUndef, inst, b.callContext, objName, "null (or undefined)")
          case (_, NullTop)        => bugStorage.addMessage(span, ObjectNullOrUndef, inst, b.callContext, objName, "null")
          case (UndefTop, _)       => bugStorage.addMessage(span, ObjectNullOrUndef, inst, b.callContext, objName, "undefined")
          case _ =>
        }
      }

      def isOnlyNullUndef(pv: PValue): Boolean = (pv.boolval <= BoolBot && pv.numval <= NumBot && pv.strval <= StrBot)
      def isNotNullUndef(pv: PValue): Boolean = (pv.undefval <= UndefBot && pv.nullval <= NullBot)
    }



    ////////////////////////////////////////////////////////////////
    //  CallNonFunction Check
    ////////////////////////////////////////////////////////////////

    def callNonFunctionCheck(span: Span, fun: CFGExpr): Unit = {
      // Get the function name
      val funId: String = varManager.getUserVarAssign(fun) match {
        case bv: BugVar0 => "the non-function '" + bv.toString + "'"
        case _ => "a non-function"
      }

      // Check for each CState
      val bugCheckInstance = new BugCheckInstance()
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(CallNonFunction))
      for ((callContext, state) <- mergedCState) {
        val funLocSet = SE.V(fun, state.heap, state.context)._1.locset

        // Check for each CState
        for (funLoc <- funLocSet) {
          val isCallable = Helper.IsCallable(state.heap, funLoc)
          //println(funId + " #" + funLoc + ": isCallable = " + isCallable)

          // Collect function's callablility
          val isBug = bugOption.CallNonFunction_MustBeCallableDefinitely match {
            case true => isCallable != BoolTrue
            case false => isCallable <= BoolFalse
          }
          val checkInstance = bugCheckInstance.insert(isBug, span, callContext, state)
          checkInstance.loc1 = funLoc
        }
      }

      // Filter out bugs depending on options
      if (!bugOption.CallNonFunction_MustBeCallableInEveryState) {
        bugCheckInstance.filter((bug, notBug) => (bug.loc1 == notBug.loc1))
      }
      if (!bugOption.CallNonFunction_MustBeCallableForEveryLocation) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state))
      }

      // Report bugs
      for (b <- bugCheckInstance.bugList) bugStorage.addMessage(span, CallNonFunction, inst, b.callContext, funId)
    }



    ////////////////////////////////////////////////////////////////
    //  CallNonConstructor Check
    ////////////////////////////////////////////////////////////////

    def callNonConstructorCheck(span: Span, const: CFGExpr): Unit = {
      // Get the function name
      val funId: String = varManager.getUserVarAssign(const) match {
        case bv: BugVar0 => "the non-constructor '" + bv.toString + "'"
        case _ => "a non-constructor"
      }

      // Check for each CState
      val bugCheckInstance = new BugCheckInstance()
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(CallNonConstructor))
      for ((callContext, state) <- mergedCState) {
        val funLocSet = SE.V(const, state.heap, state.context)._1.locset

        // Check for each CState
        for (funLoc <- funLocSet) {
          val hasConstruct = Helper.HasConstruct(state.heap, funLoc)
          //println(funId + " #" + funLoc + ": hasConstruct = " + hasConstruct)

          // Collect function's cunstructability
          val isBug = bugOption.CallNonConstructor_MustBeConstructableDefinitely match {
            case true => hasConstruct != BoolTrue
            case false => hasConstruct <= BoolFalse
          }
          val checkInstance = bugCheckInstance.insert(isBug, span, callContext, state)
          checkInstance.loc1 = funLoc
        }
      }

      // Filter out bugs depending on options
      if (!bugOption.CallNonConstructor_MustBeConstructableInEveryState) {
        bugCheckInstance.filter((bug, notBug) => (bug.loc1 == notBug.loc1))
      }
      if (!bugOption.CallNonConstructor_MustBeConstructableForEveryLocation) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state))
      }

      // Report bugs
      for (b <- bugCheckInstance.bugList) bugStorage.addMessage(span, CallNonConstructor, inst, b.callContext, funId)

      // Previous code
      /*val originalLocSet = SE.V(const, heap, context)._1._2
      val filteredLocSet = originalLocSet.filter((loc) => BoolTrue <= Helper.HasConstruct(heap, loc))
      originalLocSet.foreach((loc) => heap(loc)("@function")._1._3.foreach((fid) =>
          typing.builtinFset.get(fid) match {
            case Some(builtinName) =>
              if ((nonConsSet contains builtinName) || (filteredLocSet.size < originalLocSet.size))
                bugStorage.addMessage(span, CallNonConstructor, inst, null, filterUnnamedFunction(cfg.getFuncName(fid)))
            case None => Unit
      }))*/
    }



    ////////////////////////////////////////////////////////////////
    //  ConditionalBranch Check
    ////////////////////////////////////////////////////////////////

    def conditionalBranchCheck(info: Info, expr: CFGExpr, isOriginalCondExpr: Boolean): Unit = {
      //if (!isOriginalCondExpr) return
      val bugCheckInstance = new BugCheckInstance()
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(CondBranch))

      // Check for each CState
      for ((callContext, state) <- mergedCState) {
        // expr value
        val value: Value = SE.V(expr, state.heap, state.context)._1
        val pvalue: PValue = value.pvalue

        // undefined
        if (pvalue.undefval == UndefTop) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.UNDEFINED
          checkInstance.string1 = "undefined"
          checkInstance.string2 = "false"
        }
        // null
        if (pvalue.nullval == NullTop) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.NULL
          checkInstance.string1 = "null"
          checkInstance.string2 = "false"
        }
        // Boolean
        if (pvalue.boolval == BoolTrue || pvalue.boolval == BoolFalse) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.BOOLEAN
          checkInstance.string1 = pvalue.boolval.toString
          checkInstance.string2 = if (pvalue.boolval == BoolTrue) "true" else "false"
        }
        else if (pvalue.boolval == BoolTop) {
          val checkInstance = bugCheckInstance.insert(false, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.BOOLEAN
        }
        // Number
        if (pvalue.numval == Infinity || pvalue.numval == PosInf || pvalue.numval == NegInf || pvalue.numval == NaN || pvalue.numval == NUInt ||
            pvalue.numval.isInstanceOf[UIntSingle] || pvalue.numval.isInstanceOf[NUIntSingle]) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.NUMBER
          checkInstance.string1 = pvalue.numval.toString
          checkInstance.string2 = pvalue.numval match {
            case NaN => "false"
            case UIntSingle(n) if (n == 0) => "false"
            case NUIntSingle(n) if (n == 0) => "false"
            case _ => "true"
          }
        }
        else if (pvalue.numval == NumTop || pvalue.numval == UInt) {
          val checkInstance = bugCheckInstance.insert(false, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.NUMBER
        }
        // String
        if (pvalue.strval == NumStr || pvalue.strval.isInstanceOf[NumStrSingle] || pvalue.strval.isInstanceOf[OtherStrSingle]) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.STRING
          checkInstance.string1 = pvalue.strval.toString
          checkInstance.string2 = pvalue.strval match {
            case NumStrSingle(s) if (s == "") => "false"
            case OtherStrSingle(s) if (s == "") => "false"
            case _ => "true"
          }
        }
        else if (pvalue.strval == StrTop || pvalue.strval == OtherStr) {
          val checkInstance = bugCheckInstance.insert(false, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.STRING
        }
        // Object
        if (!value.locset.isEmpty) {
          val checkInstance = bugCheckInstance.insert(true, info.getSpan, callContext, state)
          checkInstance.valueType = EJSType.OBJECT
          checkInstance.string1 = "Object"
          checkInstance.string2 = "true"
        }
      }

      // Filter out bugs depending on options
      if (!bugOption.CondBranch_ConditionMustBeTrueOrFalseInEveryState) {
        bugCheckInstance.filter((bug, notBug) => bug.valueType == notBug.valueType)
      }
      if (!bugOption.CondBranch_ConditionMustBeTrueOrFalseForAllValue) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state))
      }

      // Expression must be only one boolean value(true or false)
      var result: AbsBool = BoolBot
      for (checkInstance <- bugCheckInstance.bugList) {
        result+= (checkInstance.string2 match {
          case "true" => BoolTrue
          case "false" => BoolFalse
        })
      }
      bugStorage.insertConditionMap(node, inst.asInstanceOf[CFGAssert], result)
      /*if (result == BoolBot || result == BoolTop) return

      // Report bugs
      bugCheckInstance.bugList.foreach((e) => bugStorage.addMessage(e.span, CondBranch, inst, e.callContext, e.string2,
        (if (e.string2 != "false" && e.string2 != "true") ", where its value is " + e.string1 else "") + "."))*/
    }



    ////////////////////////////////////////////////////////////////
    //  DefaultValue (called by main function)
    ////////////////////////////////////////////////////////////////

    /*
    def defaultValueCheck(isCall: Boolean, fun: CFGExpr, thisArg: CFGExpr, args: CFGExpr): Unit = {
      // For each function location
      for(funLoc <- SE.V(fun, heap, context)._1.locset) {
        // Function must have [[Function]] or [[Construct]] property
        val propertyName: String = BugHelper.getFuncOrConstPropName(state.heap, funLoc, isCall)
        if(propertyName != null) {
          // For each function id
          for(fid <- heap(funLoc)(propertyName)._1.funid) {
            typing.builtinFset.get(fid) match {
              case Some(builtinName) =>
                if (isCall) {
                  // ToString
                  if (builtinName == "String") CommonDetect.defaultValueCheck(inst, args, "String")
                  else if (toStringSet contains builtinName) CommonDetect.defaultValueCheck(inst, thisArg, "String")
                  // ToString:  JSON.stringify check when replacer, space or value is Object and its [[Class]] is String
                  //            or when replacer is Object and its [[Class]] is Number (what's replacer ??)
                  else if (builtinName == "JSON.strinify" && !SE.V(args, heap, context)._1.locset.subsetOf(LocSetBot)) {
                    for(loc <- SE.V(args, heap, context)._1.locset) {
                      val classProperty = heap(loc)("@class")._1.value.pvalue.strval
                      if (AbsString.alpha("String") == classProperty) CommonDetect.defaultValueCheck(inst, args, "String")
                      else if (AbsString.alpha("Number") == classProperty) CommonDetect.defaultValueCheck(inst, args, "Number")
                    }
                  }
                  // ToNumber
                  else if (builtinName == "Number") CommonDetect.defaultValueCheck(inst, args, "Number")
                  else if (toNumberSet contains builtinName) CommonDetect.defaultValueCheck(inst, args, "Number")
                }
                else {
                  if (builtinName == "String")      CommonDetect.defaultValueCheck(inst, args, "String")
                  else if (builtinName == "Number") CommonDetect.defaultValueCheck(inst, args, "Number")
                  else if (builtinName == "Date")   CommonDetect.defaultValueCheck(inst, args, "Number")
                }
              case None => Unit
            }
          }
        }
      }
    }
    */


    ////////////////////////////////////////////////////////////////
    //  BuiltinWrongArgType & FunctionArgSize
    ////////////////////////////////////////////////////////////////

    def functionCheck(span: Span, isCall: Boolean, fun: CFGExpr, args: CFGExpr): Unit = {
      // Check for each CState
      val bugCheckInstance = new BugCheckInstance()
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, CallContext._MOST_SENSITIVE)
      for ((callContext, state) <- mergedCState) {
        val funLocSet = SE.V(fun, state.heap, state.context)._1.locset
        val argLocSet = SE.V(args, state.heap, state.context)._1.locset

        // Check for each function location set
        for (funLoc <- funLocSet) {
          // Function must have [[Function]] or [[Construct]] property
          val propertyName: String = BugHelper.getFuncOrConstPropName(state.heap, funLoc, isCall)
          if (propertyName != null) {
            // Check for each function id set
            for (fid <- state.heap(funLoc)(propertyName)._1.funid) {
              // BuiltinWrongArgType
              ModelManager.getFIdMap("Builtin").get(fid) match {
                case Some(funName) =>
                  argTypeMap.get(funName) match {
                    case Some((argIndex, jsType)) =>
                      // Check bug options
                      var checkType = true
                      if (jsType == EJSType.OBJECT && !bugOption.BuiltinWrongArgType_CheckObjectType) checkType = false
                      if (jsType == EJSType.OBJECT_FUNCTION && !bugOption.BuiltinWrongArgType_CheckFunctionType) checkType = false

                      if (checkType) {
                        // Check for each argument location set
                        for (argLoc <- argLocSet) {
                          val arg = state.heap(argLoc)
                          val obj = arg(argIndex.toString)._1.objval.value
                          val isBug = jsType match {
                            case EJSType.OBJECT =>
                              bugOption.BuiltinWrongArgType_TypeMustBeCorrectForAllValue match {
                                case true => obj.locset.isEmpty || obj.pvalue </ PValueBot
                                case false => obj.locset.isEmpty
                              }
                            case EJSType.OBJECT_FUNCTION =>
                              bugOption.BuiltinWrongArgType_TypeMustBeCorrectForAllValue match {
                                case true => obj.locset.isEmpty || obj.locset.exists(loc => BoolTrue != state.heap(loc).domIn("@function")) || obj.pvalue </ PValueBot
                                case false => obj.locset.isEmpty || obj.locset.exists(loc => BoolFalse <= state.heap(loc).domIn("@function"))
                            }
                          }
                          val checkInstance = bugCheckInstance.insert(isBug, span, callContext, state)
                          checkInstance.bugKind = BuiltinWrongArgType
                          checkInstance.fid = fid
                          checkInstance.string1 = if (argIndex == 0) "First" else "Second"
                          checkInstance.string2 = jsType match {
                            case EJSType.OBJECT => "an object type"
                            case EJSType.OBJECT_FUNCTION => "a function type"
                          }
                        }
                      }
                    case None =>
                  }
                case None =>
              }

              // FunctionArgSize
              // Check for each argument location set
            if (!dtv)
              for (argLoc <- argLocSet) {
                state.heap(argLoc)("length")._1.objval.value.pvalue.numval match {
                  case UIntSingle(n) =>
                    // Get argument size range
                    var argSize: (Int, Int) = (-1, -1)
                    ModelManager.getFuncName(fid) match {
                      case funcName: String =>
                        // Model function
                        argSizeMap.get(funcName) match {
                          case Some(as) => argSize = as
                          case None => println("* Unknown argument size of \"" + funcName + "\".")
                        }
                      case _ =>
                        // User function
                        val userFuncArgSize = cfg.getArgVars(fid).length
                        argSize = (userFuncArgSize, userFuncArgSize)
                    }

                    if (argSize != (-1, -1)) {
                      val comp: String = (if (n < argSize._1) "few" else if (n > argSize._2) "many" else null)
                      val checkInstance = bugCheckInstance.insert(comp != null, span, callContext, state)
                      checkInstance.bugKind = FunctionArgSize
                      checkInstance.fid = fid
                      checkInstance.string1 = comp
                    }
                  case _ =>
                }
              }
            }
          }
        }
      }

      // Filter out bugs depending on options
      if (!bugOption.BuiltinWrongArgType_TypeMustBeCorrectInEveryState) {
        bugCheckInstance.filter((bug, notBug) => (bug.bugKind == BuiltinWrongArgType && bug.bugKind == notBug.bugKind && bug.fid == notBug.fid && bug.string1 == notBug.string1 && bug.string2 == notBug.string2))
      }

      // Report bugs
      for (b <- bugCheckInstance.bugList) {
        b.bugKind match {
          case BuiltinWrongArgType =>
            bugStorage.addMessage(span, b.bugKind, inst, b.callContext, b.string1, BugHelper.getFuncName(cfg.getFuncName(b.fid), varManager, fun), b.string2)
          case FunctionArgSize =>
            bugStorage.addMessage(span, b.bugKind, inst, b.callContext, b.string1, BugHelper.getFuncName(cfg.getFuncName(b.fid), varManager, fun))
        }
      }
    }



    ////////////////////////////////////////////////////////////////
    //  PrimitiveToObject Check
    ////////////////////////////////////////////////////////////////

    def primitiveToObjectCheck(span: Span, expr: CFGExpr): Unit = {
      // Check for each CState
      val bugCheckInstance = new BugCheckInstance()
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, bugOption.contextSensitive(PrimitiveToObject))
      for ((callContext, state) <- mergedCState) {
        // expr value
        val value: Value = SE.V(expr, state.heap, state.context)._1
        val pvalue: PValue = value.pvalue

        // undefined (type error)
        //if (pvalue.undefval != UndefBot) bugCheckInstance.insertWithStrings(true, span, callContext, state, "undefined")
        // null (type error)
        //if (pvalue.nullval != NullBot) bugCheckInstance.insertWithStrings(true, span, callContext, state, "null")
        // boolean
        if (pvalue.boolval != BoolBot) bugCheckInstance.insertWithStrings(true, span, callContext, state, "boolean")
        // number
        if (pvalue.numval != NumBot) bugCheckInstance.insertWithStrings(true, span, callContext, state, "number")
        // string
        if (pvalue.strval != StrBot) {
          if (bugOption.PrimitiveToObject_CheckEvenThoughPrimitiveIsString) bugCheckInstance.insertWithStrings(true, span, callContext, state, "string")
          else bugCheckInstance.insert(false, span, callContext, state)
        }
        // Object
        if (!value.locset.isEmpty) bugCheckInstance.insert(false, span, callContext, state)
      }

      // Filter out bugs depending on options
      if (!bugOption.PrimitiveToObject_PrimitiveMustBeConvertedInEveryState) {
        bugCheckInstance.filter((bug, notBug) => true)
      }
      if (!bugOption.PrimitiveToObject_PrimitiveMustBeConvertedForAllValue) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state))
      }

      // Group by CState to collect types
      bugCheckInstance.group(checkInstance => (checkInstance.callContext, checkInstance.state).hashCode)

      for ((_, checkInstanceList) <- bugCheckInstance.groupedBugList) {
        // Collect types
        var types = ""
        for (checkInstance <- checkInstanceList) {
          if (types.length() == 0) types = checkInstance.string1
          else types+= ", " + checkInstance.string1
        }
        // Report bugs
        if (checkInstanceList.length > 0) bugStorage.addMessage(span, PrimitiveToObject, inst, checkInstanceList.head.callContext, types)
      }
    }



    ////////////////////////////////////////////////////////////////
    //  UnreachableCode Check
    ////////////////////////////////////////////////////////////////

    def unreachableCodeCheck(cfgNode: Node, cfg: CFGInst, cstate: CState): Unit = {
      if (cfg.isInstanceOf[CFGNoOp]) return

      // Get AST
      val ast = NodeRelation.cfg2astMap.get(cfg) match {
        case Some(ast) => ast.asInstanceOf[kr.ac.kaist.jsaf.nodes.ASTNode]
        case None => return
      }

      // Debug
      /*val ir = NodeRelation.cfg2irMap(cfg)
      println(ast.getInfo.getSpan)
      println("    AST(" + ast.getClass + "): " + kr.ac.kaist.jsaf.nodes_util.JSAstToConcrete.doit(ast))
      println("    IR(" + ir.getClass + "): " + new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(ir).doit)
      println("    CFG(" + cfg.getClass + "): [" + cfg.getInstId + "] " + cfg)
      println("    cstate.size = " + cstate.size)*/

      // Insert
      //println(cfgNode + ": [" + cfg.getInstId + "] " + cfg + ", cstate.size = " + cstate.size)
      bugStorage.insertReachabilityAST(ast, cfgNode, cstate.size > 0)
    }



    ////////////////////////////////////////////////////////////////
    //  UnreferencedFunction Check 
    ////////////////////////////////////////////////////////////////

    def unreferencedFunctionCheck(span: Span, args: CFGExpr): Unit = {
      val argLocSet = SE.V(args, heap, context)._1._2
      val argObj = argLocSet.foldLeft(ObjBot)((obj, loc) => obj + heap(loc))
      val argLen = argObj("length")._1._1._1._1._4
      argLen match {
        case UIntSingle(n) => (0 to (n.toInt - 1)).foreach((i) => argObj(i.toString)._1._1._1._2.foreach((l) => 
          heap(l)("@function")._1._3.foreach((fid) => bugStorage.appendUsedFunction(fid))))
        case _ => Unit 
      }
    }



    ////////////////////////////////////////////////////////////////
    //  UnusedFunction Check 
    ////////////////////////////////////////////////////////////////

    def unusedFunctionCheck(span: Span, fun: CFGExpr): Unit = {
      val fval = SE.V(fun, heap, context)._1
      fval._2.foreach((loc) => heap(loc)("@function")._1._3.foreach((fid) => bugStorage.appendUsedFunction(fid)))
    }



    ////////////////////////////////////////////////////////////////
    //  UnreadExpr Check (only Function Expression)
    ////////////////////////////////////////////////////////////////

    def unreadExprFuncCheck(id: CFGId, expr: CFGExpr): Unit = {
      val name = id match { case CFGUserId(_,n,_,_,_) => n  case CFGTempId(t,_) => t }
      expr match {
        case CFGVarRef(info, id) => id match {
          case CFGTempId(text,_) => 
            Helper.LookupBase(heap, id).foreach((l1) => heap(l1)(text)._1._1._1._2.foreach((l2) => 
              if (BoolTrue == heap(l2).domIn("@function")) heap(l1)("@this")._1._2._2.foreach((loc) => bugStorage.updateFuncSet(loc, name))))
          case _ => // pass 
        } case _ => // pass
      }
    }



    ////////////////////////////////////////////////////////////////
    //  UnreadVarProp Check (Expression Check)
    ////////////////////////////////////////////////////////////////

    def unreadExprCheck(span: Span, expr: CFGExpr): List[RWEntry] = {
      expr match {
        case CFGBin(info, first, op, second) => unreadExprCheck(span, first) ++ unreadExprCheck(span, second)
        case CFGLoad(info, obj, index) => unreadExprCheck(span, obj) ++ unreadExprCheck(span, index) ++ unreadPropertyReadCheck(span, obj, index)
        case CFGUn(info, op, first) => unreadExprCheck(span, first)
        case CFGVarRef(info, id) => unreadVariableCheck(span, id, read)
        case _ => List() // pass
      }
    }



    ////////////////////////////////////////////////////////////////
    //  UnreadVariable Check (Store Read or Write)
    ////////////////////////////////////////////////////////////////

    def unreadVariableCheck(span: Span, id: CFGId, rwflag: Boolean): List[RWEntry] = id match {
      case CFGUserId(_, name, _, originalName, _) => 
        if (bugStorage.isInternalName(name)) bugStorage.updateNameMap(name, originalName)
        Helper.LookupBase(heap, id).foldLeft[List[RWEntry]](List())((list, loc) => list :+ (rwflag, variable, loc, name, span))
      case CFGTempId(_, _) => List() // pass
    }



    ////////////////////////////////////////////////////////////////
    //  UnreadProperty Check (Store Read)
    ////////////////////////////////////////////////////////////////

    def unreadPropertyReadCheck(span: Span, obj: CFGExpr, index: CFGExpr): List[RWEntry] = {
      if (obj.isInstanceOf[CFGVarRef] && obj.asInstanceOf[CFGVarRef].id.isInstanceOf[CFGTempId]) return List()
      val s = SE.V(index, heap, context)._1._1._5
      val locSet = SE.V(obj, heap, context)._1._2
      val locSetBase = locSet.foldLeft(LocSetBot)((locset, loc) => locset ++ Helper.ProtoBase(heap, loc, s))
      locSetBase.foldLeft[List[RWEntry]](List())((list, loc) => 
        BugHelper.props(heap, loc, s).foldLeft[List[RWEntry]](list)((l, name) => l :+ (read, property, loc, name, span))
      )
    }



    ////////////////////////////////////////////////////////////////
    //  UnreadProperty Check (Store Write)
    ////////////////////////////////////////////////////////////////

    def unreadPropertyWriteCheck(span: Span, obj: CFGExpr, index: CFGExpr): List[RWEntry] = {
      if (obj.isInstanceOf[CFGVarRef] && obj.asInstanceOf[CFGVarRef].id.isInstanceOf[CFGTempId]) return List()
      val s = SE.V(index, heap, context)._1._1._5
      val locSet = SE.V(obj, heap, context)._1._2
      locSet.foldLeft[List[RWEntry]](List())((list, loc) => AbsString.concretize(s) match {
        case Some(name) => list :+ (write, property, loc, name, span)
        case None => list // Ignore StrTop, NumStr and OtherStr (UNSOUND)
      })
    }



    ////////////////////////////////////////////////////////////////
    //  VaryingTypeArguments Check
    ////////////////////////////////////////////////////////////////

    def varyingTypeArgumentsCheck(span: Span, isCall: Boolean, fun: CFGExpr, args: CFGExpr): Unit = {
      // Check for each CState
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, CallContext._MOST_SENSITIVE)
      for ((callContext, state) <- mergedCState) {
        val funLocSet = SE.V(fun, state.heap, state.context)._1.locset
        val argLocSet = SE.V(args, state.heap, state.context)._1.locset

        // Check for each function location set
        for (funLoc <- funLocSet) {
          // Function must have [[Function]] or [[Construct]] property
          val propertyName: String = BugHelper.getFuncOrConstPropName(state.heap, funLoc, isCall)
          if (propertyName != null) {
            val fidSet = state.heap(funLoc)(propertyName)._1.funid
            for (fid <- fidSet) {
              for (argLoc <- argLocSet) {
                val argObj = state.heap(argLoc)
                bugStorage.updateDetectedFuncMap(fid, fun, argObj, span)
              }
            }
          }
        }
      }
    }



    ////////////////////////////////////////////////////////////////
    //  WrongThisType Check
    ////////////////////////////////////////////////////////////////

    def wrongThisTypeCheck(span: Span, fun: CFGExpr, thisArg: CFGExpr): Unit = {
      // (State, FunctionLoc, FunctionId, ThisLoc, IsBug)

      // Check for each CState
      val bugCheckInstance = new BugCheckInstance
      val mergedCState = stateManager.getInputCState(node, inst.getInstId, CallContext._MOST_SENSITIVE)
      for((callContext, state) <- mergedCState) {
        // For each function loc
        for(funLoc <- SE.V(fun, state.heap, state.context)._1.locset) {
          // For each function id
          for(fid <- state.heap(funLoc)("@function")._1.funid) {
            // Is this a native function?
            ModelManager.getFuncName(fid) match {
              case funName: String =>
                // Has this native function to be checked?
                thisTypeMap.get(funName) match {
                  case Some(expectedClassName) =>
                    // For each this loc
                    val thisLocSet = Helper.getThis(state.heap, SE.V(thisArg, state.heap, state.context)._1)
                    for(thisLoc <- thisLocSet) {
                      state.heap(thisLoc)("@class")._1.value.pvalue.strval.getConcreteValue match {
                        case Some(thisClassName) =>
                          // Debug
                          //println("Native function name = " + funName + ", thisLoc = " + thisLoc + ", this.@class = " + thisClassName + ", expected @class = " + expectedClassName)

                          val isBug = (thisClassName != expectedClassName)
                          val checkInstance = bugCheckInstance.insert(isBug, span, callContext, state)
                          checkInstance.loc1 = funLoc
                          checkInstance.loc2 = thisLoc
                          checkInstance.fid = fid
                          checkInstance.string1 = funName
                        case None =>
                      }
                    }
                  case None =>
                }
              case _ =>
            }
          }
        }
      }

      // Filter out bugs depending on options
      if(!bugOption.WrongThisType_TypeMustBeWrongInEveryState) {
        bugCheckInstance.filter((bug, notBug) => (bug.loc1 == notBug.loc1 && bug.loc2 == notBug.loc2 && bug.fid == notBug.fid))
      }
      if(!bugOption.WrongThisType_TypeMustBeWrongInEveryFunctionId) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state && bug.loc1 == notBug.loc1 && bug.loc2 == notBug.loc2))
      }
      if(!bugOption.WrongThisType_TypeMustBeWrongInEveryFunctionLocation) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state && bug.loc2 == notBug.loc2 && bug.fid == notBug.fid))
      }
      if(!bugOption.WrongThisType_TypeMustBeWrongInEveryThisLocation) {
        bugCheckInstance.filter((bug, notBug) => (bug.callContext == notBug.callContext && bug.state == notBug.state && bug.loc1 == notBug.loc1 && bug.fid == notBug.fid))
      }

      // Report bugs
      for(b <- bugCheckInstance.bugList) bugStorage.addMessage(b.span, WrongThisType, inst, b.callContext, b.string1)

      /* Previous code
      val objLocSet = SE.V(fun, heap, context)._1._2
      objLocSet.foreach((loc) => heap(loc)("@function")._1._3.foreach((fid) =>
        typing.builtinFset.get(fid) match {
          case Some(builtinName) =>
            if (thisTypeMap contains builtinName) {
              val thisLocs = Helper.getThis(heap, SE.V(thisArg, heap, context)._1)
              thisLocs.foreach((loc) => if (heap(loc)("@class")._1._2._1._5 != thisTypeMap(builtinName)) 
                bugStorage.addMessage(span, WrongThisType, inst, null, builtinName))
            }
          case None => Unit
      }))
      */
    }
  }
}
