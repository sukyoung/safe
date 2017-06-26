/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.android_checker

import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.android_checker.AndroidUtil._
import kr.ac.kaist.safe.util._

class AndroidTypeSystem(
    name: String,
    ir: IRRoot,
    bridge: Map[String, List[String]],
    jvC: Map[String, JvClass],
    debug: Boolean
) extends IRGeneralWalker[AndroidType] {
  ////////////////////////////////////////////////////////////////
  // private global
  ////////////////////////////////////////////////////////////////

  private var warnings: List[AndroidTypeWarning] = List()
  private var errors: List[AndroidTypeError] = List()
  private var checked: List[String] = List()
  private var unsafe = false
  private val builtins = Set("RegExp", "Date", "console", "JSON", "daum")
  private var jvClasses: Map[String, JvClass] = jvC
  type Env = Map[String, AndroidType]
  private var env: Env = newEnv
  private var store: Map[String, Env] = new HashMap[String, Env]
  private var dirty: List[String] = List()

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  def newEnv: Env = new HashMap[String, AndroidType]
  val emptyEnv = newEnv

  def lookupEnv(obj: String): Option[AndroidType] =
    if (!env.contains(obj)) None
    else Some(env(obj))

  def updateSto(obj: String, prop: String, value: AndroidType): Unit =
    if (!store.contains(obj)) store += (obj -> newEnv)
    else store += (obj -> (store(obj) + (prop -> value)))

  def lookupSto(obj: String): Option[Env] =
    if (dirty.contains(obj)) None
    else if (!store.contains(obj)) None
    else Some(store(obj))

  def lookupSto(obj: String, prop: String): Option[AndroidType] =
    if (dirty.contains(obj)) Some(Top)
    else lookupSto(obj) match {
      case Some(e) =>
        if (!e.contains(prop)) None
        else Some(e(prop))
      case None => None
    }

  def removeSto(obj: String, prop: String): Unit =
    if (store.contains(obj)) store += (obj -> (store(obj) - prop))

  def markDirty(obj: String): Unit = dirty ::= obj

  def markDirtyTop: Unit = env.values.foreach {
    case JsObject(name, _, _) => markDirty(name)
    case BrgObject(name) => markDirty(name)
    case _ =>
  }

  def bridging(bridge: Map[String, List[String]]): Unit =
    bridge.foreach {
      case (key, value @ first :: rest) =>
        val jvName =
          if (rest.isEmpty) first
          else {
            val dummy = makeJvDummyClass(value.map(jvClasses(_)))
            jvClasses += (dummy.name -> dummy)
            dummy.name
          }
        env += (key -> new BrgObject(jvName))
        updateBridge(jvName)
      case _ =>
    }

  def modeling: Unit = {
    env += ("window" -> makeJsNamedObject("window"))
    builtins.foreach(name => {
      env += (name -> makeJsNamedObject(name))
      if (name == "daum") updateSto(name, "maps", makeJsDummyObject)
    })
  }

  def printResults: Unit = {
    if (!errors.isEmpty) {
      println(name)
      println("/***** Errors *****/")
      errors.foreach(err => println(s"    $err"))
      println("/*****************/\n")
    }
    /*
    if (!warnings.isEmpty) {
      println("/**** Warnings ****/")
      warnings.foreach(warn => println(s"    $warn"))
      println("/*****************/")
    }
*/
  }

  def updateBridge(obj: String): Unit =
    jvClasses(obj).listJvMethods.foreach(m => updateSto(obj, m, makeBrgFunction(m)))
  def checkJvBoundary(androidType: AndroidType, jvType: JvType): Boolean = true
  def checkJsBoundary(jv: JvType): AndroidType = jv match {
    case JvTop => Top
    case JvObject(name) =>
      if (store.contains(name)) new BrgObject(name)
      else {
        updateBridge(name)
        new BrgObject(name)
      }
    case _ => makeJsDummyObject
  }

  def checkCall(fun: IRFunctional, thisType: AndroidType, argsType: AndroidType): AndroidType = fun match {
    case IRFunctional(ast, fromSource, name, params, args, fds, vds, body) =>
      if (checked.contains(name.uniqueName)) Top
      else if (params.length < 2 && !body.isEmpty) Top // Why?
      else {
        checked ::= name.uniqueName
        // This binding
        val thisName = params.head.uniqueName
        val previous = env.get(thisName)
        env += (thisName -> thisType)
        // Argument binding
        env += (params.tail.head.uniqueName -> argsType)
        args.map(walk); fds.map(walk); vds.map(walk)
        val t = body.map(walk).last
        // Withdraw this binding
        previous match {
          case Some(v) => env += (thisName -> v)
          case None => env -= thisName
        }
        checked = checked.tail
        t
      }
  }

  def checkUnsafeUpdate(from: AndroidType, to: AndroidType, span: Span): Unit =
    if (unsafe && !from.equals(to))
      warnings ::= new AndroidTypeWarning("Update variables with different types", span)

  def checkUnsafeUpdate(from: Option[AndroidType], to: AndroidType, span: Span): Unit = from match {
    case Some(v) => checkUnsafeUpdate(v, to, span)
    case _ =>
  }

  def isJvMth(cls: String, name: String): Boolean =
    jvClasses(cls).listJvMethods.contains(name)

  def getName(prop: IRExpr): Option[String] = prop match {
    case IRUserId(_, _, uniqueName, _, _) => Some(uniqueName)
    case IRTmpId(_, _, uniqueName, _) => Some(uniqueName)
    case IRVal(value) => value match {
      case EJSString(str) => Some(str)
      case EJSNumber(text, _) => Some(text)
      case _ => None
    }
    case _ => None
  }

  def updateObj(obj: String, value: AndroidType, index: IRExpr, span: Span): Unit = getName(index) match {
    case Some(prop) =>
      if (obj == "window") {
        checkUnsafeUpdate(env.get(prop), value, span)
        env += (prop -> value)
      } else {
        checkUnsafeUpdate(lookupSto(obj, prop), value, span)
        updateSto(obj, prop, value)
      }
    case None =>
      markDirty(obj)
      warnings ::= new AndroidTypeWarning("Use expressions as properties", span)
  }

  def updateBrgObj(obj: String, value: AndroidType, index: IRExpr, span: Span): Unit = {
    value match {
      case js: JsObject if js.isUnknown => js.know
      case _ =>
    }
    getName(index) match {
      case Some(prop) =>
        if (!isJvMth(obj, prop)) {
          checkUnsafeUpdate(lookupSto(obj, prop), value, span)
          updateSto(obj, prop, value)
        }
      case None =>
        markDirty(obj)
        warnings ::= new AndroidTypeWarning("Use expressions as properties", span)
    }
  }

  def updateTop(index: IRExpr, span: Span): Unit = getName(index) match {
    case Some(prop) => env.values.foreach {
      case JsObject(name, _, _) => updateObj(name, Top, index, span)
      case BrgObject(name) => updateBrgObj(name, Top, index, span)
      case _ =>
    }
    case None =>
      markDirtyTop
      warnings ::= new AndroidTypeWarning("Use expressions as properties", span)
  }

  def lookupObj(obj: String, index: IRExpr): AndroidType = getName(index) match {
    case Some(prop) =>
      if (obj == "window")
        lookupEnv(prop) match {
          case Some(t) => t
          case None => makeJsDummyObject
        }
      else
        lookupSto(obj, prop) match {
          case Some(t) => t
          case None => makeJsDummyObject
        }
    case None => Top
  }

  def lookupBrgObj(obj: String, index: IRExpr): AndroidType = getName(index) match {
    case Some(prop) => lookupSto(obj, prop) match {
      case Some(t) => t
      case None =>
        val dummy = makeJsDummyObject
        dummy.forget
        dummy
    }
    case None => Top
  }

  def check: Unit = {
    bridging(bridge)
    modeling
    walk(ir)
    printResults
  }

  override def walk(node: IRRoot): AndroidType = node match {
    case IRRoot(ast, fds, vds, irs) =>
      fds.map(walk)
      vds.map(walk)
      join(irs.map(s => walk(s)))
  }

  override def walk(node: IRVarStmt): AndroidType = walk(node.lhs)

  override def walk(node: IRStmt): AndroidType = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      val t1 = walk(right)
      t1 match {
        case JsObject(name, _, _) if name == "window" =>
        case _ =>
          checkUnsafeUpdate(lookupEnv(lhs.uniqueName), t1, ast.span)
          env += (lhs.uniqueName -> t1)
      }
      t1

    case IRDelete(ast, lhs, id) =>
      walk(id)
      checkUnsafeUpdate(lookupEnv(id.uniqueName), makeJsDummyObject, ast.span)
      env -= id.uniqueName
      val t = makeJsDummyObject
      env += (lhs.uniqueName -> t)
      t

    case IRDeleteProp(ast, lhs, obj, index) =>
      val (t1, t2) = (walk(obj), walk(index))
      t1 match {
        case JsObject(name, _, _) => getName(index) match {
          case Some(prop) =>
            checkUnsafeUpdate(lookupSto(obj.uniqueName, prop), makeJsDummyObject, ast.span)
            removeSto(obj.uniqueName, prop)
          case None =>
            markDirty(obj.uniqueName)
            warnings ::= new AndroidTypeWarning("Use expressions as properties", ast.span)
        }
        case BrgObject(name) => getName(index) match {
          case Some(prop) =>
            if (isJvMth(obj.uniqueName, prop)) {
              checkUnsafeUpdate(lookupSto(obj.uniqueName, prop), makeJsDummyObject, ast.span)
              removeSto(obj.uniqueName, prop)
            }
          case None =>
            markDirty(obj.uniqueName)
            warnings ::= new AndroidTypeWarning("Use expressions as properties", ast.span)
        }
        case _ =>
      }
      val t = makeJsDummyObject
      env += (lhs.uniqueName -> t)
      t

    case IRObject(ast, lhs, members, proto) =>
      val obj = makeJsDummyObject
      members.foreach {
        case IRField(ast, prop, expr) =>
          val newt = walk(expr)
          updateSto(obj.name, prop.uniqueName, newt)
        case m =>
          warnings ::= new AndroidTypeWarning("Use getter or setter", m.ast.span)
      }
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), obj, ast.span)
      env += (lhs.uniqueName -> obj)
      obj

    case IRArray(ast, lhs, elements) =>
      val obj = makeJsDummyObject
      var index = 0
      elements.foreach(elem => {
        index += 1
        elem match {
          case Some(e) => updateSto(obj.name, index.toString, walk(e))
          case _ =>
        }
      })
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), obj, ast.span)
      env += (lhs.uniqueName -> obj)
      obj

    case IRArrayNumber(ast, lhs, elements) =>
      val obj = makeJsDummyObject
      var index = 0
      elements.foreach(e => {
        index += 1
        updateSto(obj.name, index.toString, jsDoubleObject)
      })
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), obj, ast.span)
      env += (lhs.uniqueName -> obj)
      obj

    case IRArgs(ast, lhs, elements) =>
      val obj = makeJsDummyObject
      var index = 0
      elements.foreach(elem => {
        index += 1
        elem match {
          case Some(e) => updateSto(obj.name, index.toString, walk(e))
          case _ =>
        }
      })
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), obj, ast.span)
      env += (lhs.uniqueName -> obj)
      obj

    case IRCall(ast, lhs, fun, thisB, args) =>
      val fname = fun.uniqueName
      val (t1, t2, t3) = (walk(fun), walk(thisB), walk(args))
      t2 match {
        case _: JsObject => t1 match {
          case mth: JsObject if mth.ftn.isDefined => checkCall(mth.ftn.get, t2, t3)
          case _ => makeJsDummyObject
        }
        case BrgObject(oname) => t1 match {
          case mth: JsObject if !mth.isUnknown =>
            if (mth.ftn.isDefined) checkCall(mth.ftn.get, t2, t3)
            else if (mth.brg.isDefined) {
              val as = t3 match {
                case args: JsObject => lookupSto(args.name) match {
                  case Some(types) => types
                  case None => emptyEnv
                }
                case _ => emptyEnv
              }
              jvClasses(oname).lookupJvMethod(mth.brg.get, as.size) match {
                case Some(jvmth) =>
                  jvmth.params.foldLeft(0)((index, jv) => {
                    checkJvBoundary(as(index.toString), jv)
                    index + 1
                  })
                  val rt = checkJsBoundary(jvmth.result)
                  checkUnsafeUpdate(lookupEnv(lhs.uniqueName), rt, ast.span)
                  env += (lhs.uniqueName -> rt)
                  rt
                case None =>
                  // a bridge method but it does not exist in Java
                  errors ::= new AndroidTypeError(s"No bridge method $fname", ast.span)
                  makeJsDummyObject
              }
            } else makeJsDummyObject // Built-in functions
          case BrgObject(_) => makeJsDummyObject
          case _ =>
            // a bridge object but it does not have the property
            errors ::= new AndroidTypeError(s"Unknown method $fname", ast.span)
            makeJsDummyObject
        }
        case Top =>
          errors ::= new AndroidTypeError(s"Not a function $fname", ast.span)
          makeJsDummyObject
      }

    case IRInternalCall(ast, lhs, fun, args) => fun match {
      case NodeUtil.INTERNAL_TO_OBJ =>
        val t1 = walk(args.head)
        env += (lhs.uniqueName -> t1)
        t1
      case NodeUtil.INTERNAL_GET_BASE =>
        val base = makeJsNamedObject("window")
        env += (lhs.uniqueName -> base)
        base
      case _ => Top
    }

    case IRNew(ast, lhs, fun, args) =>
      val (t1, t2, t3) = (walk(fun), walk(args.head), join(args.tail.map(walk)))
      val ty = t1 match {
        case cons: JsObject =>
          if (cons.ftn.isDefined) {
            val call_t = checkCall(cons.ftn.get, t2, t3)
            if (ExplicitReturnWalker.walk(cons.ftn.get)) Top
            else t2
          } else if (builtins.contains(cons.name)) {
            env += (args.head.uniqueName -> t1)
            t1
          } else t2
        case _ => Top
      }
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), ty, ast.span)
      env += (lhs.uniqueName -> ty)
      ty

    case IRFunExpr(ast, lhs, ftn) =>
      val t = makeJsFunction(ftn)
      checkUnsafeUpdate(lookupEnv(lhs.uniqueName), t, ast.span)
      env += (lhs.uniqueName -> t)
      t

    case IREval(ast, lhs, arg) =>
      warnings ::= new AndroidTypeWarning("Use eval", ast.span)
      Top

    case IRStmtUnit(ast, stmts) => join(stmts.map(walk))

    case IRStore(ast, obj, index, rhs) =>
      val (t1, t2, t3) = (walk(obj), walk(index), walk(rhs))
      t1 match {
        case JsObject(name, _, _) => updateObj(name, t3, index, ast.span)
        case BrgObject(name) => updateBrgObj(name, t3, index, ast.span)
        case Top => updateTop(index, ast.span)
      }
      t3

    case IRBreak(ast, label) => Top

    case IRReturn(ast, expr) => if (expr.isDefined) walk(expr.get) else Top

    case IRWith(ast, id, stmt) =>
      //TODO: No with statements due to with rewriter. 
      warnings ::= new AndroidTypeWarning("Use with statements", ast.span)
      Top

    case IRLabelStmt(ast, label, stmt) => walk(stmt)

    case vs: IRVarStmt => walk(vs.lhs)

    case IRThrow(ast, expr) => walk(expr)

    case IRSeq(ast, stmts) => join(stmts.map(walk))

    case IRIf(ast, expr, trueB, falseB) =>
      walk(expr); walk(trueB)
      if (falseB.isDefined) walk(falseB.get)
      Top

    case IRWhile(ast, cond, body, _, _) =>
      walk(cond)
      unsafe = true
      walk(body)
      unsafe = false
      Top

    case IRTry(ast, body, name, catchB, finallyB) =>
      val t1 = walk(body)
      if (catchB.isDefined) walk(catchB.get)
      if (finallyB.isDefined) walk(finallyB.get)
      t1

    case IRNoOp(_, _) => Top

    case _ => super.walk(node)
  }

  override def walk(node: IRExpr): AndroidType = node match {
    case IRBin(ast, first, op, second) =>
      walk(first); walk(second); makeJsDummyObject

    case IRUn(ast, op, expr) =>
      walk(expr); makeJsDummyObject

    case IRLoad(ast, obj, index) =>
      val t1 = walk(obj)
      walk(index)
      val t = t1 match {
        case JsObject(name, _, _) => lookupObj(name, index)
        case BrgObject(name) => lookupBrgObj(name, index)
        case Top => Top
      }
      t

    case IRThis(ast) => lookupEnv("this") match {
      case Some(t) => t
      case _ => makeJsDummyObject
    }

    case IRInternalValue(ast, name) => makeJsDummyObject

    case IRVal(value) => makeJsDummyObject

    case _ => super.walk(node)
  }

  override def walk(node: IRId): AndroidType = node match {
    case id: IRUserId =>
      env.get(id.uniqueName) match {
        case Some(t) => t
        case None =>
          val create = makeJsDummyObject
          env += (id.uniqueName -> create)
          create
      }

    case id: IRTmpId =>
      env.get(id.uniqueName) match {
        case Some(t) => t
        case None =>
          val create = makeJsDummyObject
          env += (id.uniqueName -> create)
          create
      }
  }

  override def walk(node: IRFunDecl): AndroidType = {
    val t = makeJsFunction(node.ftn)
    val name = node.ftn.name.uniqueName
    checkUnsafeUpdate(lookupEnv(name), t, node.ast.span)
    env += (name -> t)
    makeJsDummyObject
  }

  override def walk(node: IRFunctional): AndroidType = {
    node.params.foreach(p => env += (p.uniqueName -> Top))
    join(node.args.map(walk)); join(node.fds.map(walk))
    join(node.vds.map(walk)); join(node.body.map(walk))
    makeJsNamedObject(node.name.uniqueName)
  }

  def join(list: AndroidType*): AndroidType = join(list.toList)

  def join(list: List[AndroidType]): AndroidType =
    if (list.isEmpty) Top else list.last

  object ExplicitReturnWalker extends IRGeneralWalker[Boolean] {
    def join(list: Boolean*): Boolean = join(list.toList)
    def join(list: List[Boolean]): Boolean =
      list.foldLeft(false)((bool, b) => bool || b)
    override def walk(node: IRRoot): Boolean = join(node.irs.map(walk))
    override def walk(node: IRStmt): Boolean = node match {
      case IRStmtUnit(_, stmts) => join(stmts.map(walk))
      case IRWith(_, _, stmt) => walk(stmt)
      case IRLabelStmt(_, _, stmt) => walk(stmt)
      case IRReturn(_, _) => true
      case IRThrow(_, _) => true
      case IRSeq(_, stmts) => join(stmts.map(walk))
      case IRIf(_, _, trueB, falseB) =>
        walk(trueB) || (if (falseB.isDefined) walk(falseB.get) else false)
      case IRWhile(_, _, body, _, _) => walk(body)
      case IRTry(_, body, _, catchB, finallyB) =>
        walk(body) ||
          (if (catchB.isDefined) walk(catchB.get) else false) ||
          (if (finallyB.isDefined) walk(finallyB.get) else false)
      case _ => false
    }
    override def walk(node: IRFunctional): Boolean = join(node.body.map(walk))
  }
}
