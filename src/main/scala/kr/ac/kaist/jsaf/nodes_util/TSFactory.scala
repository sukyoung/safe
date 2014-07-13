/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import kr.ac.kaist.jsaf.exceptions.JSAFError.error
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF, NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.useful.Useful

import edu.rice.cs.plt.tuple.{Option => JOption}

import _root_.java.lang.{Double => JDouble}
import _root_.java.lang.{Integer => JInt}
import _root_.java.util.{List => JList}
import _root_.java.io.BufferedReader
import _root_.java.io.BufferedWriter
import _root_.java.io.File
import _root_.java.math.BigInteger
import _root_.java.math.BigDecimal
import _root_.java.util.ArrayList
import _root_.java.util.Arrays
import _root_.java.util.Collections
import _root_.java.util.Set
import _root_.java.util.StringTokenizer
import scala.collection.mutable.{HashMap => MHashMap}

object TSFactory {
  def mkInfo(span: Span): ASTSpanInfo = NF.makeSpanInfoComment(span)
  def makeAnyT(span: Span): AnyT = new AnyT(mkInfo(span))
  def makeNumberT(span: Span): NumberT = new NumberT(mkInfo(span))
  def makeBoolT(span: Span): BoolT = new BoolT(mkInfo(span))
  def makeStringT(span: Span): StringT = new StringT(mkInfo(span))
  def makeVoidT(span: Span): VoidT = new VoidT(mkInfo(span))
   
  def makeTypeName(span: Span, names: JList[Id]): TypeName = toList(names) match {
    case Nil => error("TypeName is an empty string!")
    case List(name) => new TypeName(mkInfo(span), name.getText, names)
    case name::rest =>
      new TypeName(mkInfo(span),
                   toList(rest).foldLeft(name.getText)((s,id) => s+"."+id.getText),
                   names)
  }

  def makeTypeRef(span: Span, name: TypeName, args: JList[Type]): TypeRef =
    new TypeRef(mkInfo(span), name, args)

  def makeTypeQuery(span: Span, path: Path): TypeQuery =
    new TypeQuery(mkInfo(span), path)

  def makeArrayType(span: Span, typ: Type): ArrayType =
    new ArrayType(mkInfo(span), typ)

  def makeGetTy(span: Span, typ: Type) =
    some[Type](makeFunctionType(span, emptyTparams, emptyParams, typ))

  def makeSetTy(span: Span, param: Param) =
    some[Type](makeFunctionType(span, emptyTparams, toJavaList(List(param)), makeVoidT(span)))

  def makeFunctionType(span: Span, tparams: JList[TypeParam],
                       params: JList[Param], typ: Type): FunctionType =
    new FunctionType(mkInfo(span), tparams, params, typ)

  def makeConstructorType(span: Span, tparams: JList[TypeParam],
                          params: JList[Param], typ: Type) =
    new ConstructorType(mkInfo(span), tparams, params, typ)

  def makeObjectType(span: Span, members: JList[TypeMember]) =
    new ObjectType(mkInfo(span), members)

  def makeTypeParam(span: Span, id: Id, ext: JOption[Type]) =
    new TypeParam(mkInfo(span), id, ext)

  def makePropertySig(span: Span, prop: Property, opt: Boolean, typ: JOption[Type]) =
    new PropertySig(mkInfo(span), prop, opt, typ)

  def makeCallSig(span: Span, tparams: JList[TypeParam],
                  params: JList[Param], typ: JOption[Type]) =
    new CallSig(mkInfo(span), tparams, params, typ)

  val emptyTps = toJavaList(List[TypeParam]())
  def emptyTparams = emptyTps
  val emptyParams = toJavaList(List[Param]())
  val publicMod = new PublicMod()
  val privateMod = new PrivateMod()
  val staticMod = new StaticMod()
  def makePublic() = publicMod
  def makePrivate() = privateMod
  def makeStatic() = staticMod

  def makeExprType(span: Span, name: String) =
    new ExprType(mkInfo(span), name)

  def makeReqParam(span: Span, mod: JOption[Modifier], id: Id, typ: JOption[Type]) =
    new Param(mkInfo(span), id, mod, typ, none[Expr], false, false)

  def makeOptParam(span: Span, mod: JOption[Modifier], id: Id, typ: JOption[Type],
                   expr: JOption[Expr]) =
    new Param(mkInfo(span), id, mod, typ, expr, true, false)

  def makeRestParam(span: Span, id: Id, typ: JOption[Type]) =
    new Param(mkInfo(span), id, none[Modifier], typ, none[Expr], false, true)

  def makeConstructSig(span: Span, tparams: JList[TypeParam],
                       params: JList[Param], typ: JOption[Type]) =
    new ConstructSig(mkInfo(span), tparams, params, typ)

  def makeIndexSigStr(span: Span, id: Id, annot: Type) =
    new IndexSig(mkInfo(span), id, annot, false)

  def makeIndexSigNum(span: Span, id: Id, annot: Type) =
    new IndexSig(mkInfo(span), id, annot, true)

  def makeMethodSig(span: Span, prop: Property, opt: Boolean, sig: CallSig) =
    new MethodSig(mkInfo(span), prop, opt, sig)

  def makePath(span: Span, name: StringLiteral) = {
    def makeList(str: String): List[Id] =
      if (str.contains(".")) {
        val (f,b) = str splitAt str.lastIndexOf('.')
        NF.makeId(span, f)+:makeList(b.drop(1))
      } else List(NF.makeId(span, str))
    new Path(mkInfo(span), toJavaList(makeList(name.getEscaped)))
  }

  def makeIntfDecl(span: Span, id: Id, tps: JList[TypeParam], ext: JList[TypeRef],
                   typ: ObjectType) =
    new IntfDecl(mkInfo(span), id, tps, ext, typ)

  def makeAmbVarDecl(span: Span, id: Id, typ: JOption[Type]) =
    new AmbVarDecl(mkInfo(span), id, typ)

  def makeAmbFunDecl(span: Span, id: Id, sig: CallSig) =
    new AmbFunDecl(mkInfo(span), id, sig)

  def makeAmbClsDecl(span: Span, id: Id, tps: JList[TypeParam], ext: JOption[TypeRef],
                     imp: JList[TypeRef], elts: JList[AmbClsElt]) =
    new AmbClsDecl(mkInfo(span), id, tps, ext, imp, elts)

  def makeAmbIndDecl(span: Span, ind: IndexSig) =
    new AmbIndDecl(mkInfo(span), ind)

  def makeAmbCnstDecl(span: Span, ps: JList[Param]) =
    new AmbCnstDecl(mkInfo(span), ps)

  def makeAmbMemDecl(span: Span, mods: JList[Modifier], prop: Property , typ: JOption[Type]) =
    new AmbMemDecl(mkInfo(span), mods, prop, typ)

  def makeAmbEnumMem(span: Span, prop: Property, num: JOption[NumberLiteral]) =
    new AmbEnumMem(mkInfo(span), prop, num)

  def makeAmbEnumDecl(span: Span, id: Id, mem: JList[AmbEnumMem]) =
    new AmbEnumDecl(mkInfo(span), id, mem)

  def makeAmbModDecl(span: Span, path: Path, mem: JList[AmbModElt]) =
    new AmbModDecl(mkInfo(span), path, mem)

  def makeAmbModElt(span: Span, decl: SourceElement) =
    new AmbModElt(mkInfo(span), decl)

  def makeAmbExtModDecl(span: Span, name: String, mem: JList[AmbExtModElt]) =
    new AmbExtModDecl(mkInfo(span), name, mem)

  def makeAmbExtModElt(span: Span, decl: SourceElement) =
    new AmbExtModElt(mkInfo(span), decl)

  def makeModExpAssignment(span: Span, id: Id) =
    new ModExpAssignment(mkInfo(span), id)

  def makeExtImpDecl(span: Span, id: Id, module: String) =
    new ExtImpDecl(mkInfo(span), id, module)

  def makeTSImpDecl(span: Span, id: Id, path: Path) =
    new TSImpDecl(mkInfo(span), id, path)
}
