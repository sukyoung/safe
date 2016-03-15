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

package kr.ac.kaist.safe.safe_util

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.nodes_util.NodeReflection
import kr.ac.kaist.safe.nodes_util.Unprinter

import scala.Some;
import java.io._
import java.io.IOException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.Map

class Printer extends NodeReflection {
  val firstFieldOnNewLine = true
  val oneLineVarRef = true
  val skipEmpty = true
  val shortListThreshold = 4
  var oneLinerNesting: Int = 0
  val nl = System.getProperty("line.separator")
  var lastSpan: Span = null
  val indentString = "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           " +
    "                                                           "

  def indentF(i: Int, w: Appendable): Unit = {
    w.append("\n")
    w.append(indentString.substring(0, i))
  }

  def dumpSpan(span: Span, w: Appendable): Unit = {
    if (span == null) return
    if (lastSpan == null) {
      span.appendTo(w, true, true)
      lastSpan = span
      return
    }
    val do_file = !(lastSpan.end.fileName.equals(span.begin.fileName)) ||
      !(lastSpan.begin.fileName.equals(span.begin.fileName))
    if (do_file ||
      lastSpan.begin.column != span.begin.column ||
      lastSpan.end.column != span.end.column ||
      lastSpan.begin.line != span.begin.line ||
      lastSpan.end.line != span.end.line) {
      span.appendTo(w, do_file, true)
    }
    lastSpan = span
  }

  def allAtoms[T](l: List[T]): Boolean =
    !l.find(x => !(x.isInstanceOf[String] || x.isInstanceOf[Number] || x.isInstanceOf[Boolean])).isDefined

  def allDoubles[T](l: List[T]): Boolean =
    !l.find(x => !(x.isInstanceOf[Double])).isDefined

  def dumpIRExtra(x: Any, w: Appendable, indent: Int): Unit = {
    val cl = x.getClass
    val clname = cl.getSimpleName
    val oneLinerNestingInc = if (x.isInstanceOf[IRId]) 1 else 0
    oneLinerNesting += oneLinerNestingInc;
    val oneLiner = oneLineVarRef && (oneLinerNesting > 0 || x.isInstanceOf[IRId] || x.isInstanceOf[Some[Any]])
    val fields = getCachedPrintableFields(cl, clname)
    w.append("(")
    w.append(clname)
    dumpFields(w, indent, x, oneLiner, fields, true)
    w.append(")")
    oneLinerNesting -= oneLinerNestingInc
  }

  def dump(o: Any, w: Appendable): Unit = dump(o, w, 0)

  def dump(o: Any, w: Appendable, indent: Int): Unit = {
    if (o == null) {
      w.append("_")
    } else if (o.isInstanceOf[String]) {
      val s = o.asInstanceOf[String]
      // Always quote on output.
      w.append('"')
      w.append(Unprinter.enQuote(s))
      w.append('"')
    } else if (o.isInstanceOf[Double]) {
      w.append(o.asInstanceOf[Double].doubleValue.toString)
    } else if (o.isInstanceOf[Number]) {
      w.append(o.toString)
    } else if (o.isInstanceOf[Boolean]) {
      w.append(o.toString)

    } else if (o.isInstanceOf[ArrayNumberExpr]) {
      val x = o.asInstanceOf[ArrayNumberExpr]
      val cl = x.getClass
      val clname = cl.getSimpleName
      w.append("(")
      w.append(clname)
      dumpSpan(NodeUtil.getSpan(x), w)
      indentF(indent + 1, w)
      w.append("elements=")
      getCachedPrintableFields(cl, clname).foreach(f => {
        val p = f.get(x)
        if (p.isInstanceOf[List[Any]]) {
          val l = p.asInstanceOf[List[Double]]
          w.append("[\n<")
          l.foreach(x => { indentF(indent + 1, w); dump(x, w, indent + 1) })
          w.append("\n>]")
        }
      })
      w.append(")")

    } else if (o.isInstanceOf[List[Any]]) {
      val l = o.asInstanceOf[List[Any]]
      val length = l.length
      w.append("[")
      if (length < shortListThreshold && allAtoms(l)) {
        l.foreach(x => { w.append(" "); dump(x, w, indent + 1) })
      } else {
        l.foreach(x => {
          if (firstFieldOnNewLine) {
            if (oneLineVarRef && oneLinerNesting > 0) {
              w.append(" ")
            } else {
              indentF(indent + 1, w)
            }
          }
          dump(x, w, indent + 1)
        })
      }
      w.append("]")
    } else if (o.isInstanceOf[Some[Any]]) {
      w.append("(")
      w.append("Some x=")
      dump(o.asInstanceOf[Some[Any]].get, w, indent)
      w.append(")")
    } else if (o.isInstanceOf[None$]) {
      w.append("(")
      w.append("Some")
      w.append(")")
    } else if (o.isInstanceOf[ASTNode]) {
      val x = o.asInstanceOf[ASTNode]
      val cl = x.getClass
      val clname = cl.getSimpleName
      val oneLinerNestingInc = if (o.isInstanceOf[Literal] || o.isInstanceOf[VarRef]) 1 else 0
      oneLinerNesting += oneLinerNestingInc
      val oneLiner = oneLineVarRef && (oneLinerNesting > 0 || o.isInstanceOf[Id] || o.isInstanceOf[Some[Any]])
      val fields = getCachedPrintableFields(cl, clname)
      w.append("(")
      w.append(clname)
      dumpSpan(NodeUtil.getSpan(x), w)
      dumpFields(w, indent, x, oneLiner, fields, true)
      w.append(")")
      oneLinerNesting -= oneLinerNestingInc
    } else if (o.isInstanceOf[ASTNodeInfo]) {
      val x = o.asInstanceOf[ASTNodeInfo]
      val cl = x.getClass
      val clname = cl.getSimpleName
      val fields = getCachedPrintableFields(cl, clname)
      if (x.comment.isDefined) {
        w.append("(")
        w.append(clname)
        w.append("(")
        dump(x.comment.get.txt, w)
        w.append("))")
      }
    } else if (o.isInstanceOf[IRNode]) {
      val x = o.asInstanceOf[IRNode]
      val cl = x.getClass
      val clname = cl.getSimpleName
      val oneLinerNestingInc = if (o.isInstanceOf[IRId]) 1 else 0
      oneLinerNesting += oneLinerNestingInc;
      val oneLiner = oneLineVarRef && (oneLinerNesting > 0 || o.isInstanceOf[IRId] || o.isInstanceOf[Some[Any]])
      val fields = getCachedPrintableFields(cl, clname)
      w.append("(")
      w.append(clname)
      dumpSpan(NodeUtil.getSpan(x), w)
      dumpFields(w, indent, x, oneLiner, fields, true)
      w.append(")")
      oneLinerNesting -= oneLinerNestingInc
    } else {
      w.append("?" + o.getClass.getName)
    }
  }

  def dumpFields(w: Appendable, indent: Int, x: Any, oneLiner: Boolean,
    fields: Array[Field], skipThisEmpty: Boolean): Unit =
    fields.foreach(f => try {
      val p = f.get(x)
      if (skipEmpty &&
        skipThisEmpty &&
        ((p.isInstanceOf[List[Any]] && p.asInstanceOf[List[Any]].isEmpty) ||
          p.isInstanceOf[Long] ||
          (p.isInstanceOf[Boolean] && !p.asInstanceOf[Boolean]))) {
        /* do nothing */
      } else if (x.isInstanceOf[Span] ||
        f.getName.equals("uniqueName") ||
        f.getName.equals("info")) {
        /* do nothing */
      } else {
        if (oneLiner) {
          w.append(" ")
        } else {
          indentF(indent + 1, w)
        }
        w.append(f.getName)
        w.append("=")
        dump(p, w, indent + 1)
      }
    } catch {
      case (e: IllegalArgumentException) => e.printStackTrace
      case (e: IllegalAccessException) => e.printStackTrace
    })
}
