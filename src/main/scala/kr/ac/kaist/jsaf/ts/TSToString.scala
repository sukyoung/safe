/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.ts

import java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, SpanInfo}
import kr.ac.kaist.jsaf.nodes_util.JSAstToConcrete
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

/* Converts a TypeScript AST to a string which is the concrete version of that node
 *
 * Caveats:
 * 1. Comments are not preserved.
 *
 * Possible improvements:
 * 1. We may want to keep comments.
 */
object TSToString extends Walker {

  val width = 50

  def doit(node: ASTNode): String = {
    JSAstToConcrete.setTypeScript
    walk(node)
  }

  /* indentation utilities *************************************************/
  var indent = 0
  val tab: StringBuilder = new StringBuilder("  ")
  def increaseIndent = indent += 1
  def decreaseIndent = indent -= 1
  def getIndent = {
    val s: StringBuilder = new StringBuilder
    for (i <- 0 to indent-1) s.append(tab)
    s.toString
  }

  /* utility methods ********************************************************/
  var isTop = true

  def join(kind: String, all: List[Any],
           sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ =>
      result.append(kind)
      join(all, sep, result)
  }

  def join(all: List[Any], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil =>
      result
    case _ => result.length match {
      case 0 => {
        join(all.tail, sep, result.append(walk(all.head)))
      }
      case _ =>
        if (result.length > width && sep.equals(", ")) {
          join(all.tail, sep, result.append(", \n"+getIndent).append(walk(all.head)))
        }
        else {
          join(all.tail, sep, result.append(sep).append(walk(all.head)))
        }
    }
  }

  def prTs(ts: List[Any], s: StringBuilder) =
    if (!ts.isEmpty) {
      s.append(" <")
      s.append(join(ts, ", ", new StringBuilder("")))
      s.append(">")
    }

  def prPs(ps: List[Param], s: StringBuilder) = {
    s.append(" (")
    if (!ps.isEmpty) {
      s.append(join(ps, ", ", new StringBuilder("")))
    }
    s.append(")")
  }

  def prTyA(tyOpt: Option[Type], s: StringBuilder) =
    if (tyOpt.isDefined) s.append(" : ").append(walk(tyOpt.get))

  def prLines(elts: List[Any], sep: String, s: StringBuilder) = elts match {
    case Nil => s.append(" {}")
    case _ =>
      s.append(" {\n")
      s.append(join(elts, sep, new StringBuilder("")))
      s.append("}\n")
  }

  /* The rule of separators(indentation, semicolon and newline) in unparsing pattern matchings.
   * This rule is applied recursively
   * Principle: All case already has indentation at the front and newline at the end.
   *
   * Root case: See [case SProgram].
   *    program's type is List<SourceElement>.
   *    Each <SourceElement> has indentation at the front
   *    and newline at the end to keep the principle.
   *
   * Branch case(Stmt or ListofStmt): SBlock, SFunDecl, SVarStmt, SExprStmt...
   *    Add indentation and newline to keep the principle in inner cases.
   *    When its type is [Stmt], add ";" at the end.
   *
   * Leaf case(not Stmt, may have inner case): SExprList, SArrayExpr, ...
   *    Don't add indentation, newline and ";".
   *    They are already added.
   *    But other separators(like ", " or " ") may be added.
   *
   */
  override def walk(node:Any):String = node match {
    case SModExpAssignment(info, id) =>
      val s: StringBuilder = new StringBuilder
      s.append("export = ").append(walk(id)).append(";")
      s.toString

    case SExtImpDecl(info, id, module) =>
      val s: StringBuilder = new StringBuilder
      s.append("import ").append(walk(id)).append(" = require (\"").append(module).append("\");")
      s.toString

    case STSImpDecl(info, id, path) =>
      val s: StringBuilder = new StringBuilder
      s.append("import ").append(walk(id)).append(" = ").append(walk(path)).append(";")
      s.toString

    case SIntfDecl(info, id, tps, ext, typ) =>
      val s: StringBuilder = new StringBuilder
      s.append("interface ").append(walk(id))
      prTs(tps, s)
      if (!ext.isEmpty) {
        s.append(" extends ")
        s.append(join(ext, ", ", new StringBuilder("")))
      }
      s.append(walk(typ))
      s.toString

    case SAmbVarDecl(info, id, tyOpt) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("var ").append(walk(id))
      prTyA(tyOpt, s)
      s.append(";")
      s.toString

    case SAmbFunDecl(info, id, sig) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("function ").append(walk(id)).append(walk(sig)).append(";")
      s.toString

    case SAmbClsDecl(info, id, tps, extOpt, imp, elts) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("class ").append(walk(id))
      prTs(tps, s)
      if (extOpt.isDefined)
        s.append(" extends ").append(walk(extOpt.get))
      if (!imp.isEmpty) {
        s.append(" implements ")
        s.append(join(imp, ", ", new StringBuilder("")))
      }
      prLines(elts, "\n", s)
      s.toString

    case SAmbEnumDecl(info, id, mem) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("enum ").append(walk(id))
      prLines(mem, ", ", s)
      s.toString

    case SAmbModDecl(info, path, mem) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("module ").append(walk(path))
      prLines(mem, "\n", s)
      s.toString

    case SAmbExtModDecl(info, name, mem) =>
      val s: StringBuilder = new StringBuilder
      if (isTop) s.append("declare ")
      s.append("module \"").append(name).append("\"")
      prLines(mem, "\n", s)
      s.toString

    case SAmbCnstDecl(info, ps) =>
      val s: StringBuilder = new StringBuilder
      s.append("constructor")
      prPs(ps, s).append(";")
      s.toString

    case SAmbMemDecl(info, mods, prop, typOpt) =>
      val s: StringBuilder = new StringBuilder
      s.append(join(mods, " ", new StringBuilder("")))
      if (!mods.isEmpty) s.append(" ")
      s.append(walk(prop))
      if (typOpt.isDefined) typOpt.get match {
        case ty:CallSig => s.append(" ").append(walk(ty))
        case ty => s.append(" : ").append(walk(ty))
      }
      s.append(";")
      s.toString

    case SAmbIndDecl(info, ind) => walk(ind)

    case SAmbEnumMem(info, prop, numOpt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(prop))
      if (numOpt.isDefined) {
        s.append(" = ").append(walk(numOpt.get))
      }
      s.toString

    case SAmbModElt(info, decl) =>
      val s: StringBuilder = new StringBuilder
      s.append("export ")
      val oldIsTop = isTop
      isTop = false
      s.append(walk(decl))
      isTop = oldIsTop
      s.toString

    case SAmbExtModElt(info, decl) =>
      val s: StringBuilder = new StringBuilder
      val oldIsTop = isTop
      isTop = false
      s.append(walk(decl))
      isTop = oldIsTop
      s.toString

    case STypeName(info, text, names) => text

    // required parameter
    case SParam(info, name, modOpt, tyOpt, defOpt, false, false) =>
      val s: StringBuilder = new StringBuilder
      if (modOpt.isDefined) {
        s.append(walk(modOpt.get)).append(" ")
      }
      s.append(walk(name))
      prTyA(tyOpt, s)
      s.toString

    // optional parameter
    case SParam(info, name, modOpt, tyOpt, defOpt, true, false) =>
      val s: StringBuilder = new StringBuilder
      if (modOpt.isDefined) {
        s.append(walk(modOpt.get)).append(" ")
      }
      s.append(walk(name))
      if (!defOpt.isDefined) s.append("?")
      prTyA(tyOpt, s)
      if (defOpt.isDefined) s.append(" = ").append(walk(defOpt.get))
      s.toString

    // rest parameter
    case SParam(info, name, modOpt, tyOpt, defOpt, false, true) =>
      val s: StringBuilder = new StringBuilder
      s.append("... ").append(walk(name))
      prTyA(tyOpt, s)
      s.toString

    case SAnyT(info) => "any"
    case SNumberT(info) => "number"
    case SBoolT(info) => "boolean"
    case SStringT(info) => "string"
    case SVoidT(info) => "void"

    case STypeRef(info, name, args) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(name))
      prTs(args, s)
      s.toString

    case STypeQuery(info, path) =>
      val s: StringBuilder = new StringBuilder
      s.append("typeof ").append(walk(path))
      s.toString

    case SObjectType(info, members) =>
      val s: StringBuilder = new StringBuilder
      prLines(members, ";\n", s)
      s.toString

    case SArrayType(info, typ) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(typ)).append("[]")
      s.toString

    case SFunctionType(info, tps, ps, typ) =>
      val s: StringBuilder = new StringBuilder
      prTs(tps, s)
      prPs(ps, s)
      s.append(" => ").append(walk(typ))
      s.toString

    case SConstructorType(info, tps, ps, typ) =>
      val s: StringBuilder = new StringBuilder
      s.append("new")
      prTs(tps, s)
      prPs(ps, s)
      s.append(" => ").append(walk(typ))
      s.toString

    case SExprType(info, name) => "\""+name+"\""

    case SPropertySig(info, prop, optional, tyOpt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(prop))
      if (optional) s.append("?")
      prTyA(tyOpt, s)
      s.toString

    case SCallSig(info, tps, ps, tyOpt) =>
      val s: StringBuilder = new StringBuilder
      prTs(tps, s)
      prPs(ps, s)
      prTyA(tyOpt, s)
      s.toString

    case SConstructSig(info, tps, ps, tyOpt) =>
      val s: StringBuilder = new StringBuilder
      s.append("new")
      prTs(tps, s)
      prPs(ps, s)
      prTyA(tyOpt, s)
      s.toString

    case SIndexSig(info, id, annot, num) =>
      val s: StringBuilder = new StringBuilder
      s.append("[ ").append(walk(id)).append(" : ")
      if (num) s.append("number") else s.append("string")
      s.append(" ] : ").append(walk(annot))
      s.toString

    case SMethodSig(info, prop, optional, sig) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(prop))
      if (optional) s.append("?")
      s.append(walk(sig))
      s.toString

    case STypeParam(info, name, extOpt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(name))
      if (extOpt.isDefined)
        s.append(" extends ").append(walk(extOpt.get))
      s.toString

    case _: Modifier =>
      if (node.isInstanceOf[PublicMod]) "public"
      else if (node.isInstanceOf[PrivateMod]) "private"
      else if (node.isInstanceOf[StaticMod]) "static"
      else "#@#"+node.getClass.toString

    case _ => JSAstToConcrete.walk(node)
  }
}
