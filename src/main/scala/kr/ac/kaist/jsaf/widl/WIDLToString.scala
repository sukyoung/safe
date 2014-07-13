/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, SpanInfo}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes.{WIDLWalker => _WIDLWalker}
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

/* Converts a WIDL AST to a string which is the concrete version of that node
 *
 * Caveats:
 * 1. Comments are not preserved.
 * 2. We do not distinguish between ExtendedAttribute and modifiers for now.
 *    For example, "readonly attribute DOMString name;" is parsed and then unparsed
 *    to "[readonly] attribute DOMString name;".
 *
 * Possible improvements:
 * 1. We may want to keep comments.
 */
object WIDLToString extends _WIDLWalker {

  val width = 50

  def doit(program: List[WDefinition]) = walk(program)
  def doit(program: JList[WDefinition]) = walk(program)

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
    case SWModule(info, _, name, defs) =>
      val s: StringBuilder = new StringBuilder
      s.append("module ").append(name).append(" {")
      s.append(join(defs, ", ", new StringBuilder("")))
      s.append("}")
      s.toString

    case SWCallback(info, attrs, name, returnType, args) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append("callback ").append(name).append(" = ")
      s.append(walk(returnType)).append("(")
      s.append(join(args, ", ", new StringBuilder("")))
      s.append(");")
      s.toString
    case SWInterface(info, attrs, name, parent, members) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
    /*
      if (isPartial(attrs)) s.append("partial ")
    */
      s.append("interface ").append(name)
      if (parent.isSome) s.append(" : ").append(walk(parent))
      s.append(" {\n")
      increaseIndent
      s.append(getIndent).append(join(members, "\n"+getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n};")
      s.toString
    case SWDictionary(info, attrs, name, parent, members) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
    /*
      if (isPartial(attrs)) s.append("partial ")
    */
      s.append("dictionary ").append(name)
      if (parent.isSome) s.append(" : ").append(walk(parent))
      s.append(" {\n")
      increaseIndent
      s.append(getIndent).append(join(members, "\n"+getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n};")
      s.toString
    case SWDictionaryMember(info, attrs, typ, name, default) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append(walk(typ)).append(" ").append(name)
      if (default.isSome) s.append(" = ").append(walk(default))
      s.append(";")
      s.toString
    case SWException(info, attrs, name, parent, members) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append("exception ").append(name)
      if (parent.isSome) s.append(" : ").append(walk(parent))
      s.append(" {\n")
      increaseIndent
      s.append(getIndent).append(join(members, "\n"+getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n};")
      s.toString
    case SWEnum(info, attrs, name, members) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append("enum ").append(name).append(" {")
      s.append(join(members, ", ", new StringBuilder("")))
      s.append("};")
      s.toString
    case SWTypedef(info, attrs, typ, name) =>
      val s: StringBuilder = new StringBuilder
      s.append("typedef ")
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append(walk(typ)).append(" ").append(name).append(";")
      s.toString
    case SWImplementsStatement(info, attrs, name, parent) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append(name).append(" implements ").append(parent).append(";")
      s.toString
    case SWConst(info, attrs, typ, name, value) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append("const ").append(walk(typ)).append(" ").append(name).append(" = ")
      s.append(walk(value)).append(";")
      s.toString
    case SWBoolean(info, value) => if(value) "true" else "false"
    case SWFloat(info, value) => value
    case SWInteger(info, value) => value
    case SWString(info, str) => str
    case SWNull(info) => "null" 
    case SWAttribute(info, attrs, typ, name, exns) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
    /*
      if (isStringifier(attrs)) s.append("stringifier ")
    */
      s.append("attribute ").append(walk(typ)).append(" ").append(name)
      if (!exns.isEmpty) {
        s.append(" raises (")
        s.append(join(exns, ", ", new StringBuilder("")))
        s.append(")")
      }
      s.append(";")
      s.toString
    case SWExceptionField(info, attrs, typ, name) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append(walk(typ)).append(" ").append(name).append(";")
      s.toString
    case SWOperation(info, attrs, qualifiers, returnType, name, args, exns) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      /*
        if (isStringifier(attrs)) s.append("stringifier ")
      */
      if (!qualifiers.isEmpty) {
        s.append(join(qualifiers, " ", new StringBuilder("")))
        s.append(" ")
     }
      s.append(walk(returnType)).append(" ")
      if (name.isSome) s.append(name.get)
      s.append("(")
      s.append(join(args, ", ", new StringBuilder("")))
      s.append(")")
      if (!exns.isEmpty) {
        s.append(" raises (")
        s.append(join(exns, ", ", new StringBuilder("")))
        s.append(")")
      }
      s.append(";")
      s.toString
    case SWArgument(info, attrs, typ, name, default) =>
      val s: StringBuilder = new StringBuilder
      if (!attrs.isEmpty) {
        s.append("[")
        s.append(join(attrs, " ", new StringBuilder("")))
        s.append("] ")
      }
      s.append(walk(typ)).append(" ").append(name)
      if (default.isSome) s.append(" = ").append(walk(default))
      s.toString
    case SWAnyType(info, suffix) =>
      val s: StringBuilder = new StringBuilder
      s.append("any")
      if (!suffix.isEmpty) {
        s.append("[]")
        s.append(join(suffix, " ", new StringBuilder("")))
      }
      s.toString
    case SWUnionType(info, suffix, types) =>
      val s: StringBuilder = new StringBuilder
      s.append("(")
      s.append(join(types, " or ", new StringBuilder("")))
      s.append(")")
      join(suffix, " ", s)
      s.toString
    case SWArrayType(info, suffix, typ) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(typ))
      s.append("[]")
      s.append(join(suffix, " ", new StringBuilder("")))
      s.toString
    case SWNamedType(info, suffix, name) =>
      val s: StringBuilder = new StringBuilder
      s.append(name)
      s.append(join(suffix, " ", new StringBuilder("")))
      s.toString
    case SWSequenceType(info, suffix, typ) =>
      val s: StringBuilder = new StringBuilder
      s.append("sequence <").append(walk(typ)).append(">")
      s.append(join(suffix, " ", new StringBuilder("")))
      s.toString
    case SWId(info, name) => name
    case SWQId(info, names) =>
      val s: StringBuilder = new StringBuilder
      s.append(join(names, "::", new StringBuilder("")))
      s.toString
    case _:SpanInfo => ""
    case xs:List[_] =>
      val s: StringBuilder = new StringBuilder
      s.append(join(xs, "\n"+getIndent, new StringBuilder("")))
      s.toString
    case xs:JList[_] =>
      val s: StringBuilder = new StringBuilder
      s.append(join(toList(xs), "\n"+getIndent, new StringBuilder("")))
      s.toString
    case Some(in) => walk(in)
    case None => ""
    case _ => walkJavaNode(node)
  }

  def walkJavaNode(node:Any):String =
    if (node.isInstanceOf[WTSArray]) "[]"
    else if (node.isInstanceOf[WTSQuestion]) "?"
    else if (node.isInstanceOf[WEAConstructor]) {
      val s: StringBuilder = new StringBuilder
      s.append("Constructor(")
      s.append(join(toList(node.asInstanceOf[WEAConstructor].getArgs), ", ", new StringBuilder("")))
      s.append(")")
      s.toString
    } else if (node.isInstanceOf[WEAArray]) "[]"
    else if (node.isInstanceOf[WEANoInterfaceObject]) "NoInterfaceObject"
    else if (node.isInstanceOf[WEACallbackFunctionOnly]) "Callback=FunctionOnly"
    else if (node.isInstanceOf[WEAString]) {
      node.asInstanceOf[WEAString].getStr
    }
    else if (node.isInstanceOf[WEAQuestion]) "?"
    else if (node.isInstanceOf[WEAEllipsis]) "..."
    else if (node.isInstanceOf[WEAOptional]) "optional"
    else if (node.isInstanceOf[WEAAttribute]) "attribute"
    else if (node.isInstanceOf[WEACallback]) "callback"
    else if (node.isInstanceOf[WEAConst]) "const"
    else if (node.isInstanceOf[WEACreator]) "creator"
    else if (node.isInstanceOf[WEADeleter]) "deleter"
    else if (node.isInstanceOf[WEADictionary]) "dictionary"
    else if (node.isInstanceOf[WEAEnum]) "enum"
    else if (node.isInstanceOf[WEAException]) "exception"
    else if (node.isInstanceOf[WEAGetter]) "getter"
    else if (node.isInstanceOf[WEAImplements]) "implements"
    else if (node.isInstanceOf[WEAInherit]) "inherit"
    else if (node.isInstanceOf[WEAInterface]) "interface"
    else if (node.isInstanceOf[WEAReadonly]) "readonly"
    else if (node.isInstanceOf[WEALegacycaller]) "legacycaller"
    else if (node.isInstanceOf[WEAPartial]) "partial"
    else if (node.isInstanceOf[WEASetter]) "setter"
    else if (node.isInstanceOf[WEAStatic]) "static"
    else if (node.isInstanceOf[WEAStringifier]) "stringifier"
    else if (node.isInstanceOf[WEATypedef]) "typedef"
    else if (node.isInstanceOf[WEAUnrestricted]) "unrestricted"
    else if (node.isInstanceOf[WQStatic]) "static"
    else if (node.isInstanceOf[WQGetter]) "getter"
    else if (node.isInstanceOf[WQSetter]) "setter"
    else if (node.isInstanceOf[WQCreator]) "creator"
    else if (node.isInstanceOf[WQDeleter]) "deleter"
    else if (node.isInstanceOf[WQLegacycaller]) "legacycaller"
    else if (node.isInstanceOf[String]) node.asInstanceOf[String]
    else "#@#"+node.getClass.toString
}
