/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.interpreter

import kr.ac.kaist.safe.interpreter.{InterpreterPredefine => IP}
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.{NodeUtil => NU}

object InterpreterDebug {

  var uniq_id = 0
  def fresh(id: String) = {
    uniq_id += 1
    val stringarr = id.split(NU.INTERNAL_SYMBOL)
    stringarr.update(stringarr.length-1, uniq_id.toString)
    stringarr.foldLeft("")((s,a) => s+NU.INTERNAL_SYMBOL+a)
  }
  type TmpIdEnv = List[(String, String)]
  var tmpEnv = Nil.asInstanceOf[TmpIdEnv]
  def addE(uniq: String, new_uniq: String) = tmpEnv = (uniq, new_uniq)::tmpEnv
  // def getE(uniq: String): String = uniq
  def getE(uniq: String): String = tmpEnv.find(p => p._1.equals(uniq)) match {
    case None =>
      val new_uniq = if (NU.isInternal(uniq) && !NU.isGlobalName(uniq)) fresh(uniq) else uniq
      addE(uniq, new_uniq)
      new_uniq
    case Some((_, new_uniq)) => new_uniq
  }

  def prExpr(e: IRExpr): String = e match {
    case IRBin(_,f,o,s) => prExpr(f)+o.toString+prExpr(s)
    case IRUn(_,o,expr) => o.toString+prExpr(expr)
    case id:IRId => getE(id.uniqueName)
    case _:IRThis => "this"
    case IRVal(EJSUndef) => "undefined"
    case IRVal(EJSNull) => "null"
    case IRVal(b: EJSBool) => b.bool.toString
    case IRVal(n: EJSNumber) => n.toString //IH.toString(n)
    case IRVal(s: EJSString) => s.str
  }

/*
  def prHeapEnv(IS: InterpreterState) = {
    prHeap(IS.heap)
    System.out.println("Global object environment: " + toStringEnvRec(IS.GlobalObject.declEnvRec))
    prEnv(IS.env)
  }

  def prHeapEnv(result: (Heap, Env)) = {
    prHeap(result._1)
    prEnv(result._2)
  }

  def prHeap(h: Heap) = {
    System.out.println("Heap: {")
    //h.foreach(e => if (e._1.toString.size < 4) System.out.print(e._1+" |-> "+e._2.className+", "))
    val i = h.entrySet().iterator()
    while(i.hasNext()) {
      val e = i.next()
      val id = getE(e.getKey().n.toString())
      if (e.getKey().n >= 0) {
        if (e.getValue().className == "Function") {
          System.out.print("    " + id +" |-> "+e.getValue().className+" <<"+
                           toStringEnv(e.getValue().asInstanceOf[JSFunction].scope)+">>, ")
        }
        else {
          System.out.println("    " + id +" |-> "+e.getValue().className+" <<"+
                             toStringPropTable(e.getValue().property)+">>, ")
        }
      }
    }
    System.out.println("}\nHeap(#Global)="+toStringPropTable(h.get(IP.lGlobal).property))
    //System.out.println("Heap(#1)="+toStringPropTable(h(Loc("#1")).property))
  }

  def prEnv(e: Env) = {
    System.out.println("Env: ")
    System.out.println(toStringEnv(e))
    System.out.println()
  }

  def toStringEnv(env: Env): String = env match {
    case EmptyEnv() => "[]"
    case ConsEnv(er, rest) => toStringEnvRec(er)+" :: "+toStringEnv(rest)
  }
  def toStringEnvRec(er: EnvRec) = er match {
    case DeclEnvRec(s) => "{"+toStringStore(s)+"}"
    case ObjEnvRec(l) => "Loc("+l.n+")"
  }
  */
  def toStringStore(s: Store) = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{")
    val i = s.entrySet().iterator()
    while(i.hasNext()) {
      val e = i.next()
      sb.append("    " + getE(e.getKey())).append(" |-> ").append(e.getValue().value).append(", ")
    }
    sb.append("}")
    sb.toString
  }
  /*
  def toStringPropTable(s: PropTable) = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{\n")
    for (key <- s.keys) {
      var print = true
      val op = s.get(key)
      if(op.value.isDefined) {
         val v = op.value.get
         print = !v.isInstanceOf[JSObject] || v.asInstanceOf[JSObject].n >= 0
      }
      val id = getE(key)
      if(print == true) sb.append("    " + id).append(" |-> ").append(toStringOV(s.get(key))).append(",\n")
    }
    sb.append("}")
    sb.toString
  }
  */
  def toStringOV(op: ObjectProp): String = {
    var str = ""
    if(op.value.isDefined) str+= "value:" + op.value.get + ", "
    if(op.writable.isDefined) str+= "writable:" + op.writable.get + ", "
    if(op.get.isDefined) str+= "get:" + op.get.get + ", "
    if(op.set.isDefined) str+= "set:" + op.set.get + ", "
    str+= "enumerable:" + op.enumerable + ", "
    str+= "configurable:" + op.configurable

    str
  }
  
  var performanceTimer: Long = 0
  def timerStart(): Unit = performanceTimer = System.currentTimeMillis()
  def timerStop(): Unit = System.out.println((System.currentTimeMillis() - performanceTimer) + "ms")
}
