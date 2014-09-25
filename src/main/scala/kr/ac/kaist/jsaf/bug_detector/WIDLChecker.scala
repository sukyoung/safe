/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.{ HashMap => MHashMap, ListBuffer => MListBuffer }
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.CallContext._
import kr.ac.kaist.jsaf.analysis.typing.{ SemanticsExpr => SE }
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.widl.{WIDLTypeMap, WIDLToString, WIDLHelper}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ IRFactory, NodeRelation, Span }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import java.util.{List => JList}

// libraries = ["webapis.tv.channel", ...]
class WIDLChecker(bugDetector: BugDetector) extends Walker {
  ////////////////////////////////////////////////////////////////////////////////
  // Analysis
  ////////////////////////////////////////////////////////////////////////////////
  val cfg           = bugDetector.cfg
  val typing        = bugDetector.typing
  val semantics     = bugDetector.semantics
  val stateManager  = bugDetector.stateManager

  val bugStorage    = bugDetector.bugStorage
  val bugOption     = bugDetector.bugOption

  var argObj: Obj = null
  var argState: State = null

  ////////////////////////////////////////////////////////////////////////////////
  // Soundness for argument type checking
  ////////////////////////////////////////////////////////////////////////////////
  val soundness = false

  ////////////////////////////////////////////////////////////////////////////////
  // ...
  ////////////////////////////////////////////////////////////////////////////////

  def getType(typ: WType): Option[String] = typ match {
    case SWNamedType(_, _, n) => Some(n)
    case SWArrayType(_, _, t) => getType(t) match {
      case Some(n) => Some(n+"[]")
      case _ => Some("[]")
    }
    case _ => None // Not yet implemented : In case of other types...?
  }

  def isErrorCallback(typ: WType): Boolean = getType(typ) match {
    case Some(t) => t.endsWith("ErrorCallback")
    case None => false
  }

  def isOptional(attr: WEAttribute): Boolean = attr match {
    case SWEAOptional(_) => true
    case _ => false
  }

  // check if the [name]d property is in the [dic]tionary
  def contains(dic: WDictionary, name: String): Boolean = {
    for (i <- 0 until dic.getMembers.size)
      if (dic.getMembers.get(i).getName == name) return true
    false
  }

  // check if the [name]d value is in the [enum] list
  def contains(enum: WEnum, name: String): Boolean = {
    for (i <- 0 until enum.getEnumValueList.size)
      if (enum.getEnumValueList.get(i).getStr == name) return true
    false
  }

  // retrieve the [name]ed property from the [dic]ionary
  def getFromDic(dic: WDictionary, name: String): Option[WDictionaryMember] = {
    val lst: JList[WDictionaryMember] = dic.getMembers()
    for (i <- 0 until lst.size){
      val mem: WDictionaryMember = lst.get(i)
      if (mem.getName == name) return Some(mem)
    }
    None
  }

  // return Some(_) if concrete value exists
  //        None if we cannot know
  //        null if we wanna report a bug (functions, arrays)
  def toDOMString(given: PropValue): Option[String] = {
    var result: Option[String] = null
    def setResult(str: String): Unit = result match {
      case Some(old) => if (old != str) result = None
      case None => result = None
      case _ => result = Some(str)
    }
    val undefval = given.objval.value.pvalue.undefval
    val nullval = given.objval.value.pvalue.nullval
    val boolval = given.objval.value.pvalue.boolval
    val strval = given.objval.value.pvalue.strval
    val numval = given.objval.value.pvalue.numval
    val locset = given.objval.value.locset
    if (undefval.isConcrete) setResult("undefined")
    if (nullval.isConcrete) setResult("null")
    boolval match {
      case BoolTop => result = None // cannot know
      case BoolTrue => setResult("true")
      case BoolFalse => setResult("false")
      case BoolBot =>
    }
    strval match {
      case StrTop => result = None
      case StrBot =>
      case _ => strval.getSingle match {
        case Some(s) => setResult(s)
        case _ => result = None
      }
    }
    numval match {
      case NumBot =>
      case PosInf => setResult("Infinity")
      case NegInf => setResult("-Infinity")
      case NaN => setResult("NaN")
      case _ => numval.getSingle match {
        case Some(n) => setResult(n.toString)
        case _ => result = None
      }
    }
    for (loc <- locset) {
      argState.heap(loc)("@class")._1.value.pvalue.strval.getSingle match {
        case Some(str) => str match {
          case "Function" => // TODO: get function body as string if possible
          case "Array" => {
            /* commented out because absent property cannot be handled well.
            argState.heap(loc)("length").objval.value.pvalue.numval match {
              case UIntSingle(v) => { // array to string
                var arr: String = ""
                var comma: Boolean = false
                for (i <- 0 until v.toInt){
                  if (comma) arr += ","
                  argState.heap(loc)(i.toString) match {
                    case Obj.bottom => // absent property
                    case obj =>
                      obj._2 match {
                        case AbsentTop => result = None // property can be absent
                        case AbsentBot => toDOMString(obj._1) match {
                          case Some(str) => if (str != "undefined" && str != "null") arr += str
                          case _ => result = None
                        }
                      }
                  }
                  comma = true
                }
                setResult(arr)
              }
              case tmp@_ => result = None
            }
            */
          }
          case _ => setResult("[object Object]")
        }
        case _ => result = None
      }
    }
    result
  }

  // used in overload resolution algorithm
  def isUndefined(v: Option[(PropValue, Absent)]): Boolean = v match {
    case Some((p, _)) =>
      val undefval = p.objval.value.pvalue.undefval
      val nullval  = p.objval.value.pvalue.nullval
      val boolval  = p.objval.value.pvalue.boolval
      val strval   = p.objval.value.pvalue.strval
      val numval   = p.objval.value.pvalue.numval
      val locset   = p.objval.value.locset
      val notAllBottom = !undefval.isBottom || !nullval.isBottom || !boolval.isBottom || !strval.isBottom || !numval.isBottom || !locset.isEmpty
      if (notAllBottom) undefval.isTop
      else true
    case _ => true
  }
  /*
   * overload resolution algorithm (from WIDL 4.4.1.1)
   * input  - S: an effective overload set
   *        - arg[0...n-1]: a list of ECMAScript values
   * output - one of S's entries
   *        - a list of IDL values
   */
  def overloadResolution(_S: List[JList[WArgument]], argObj: Obj): Set[JList[WArgument]] = { //TODO: refactoring arg type
    // 1. Let maxarg be the length of the longest type list of the entries in S.
    val maxarg: Int = _S.foldLeft(0)((res, arg) =>
      if (arg.size > res) arg.size else res)
    // 2. Initialize argcount to be min(maxarg, n).
    val n: Int = argObj("length")._1._1._1._4.getSingle match {
      case Some(len) => len.toInt
      case _ =>
        System.out.println("* WARNING: the length of argument is not single number *")
        maxarg // TODO: is it possible?
    }
    val argcount: Int = if (maxarg < n) maxarg else n
    /*
    // 3. Initialize j to n−1.
    var j: Int = n - 1
    // 4. While j ≥ 0 and argj is undefined:
    while (j >= 0 && isUndefined(argObj.map.get(j.toString))) {
      System.out.println(j)
      // 4. 1. If S has one or more entries whose type list is of length j, then:
      if (_S.exists(arg => arg.size == j)) {
        // 4. 1. 1. If S has one or more entries whose type list is longer than j,
        // and at least one of those entries has the [TreatUndefinedAs=Missing] extended attribute on its arguments at indices j and above,
        // then set argcount to j.
        for (entry <- _S) {
          val m: Int = entry.size
          System.out.println(j until m)
          if (m >= j){
            for (i <- j until m){
              val attr_list: JList[WEAttribute] = entry.get(j).getAttributes
              for (attr_index <- 0 until attr_list.size){
                val attr: WEAttribute = attr_list.get(attr_index)
                attr match {
                  case SWAttribute(_, _, typ, na, _) =>
                    // TODO: there is no element which has [TreatUndefinedAs=Missing] extended attribute.
                  case _ => System.out.println("Hi?") // DEBUG
                }
              }
            }
          }
        }
      }
      // 4. 2. Set j to j−1.
      j = j - 1
    }
    */
    // 5. Initialize k to argcount.
    val k: Int = argcount
    // 6. Remove from S all entries whose type list is not of length argcount.
    val S: Set[JList[WArgument]] = _S.filter(arg => arg.size == argcount).toSet
    // 7. If S is empty, then throw a TypeError.
    if (S.isEmpty) return Set() // TODO: no matched constructor found
    // 8. Initialize d to −1.
    var d: Int = -1
    // 9. If there is more than one entry in S, then set d to be the distinguishing argument index for the entries of S.
    if (S.size > 1) {
      // TODO: 9
      d = argcount
    }
    // 10. Initialize values to be an empty list of IDL values.
    var paramss: Set[JList[WArgument]] = Set()
    // 11. Initialize i to 0.
    var i: Int = 0
    // 12. While i < d:
    while (i < d) {
      // 12. 1. Let V be argi.
      val V = argObj(i.toString)
      // 12. 2. Let type be the type at index i in the type list of any entry in S.
      paramss.head.get(i) match {
        case SWArgument(_, attrs, t, _, _) =>
      // 12. 3. Append to values the result of converting V to IDL type type.
          checkType(V, t) // TODO: 에러 내야 댐
      }
      // 12. 4. Set i to i + 1.
      i = i + 1
    }
    var resS: Set[JList[WArgument]] = Set()
    // 13. If i = d, then:
      // 13. 1. Let V be argi.
    val V = argObj(i.toString)

    val undefval = V.objval.value.pvalue.undefval
    val nullval  = V.objval.value.pvalue.nullval
    val boolval  = V.objval.value.pvalue.boolval
    val strval   = V.objval.value.pvalue.strval
    val numval   = V.objval.value.pvalue.numval
    val locset   = V.objval.value.locset
      // 13. 2. If V is null or undefined, and any entry in S has a nullable type or a union type
      // that includes a nullable type at position i of its type list, then remove from S all other entries.
    if (undefval.isTop || nullval.isTop) {
      resS ++= S.filter(params => params.get(i) match {
        case SWArgument(_, attrs, t, _, _) if attrs.exists(attr => attr.isInstanceOf[WEAQuestion]) =>
          true
        case _ =>
          false
      })
    }
      // 13. 3. Otherwise: if V is a platform object – but not a platform array object – and there is an entry in S that has one of the following types at position i of its type list,
        // * an interface type that V implements
        // * object
        // * a nullable version of any of the above types
        // * a union type or a nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 4. Otherwise: if V is a platform array object, a native Array object, or a platform object that supports indexed properties; and there is an entry in S that has one of the following types at position i of its type list,
        // * an array type
        // * a sequence type
        // * object
        // * a nullable version of any of the above types
        // * a union type or nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 5. Otherwise: if V is a Date object and there is an entry in S that has one of the following types at position i of its type list,
        // * Date
        // * object
        // * a nullable version of either of the above types
        // * a union type or nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 6. Otherwise: if V is any other type of object and there is an entry in S that has one of the following types at position i of its type list,
        // * a callback interface type
        // * a callback function type
        // * a dictionary type
        // * object
        // * a nullable version of any of the above types
        // * a union type or nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 7. Otherwise: if there is an entry in S that has one of the following types at position i of its type list,
        // * DOMString
        // * an enumeration type
        // * a nullable version of any of the above types
        // * a union type or nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 8. Otherwise: if there is an entry in S that has one of the following types at position i of its type list,
        // * a primitive type
        // * a nullable primitive type
        // * a union type or nullable union type that has one of the above types in its flattened member types
        // then remove from S all other entries.
      // 13. 9. Otherwise: if there is an entry in S that has any at position i of its type list, then remove from S all other entries.
      // 13. 10. Otherwise: throw a TypeError.
    // 14. While i < argcount:
      // 14. 1. Let V be argi.
      // 14. 2. Let type be the type at index i in the type list of the single entry in S.
      // 14. 3. Append to values the result of converting V to IDL type type.
      // 14. 4. Set i to i + 1.
    // 15. Let callable be the operation or extended attribute of the single entry in S.
    // 16. While i is less than the length of the number of arguments callable is declared to take, and the argument at index i is declared with a default value:
      // 16. 1. Append to values that default value.
      // 16. 2. Set i to be i + 1.
    // 17. Return the pair <callable, values>.
    S
  }

  /*
   * checkType: check if [given] type is matched with [expected] type
   * Actually, argState, argObj are arguments of checkType
   */
  def checkType(given: PropValue, expected: WType): (Boolean, String) = { // TODO: detect given type
    val undefval = given.objval.value.pvalue.undefval
    val nullval = given.objval.value.pvalue.nullval
    val boolval = given.objval.value.pvalue.boolval
    val strval = given.objval.value.pvalue.strval
    val numval = given.objval.value.pvalue.numval
    val locset = given.objval.value.locset
    val notAllBottom = !undefval.isBottom || !nullval.isBottom || !boolval.isBottom || !strval.isBottom || !numval.isBottom || !locset.isEmpty
    val notInf = !(numval == PosInf || numval == NegInf || numval == NaN || numval == Infinity)
    val notOtherStr = !(strval.isAllOthers)
    val intResult = // TODO: consider object case (it depends the result of toString() or valueOf()
      if (soundness)
        locset.isEmpty
      else
        notAllBottom
    val floatResult = // TODO: consider object case (it depends the result of toString() or valueOf(), and empty string
      if (soundness)
        undefval.isBottom && !(strval </ NumStr) && notInf && locset.isEmpty
      else
        !nullval.isBottom || !boolval.isBottom || (!strval.isBottom && notOtherStr) || !numval.isBottom || !locset.isEmpty
    if (WIDLHelper.isNullable(expected) && !nullval.isBottom) return (true, "Null")
    def checkArrayType(elty: WType): Boolean = {
      var result = !locset.isEmpty
      val h: Heap = argState.heap
      for (loc <- locset) {
        val locres: Boolean = (BoolTrue <= Helper.IsArray(h, loc)) match {
          case true => {
            val _arrSize = h(loc)("length").objval.value.pvalue.numval
            var locres = true
            if (_arrSize.isBottom) {
              // not an array
              locres = false
            } else {
              try {
                val arrSize = _arrSize.getConcreteValueAsString("0").toString.toInt

                // TODO Need to be check.
                locres = locres && checkType(h(loc)(Str_default_number), elty)._1
                for (idx <- (0 until arrSize)) {
//                  if (AbsentTop </ h(loc).lookup(idx.toString)._2) {
                    locres = locres && checkType(h(loc)(idx.toString), elty)._1
//                  }
                }
              } catch {
                case e: Throwable => {
//                  if (AbsentTop </ h(loc).lookup(Str_default_number)._2)
                    locres = locres && checkType(h(loc)(Str_default_number), elty)._1
                  for (prop <- argState.heap(loc).getProps) {
                    try {
                      prop.toInt // check if prop is integer or not
                      locres = locres && checkType(h(loc)(prop), elty)._1
                    } catch {
                      case e: Throwable =>
                    }
                  }
                }
              }
            }
            locres
          }
          case false => false // not an array
        }
        result = result && locres // we report errors only if there are errors in all possible locations
      }
      result
    }
    expected match {
      case SWAnyType(info, suffix) => (true, "DEBUG")
      case SWNamedType(info, suffix, name2) => name2 match {
        case "any" => (true, "any")
        case "boolean" => (true, "boolean")
        case "byte" => (intResult, "byte")
        case "octet" => (intResult, "octet")
        case "short" => (intResult, "short")
        case "unsigned short" => (intResult, "unsigned short")
        case "long" => (intResult, "long")
        case "unsigned long" => (intResult, "unsigned long")
        case "long long" => (intResult, "long long")
        case "unsigned long long" => (intResult, "unsigned long long")
        case "float" => (floatResult, "float")
        case "unrestricted float" => (floatResult, "unrestricted float")
        case "double" => (floatResult, "double")
        case "unrestricted double" => (floatResult, "unrestricted double")
        case "DOMString" => (true, "DOMString")
        case "Date" => {
          var result: Boolean = true
          for (loc <- locset) {
            if (AbsString.alpha("Date") </ argState.heap(loc)("@class")._2._1._5) {
              result = false
            }
          }
          (result, "Date")
        }
        case _enum_ if WIDLTypeMap.enumMap.contains(_enum_) => {
          //println("enum " + _enum_)
          val enum = WIDLTypeMap.enumMap(_enum_)
          toDOMString(given) match {
            case Some(str) =>
              (contains(enum, str), _enum_)
            case None => // cannot know
              (!soundness, _enum_)
            case null => // buggy case
              (false, _enum_)
          }
        }
        case _dic_ if WIDLTypeMap.dictionaryMap.contains(_dic_) => {
          //println("dic " + _dic_)
          val dic = WIDLTypeMap.dictionaryMap(_dic_)
          var result = true
          for (loc <- locset) {
            var locres: Boolean = true // check if there are absent properties in this loc
            for (prop <- argState.heap(loc).getProps) {
              getFromDic(dic, prop) match {
                case None => // no property named "[prop]" in the [dic]tionary
                  locres = false
                case Some(mem) => // check if the type of the [mem]ber fits with the specification
                  locres = locres && checkType(argState.heap(loc)(prop), mem.getTyp)._1
              }
            }
            result = result && locres // we report errors only if there are errors in all possible locations
          }
          (result, _dic_)
        }
        case _interface_ if WIDLTypeMap.interfaceMap.contains(_interface_) => { // callback without FunctionOnly
          //println("interface " + _interface_)
          val interface = WIDLTypeMap.interfaceMap(_interface_)
          var result = false
          val interfaceMemberTypeMap = new MHashMap[String, Option[WType]]
          for (mem <- toList(interface.getMembers)) {
            mem match {
              case SWConst(_, _, t, n, _) => interfaceMemberTypeMap.update(n, Some(t))
              case SWAttribute(_, _, t, n, _) => interfaceMemberTypeMap.update(n, Some(t))
              case SWOperation(_, _, _, t, n, args, _) => n match {
                case Some(n) => interfaceMemberTypeMap.update(n, None)
                case _ =>
              }
              case _ =>
            }
          }
          /* checkWithProto:
           *   follows every possible prototype chain, and checks if every specified property exists
           *   it returns false if every possible prototype chain has faults
           */
          def checkWithProto(ls: LocSet, props: Set[String]): Boolean = {
            if (ls.isEmpty) {
              !interfaceMemberTypeMap.keySet.exists(attr => !props.contains(attr))
            } else {
              ls.foldLeft[Boolean](false)((res: Boolean, loc: Loc) =>
                argState.heap(loc).getProps.foldLeft[Boolean](true)((typecorrect:Boolean, prop:String) =>
                  interfaceMemberTypeMap.get(prop) match {
                    case Some(typ) => typ match {
                      case Some(typ) =>
                        typecorrect && checkType(argState.heap(loc)(prop), typ)._1
                      case None =>
                        typecorrect // TODO? check function type
                    } // type check for properties that the current location has
                    case None => typecorrect
                  }) match { // if the current object has no problem, check the prototype objects
                  case true => res || checkWithProto(argState.heap(loc)("@proto").objval.value.locset, props ++ argState.heap(loc).getProps)
                  case false => false
                })
            }
          }
          result = checkWithProto(locset, Set())
          (result, _interface_)
        }
        case _callback_ if WIDLTypeMap.callbackMap.contains(_callback_) => { // callback with functionOnly
          //println("callback " + _callback_)
          val callback = WIDLTypeMap.callbackMap(_callback_)
          var result  = false // report a bug only if every locset has bug
          for (loc <- locset) {
            if (BoolTrue <= Helper.IsCallable(argState.heap, loc)) result = true // acceptable if [loc] is a function
          }
          (result, _callback_)
        }
        case _typedef_ if WIDLTypeMap.typedefMap.contains(_typedef_) => {
          //println("typedef " + _typedef_)
          val typedef = WIDLTypeMap.typedefMap(_typedef_)
          (checkType(given, typedef.getTyp)._1, _typedef_)
        }
        case typ => {// if the type in the specification is not primitive
          (true, typ)
        }
      }
      case typ@SWArrayType(info, suffix, type2) =>
        (checkArrayType(type2), getType(typ).getOrElse("[]"))
      case typ@SWSequenceType(info, suffix, type2) =>
        (checkArrayType(type2), getType(typ).getOrElse("[]"))
      case SWUnionType(info, suffix, types) => (true, "DEBUG")
    }
  }

  def dotToStr(dot: LHS): Option[String] = dot match {
    case SDot(_, d: Dot, SId(_, x, _, _)) => dotToStr(d) match {
      case Some(str) => Some(str + "." + x)
      case None => None
    }
    case SDot(_, SVarRef(_, SId(_, o, _, _)), SId(_, x, _, _)) => Some(o + "." + x)
    case SVarRef(_, SId(_, x, _, _)) => Some(x)
    case SBracket(_, o, i) => i match {
      case SStringLiteral(_, _, idxS) => dotToStr(o) match {
        case Some(objS) => Some(objS + "." + idxS)
        case _ => None
      }
      case _ => None
    }
    case _ => None
  }

  // consumes a set and provides an enumeration of that (ex. Set(1, 2, 3) => "1, 2, 3")
  // corner case: empty set Set() => ""
  def setToStr(st: Set[String]): String = {
    if (st.isEmpty) ""
    else{
      var res: String = ""
      var flag: Boolean = false
      for (str <- st){
        if (flag) res += ", "
        res += str
      }
      res
    }
  }

  private var nestedTries = 0
  def initAll = {
    // Initialize variables
    nestedTries = 0
  }

  def doit(_program: Program, libraries: List[String] = null) = {
    var program = _program
    // Set libraries (database)
    if(libraries != null) WIDLTypeMap.setLibraries(libraries)
    // Check
    walkUnit(program)
  }

  /* error-related things */
  val errSpace = "\n            "

  def reportMessages(res: (Boolean, Boolean, List[() => Unit])): Unit = {
    res._3.foreach(msg => msg())
  }

  def isAPI(obj: LHS): Boolean = obj match {
    case SDot(_, dot, _) => isAPI(dot)
    case SVarRef(_, SId(_, x, _, _)) if x.equals("webapis") =>
      WIDLTypeMap.typedbs.contains(("WindowWebAPIs", "webapis"))
    case SVarRef(_, SId(_, x, _, _)) if x.equals("tizen") =>
      WIDLTypeMap.typedbs.contains(("TizenObject", "tizen"))
    case _ => false
  }

  /*
   * checkArgs: checks if [arg]uments fit into the definition of [param]eters in WIDL spec
   * and produces 1. a boolean that represents if errors occur or not
   *              2. a boolean that represents any warnings exist or not
   *              3. a list of string that contains error / warning messages
   */
  def checkArgsCommon(span: Span, name: String, argEnvs: List[Pair[Obj, State]], _S: List[JList[WArgument]])
  : (Boolean, Boolean, List[() => Unit]) = {
    for (env <- argEnvs) {
      argObj = env._1
      argState = env._2
      val S = overloadResolution(_S, argObj)
      // TODO: implement
    }
    (false, false, List())
  }
  def checkArgsCommon(span: Span, name: String, argEnvs: List[Pair[Obj, State]], params: JList[WArgument])
    : (Boolean, Boolean, List[() => Unit]) = {
    var errors: Boolean = false
    var warnings: Boolean = false
    var msgs: List[() => Unit] = List()
    var numOfOptional: Int = 0
    val numOfParameter: Int = params.size
    val numOfArgument: Int = argEnvs.foldLeft(-1)((base, env) => env._1("length")._1._1._1._4.getSingle match {
      case Some(d) => d.toInt
      case _ => base
    })
    for (i <- 0 until numOfParameter) params.get(i) match {
      case SWArgument(_, attrs, t, _, _) =>
        if (i < numOfArgument) {
          // check for every locset
          var global_result: Boolean = soundness || argEnvs.size == 0 // shall we report bugs for this parameter?
          var global_detected: Set[String] = Set() // detected types for this parameter
          val expected_type: Option[String] = getType(t)
          for (env <- argEnvs) {
            argObj = env._1
            argState = env._2
            var local_result = (false, "?")
            if (argObj != null) {
              val objTuple = argObj(i.toString)
              if (objTuple </ PropValueBot) local_result = checkType(objTuple, t)
              else local_result = (false, getType(t).get)
            }
            if (soundness && !local_result._1) {
              // if soundness==true, reports error if a locset has error
              // report
              global_result = false
              global_detected += local_result._2
            }
            if (!soundness) {
              // if soundness==false, reports error only if every locset has error
              if (local_result._1) {
                // not report (existing normal cases)
                global_result = true
              } else {
                global_detected += local_result._2
              }
            }
          }
          if (!global_result) {
            errors = true
            msgs :+= (() => {
              bugStorage.addMessage(span, WebAPIWrongArgType, null, null, i + 1, name, expected_type.getOrElse(""))
            })
            //expected_type.getOrElse(""), setToStr(global_detected), i+1))
          }
        }
        if (!attrs.filter(attr => isOptional(attr)).isEmpty) { // if an error callback function is missing
          numOfOptional = numOfOptional + 1
          if (i >= numOfArgument && isErrorCallback(t)){
            warnings = true
            msgs :+= (() => { bugStorage.addMessage(span, WebAPIMissingErrorCB, null, null, name) })
          }
        }
    }
    if (numOfArgument != -1 && (numOfParameter - numOfOptional > numOfArgument || numOfArgument > numOfParameter)){
      errors = true
      if (numOfOptional != 0)
        msgs :+= (() => { bugStorage.addMessage(span, WebAPIWrongArgs, null, null, name, numOfArgument, "from %s to %s".format(numOfParameter - numOfOptional, numOfParameter)) })
      else
        msgs :+= (() => { bugStorage.addMessage(span, WebAPIWrongArgs, null, null, name, numOfArgument, "of %s".format(numOfParameter)) })
    }
    (errors, warnings, msgs)
  }
  def checkArgs(fa: LHS, name: String, params: List[JList[WArgument]])
  : (Boolean, Boolean, List[() => Unit]) = {
    val span: Span = fa.getInfo.getSpan
    var argEnvs: List[Pair[Obj, State]] = List()
    NodeRelation.ast2cfgMap.get(fa) match {
      case Some(cfgList) => {
        def aux(inst: CFGInst, arguments: CFGExpr) = {
          val cfgNode = cfg.findEnclosingNode(inst)
          val cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
          for ((callContext, state) <- cstate) {
            val argLocSet = SE.V(arguments, state.heap, state.context)._1.locset
            for (argLoc <- argLocSet) {
              argEnvs :+= (state.heap(argLoc), state)
            }
          }
        }
        for (cfgInst <- cfgList) {
          cfgInst match {
            case inst@CFGCall(_, _, _, _, arguments, _, _) =>
              aux(inst, arguments)
            case inst@CFGConstruct(_, _, _, _, arguments, _, _) =>
              aux(inst, arguments)
            case _ =>
          }
        }
      }
      case None =>
    }
    checkArgsCommon(span, name, argEnvs, params)
  }
  def checkArgs(fa: LHS, name: String, params: JList[WArgument], args: List[Expr])
    : (Boolean, Boolean, List[() => Unit]) = {
    val span: Span = fa.getInfo.getSpan
    var argEnvs: List[Pair[Obj, State]] = List()
    NodeRelation.ast2cfgMap.get(fa) match {
      case Some(cfgList) => {
        def aux(inst: CFGInst, arguments: CFGExpr) = {
          val cfgNode = cfg.findEnclosingNode(inst)
          val cstate = stateManager.getInputCState(cfgNode, inst.getInstId, _MOST_SENSITIVE)
          for ((callContext, state) <- cstate) {
            val argLocSet = SE.V(arguments, state.heap, state.context)._1.locset
            for (argLoc <- argLocSet) {
              argEnvs :+= (state.heap(argLoc), state)
            }
          }
        }
        for (cfgInst <- cfgList) {
          cfgInst match {
            case inst@CFGCall(_, _, _, _, arguments, _, _) =>
              aux(inst, arguments)
            case inst@CFGConstruct(_, _, _, _, arguments, _, _) =>
              aux(inst, arguments)
            case _ =>
          }
        }
      }
      case None =>
    }
    checkArgsCommon(span, name, argEnvs, params)
  }
  def checkArgs(span: Span, name: String, args: CFGExpr, cstate: CState)
  : Unit = {
    if (name.endsWith(".constructor")) return
    var argEnvs: List[Pair[Obj, State]] = List()
    for ((callContext, state) <- cstate) {
      val argLocSet = SE.V(args, state.heap, state.context)._1.locset
      for (argLoc <- argLocSet) {
        argEnvs :+= (state.heap(argLoc), state)
      }
    }
    val splitByDot: Array[String] = name.split('.')
    val interface: String = splitByDot(0)
    val func: String = splitByDot(splitByDot.size - 1)
    val winterf: Option[(String, WInterfaceMember)] = WIDLTypeMap.getMembers(interface).find(p => p._2._1.equals(func)) match {
      case Some(pair) => Some(pair._2)
      case None => None
    }
    val params: JList[WArgument] = winterf match {
      // Now, we get the arguments of the API
      case Some((_, op: WOperation)) => op.getArgs
      case _ => null
    }
    val result = checkArgsCommon(span, name, argEnvs, params)
    reportMessages(result)
  }

  def ConstructorToStr(lst: MListBuffer[WEAConstructor], space: String): String = {
    var result: String = ""
    for (constructor <- lst) {
      result += space + WIDLToString.walk(constructor)
    }
    result
  }

  def isWrappedByTry(node: kr.ac.kaist.jsaf.nodes.Node): Boolean = NodeRelation.astParentMap.get(node) match {
    case Some(par) => par match {
      case STry(_, _, _, _) => true
      case _ => isWrappedByTry(par)
    }
    case None => false
  }
  def exceptionHandlingCheck(span: Span, node: FunApp, funName: String): Unit = {
    if (funName.endsWith(".constructor")) return
    val splitByDot: Array[String] = funName.split('.')
    val interface: String = splitByDot(0)
    val func: String = splitByDot(splitByDot.size - 1)
    val winterf: Option[(String, WInterfaceMember)] = WIDLTypeMap.getMembers(interface).find(p => p._2._1.equals(func)) match {
      case Some(pair) => Some(pair._2)
      case None => None
    }
    winterf match {
      case Some((_, op: WOperation)) if !op.getExns.isEmpty =>
        if (!isWrappedByTry(node)) {
          bugStorage.addMessage(span, WebAPINoExceptionHandling, null, null, funName)
        }
      case _ =>
    }
  }

  override def walkUnit(node: Any): Unit = node match {
    /*
     * webapis.tv.channel.tuneUp
     *
     * WindowWebAPIs defines webapis of type WebAPIs
     * WebAPIs implements { WebAPIsTVObject }
     * WebAPIsTVObject defines tv of type TV
     * TV implements { WebAPIsTVChannelManager }
     * WebAPIsTVChannelManager defines channel of type TVChannelManager
     * TVChannelManager defines { tuneUp, ... }
     */
    case fa@SDot(span, obj, SId(_, x, _, _)) if (isAPI(obj)) => {
      val api = dotToStr(obj) match {
        case Some(apis) => apis
        case _ => obj.toString
      }
      WIDLTypeMap.getAPI(obj, x) match {
        case Some(typ) =>
          WIDLTypeMap.getMembers(typ).find(p => p._2._1.equals(x)) match {
            case Some(pair) =>
            case None => bugStorage.addMessage(span.getSpan, WebAPIInvalidNamespace, null, null, x, typ)
          }
        case _ =>
          // obj is not an API
          //bugStorage.addMessage(span.getSpan, WebAPIInvalidNamespace, null, null, x, api)
      }
    }
    case fa@SFunApp(span, fun@SDot(_, obj, SId(_, x, _, _)), args) => WIDLTypeMap.getAPI(obj, x) match {
      case Some(typ) => WIDLTypeMap.getMembers(typ).find(p => p._2._1.equals(x)) match {
        case Some(pair) => pair._2 match {
          case (propName, op: WOperation) =>
            val funName = op.getName.isSome match {
              case true =>
                val isCallback = WIDLHelper.isCallback(WIDLTypeMap.interfaceMap(typ))
                val isStatic = WIDLHelper.isStatic(op)
                (isCallback | isStatic) match {
                  case true => typ + "." + propName
                  case false => typ + ".prototype." + propName
                }
              case _ => typ + "." + propName
            }
            if (!op.getExns.isEmpty && nestedTries == 0)
              bugStorage.addMessage(span.getSpan, WebAPINoExceptionHandling, null, null, funName)
            args.foreach(walkUnit)
        }
        case _ => super.walkUnit(fa)
      }
      case _ => super.walkUnit(fa)
    }
    case nw@SNew(span, fa@SFunApp(_, SVarRef(_, SId(_, f, _, _)), args))
         if f.startsWith("<>webapis_") || f.startsWith("<>tizen_") => {
      val fname = if (f.startsWith("<>webapis_")) f.drop(10) else if (f.startsWith("<>tizen_")) f.drop(8) else f
      WIDLTypeMap.constructorMap.get(fname) match {
        case Some(constructorList) =>
          var result: (Boolean, Boolean, List[() => Unit]) = null
          //checkArgs(nw, fname, constructorList.map(f => f.getArgs).toList)
          for (constructor <- constructorList){
            val temp: (Boolean, Boolean, List[() => Unit]) = checkArgs(nw, fname, constructor.getArgs, args)
            if (!temp._1 || constructorList.size == 1){ // if there is no error or #(possible constructor) is 1
              if (result == null)
                result = temp
              //else
                // System.out.format("* Warning: many constructors are possible for %s\n", fname)
            }
          }
          if (result != null){ // there is a matching constructor
            reportMessages(result)
          } else {
            bugStorage.addMessage(span.getSpan, WebAPIWrongConstructor, null, null, fname, ConstructorToStr(constructorList, errSpace + "  "))
          }
        case None =>
          if (f.startsWith("<>webapis_"))
            bugStorage.addMessage(span.getSpan, WebAPIInvalidNamespace, null, null, fname, "webapis")
          else if (f.startsWith("<>tizen_"))
            bugStorage.addMessage(span.getSpan, WebAPIInvalidNamespace, null, null, fname, "tizen")
      }
      args.foreach(walkUnit)
    }
    case STry(_, body, catchB, _) => {
      nestedTries += 1
      walkUnit(body)
      nestedTries -= 1
      walkUnit(catchB)
    }
    case _: Comment =>
    case _ => super.walkUnit(node)
  }
}
