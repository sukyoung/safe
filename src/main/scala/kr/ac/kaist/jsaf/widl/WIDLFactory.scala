/*******************************************************************************
    Copyright (c) 2012-2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, SpanInfo, SourceLocRats, Span}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import edu.rice.cs.plt.tuple.{Option => JOption}

object WIDLFactory {
  def makeSpan(villain: String): Span = {
    val sl = new SourceLocRats(villain,0,0,0)
    new Span(sl,sl)
  }

  def makeSpanInfo(span: Span): SpanInfo = new SpanInfo(span)

  def addAttrs(attrs: JList[WEAttribute], dfn: WDefinition): WDefinition = dfn match {
    case SWInterface(info, as, name, parent, members) =>
      SWInterface(info, toList(attrs)++as, name, parent, members)
    case SWCallback(info, as, name, retTy, args) =>
      SWCallback(info, toList(attrs)++as, name, retTy, args)
    case SWDictionary(info, as, name, parent, members) =>
      SWDictionary(info, toList(attrs)++as, name, parent, members)
    case SWException(info, as, name, parent, members) =>
      SWException(info, toList(attrs)++as, name, parent, members)
    case SWEnum(info, as, name, elems) =>
      SWEnum(info, toList(attrs)++as, name, elems)
    case SWTypedef(info, as, typ, name) =>
      SWTypedef(info, toList(attrs)++as, typ, name)
    case SWImplementsStatement(info, as, name, parent) =>
      SWImplementsStatement(info, toList(attrs)++as, name, parent)
  }

  def addAttrs(attrs: JList[WEAttribute], m: WConst): WConst = m match {
    case SWConst(info, as, typ, name, value) =>
      SWConst(info, toList(attrs)++as, typ, name, value)
  }

  def addAttrs(attrs: JList[WEAttribute], m: WInterfaceMember): WInterfaceMember = m match {
    case SWConst(info, as, typ, name, value) =>
      SWConst(info, toList(attrs)++as, typ, name, value)
    case SWAttribute(info, as, typ, name, exns) =>
      SWAttribute(info, toList(attrs)++as, typ, name, exns)
    case SWOperation(info, as, quals, typ, name, args, exns) =>
      SWOperation(info, toList(attrs)++as, quals, typ, name, args, exns)
  }

  def addAttrs(attrs: JList[WEAttribute], m: WExceptionMember): WExceptionMember = m match {
    case SWConst(info, as, typ, name, value) =>
      SWConst(info, toList(attrs)++as, typ, name, value)
    case SWExceptionField(info, as, typ, name) =>
      SWExceptionField(info, toList(attrs)++as, typ, name)
  }

  def addAttrs(attrs: JList[WEAttribute], m: WDictionaryMember): WDictionaryMember = m match {
    case SWDictionaryMember(info, as, typ, name, default) =>
      SWDictionaryMember(info, toList(attrs)++as, typ, name, default)
  }

  def addAttrs(attrs: JList[WEAttribute], arg: WArgument): WArgument = arg match {
    case SWArgument(info, as, typ, name, default) =>
      SWArgument(info, toList(attrs)++as, typ, name, default)
  }

  def addCallback(dfn: WDefinition): WDefinition = dfn match {
    case SWCallback(info, attrs, name, retTy, args) =>
      SWCallback(info, attrs++List(eaCallback), name, retTy, args)
    case SWInterface(info, attrs, name, parent, members) =>
      SWInterface(info, attrs++List(eaCallback), name, parent, members)
    case _ => dfn
  }       

  def addStringifier(attr: WAttribute): WAttribute = attr match {
    case SWAttribute(info, attrs, typ, name, exns) =>
      SWAttribute(info, attrs++List(eaStringifier), typ, name, exns)
  }        

  def addPartial(dfn: WDefinition): WDefinition = dfn match {
    case SWInterface(info, attrs, name, parent, members) =>
      SWInterface(info, attrs++List(eaPartial), name, parent, members)
    case SWDictionary(info, attrs, name, parent, members) =>
      SWDictionary(info, attrs++List(eaPartial), name, parent, members)
    case _ => dfn
  }

  def mkModule(span: Span, name: String, defs: JList[WDefinition]): WModule =
    new WModule(makeSpanInfo(span), name, defs)

  def mkInterface(span: Span, name: String, parent: Option[WId],
                  members: JList[WInterfaceMember]): WInterface =
    new WInterface(makeSpanInfo(span), toJavaList(List[WEAttribute]()),
                   name, toJavaOption(parent), members)
  def mkInterface(span: Span, name: String, parent: WId,
                  members: JList[WInterfaceMember]): WInterface =
    mkInterface(span, name, some(parent), members)
  def mkInterface(span: Span, name: String, parent: WId): WInterface =
    mkInterface(span, name, some(parent), toJavaList(List[WInterfaceMember]()))
  def mkInterface(span: Span, name: String, members: JList[WInterfaceMember]): WInterface =
    mkInterface(span, name, none, members)
  def mkInterface(span: Span, name: String): WInterface =
    mkInterface(span, name, none, toJavaList(List[WInterfaceMember]()))

  def mkDictionary(span: Span, name: String, parent: Option[WId],
                   members: JList[WDictionaryMember]): WDictionary =
    new WDictionary(makeSpanInfo(span), name, toJavaOption(parent), members)
  def mkDictionary(span: Span, name: String, parent: WId,
                   members: JList[WDictionaryMember]): WDictionary =
    mkDictionary(span, name, some(parent), members)
  def mkDictionary(span: Span, name: String,
                   members: JList[WDictionaryMember]): WDictionary =
    mkDictionary(span, name, none, members)
  def mkDictionary(span: Span, name: String, parent: WId): WDictionary =
    mkDictionary(span, name, some(parent), toJavaList(List[WDictionaryMember]()))
  def mkDictionary(span: Span, name: String): WDictionary =
    mkDictionary(span, name, none, toJavaList(List[WDictionaryMember]()))

  def mkDictionaryMember(span: Span, typ: WType, name: String,
                         default: Option[WLiteral]): WDictionaryMember =
    new WDictionaryMember(makeSpanInfo(span), typ, name, toJavaOption(default))
  def mkDictionaryMember(span: Span, typ: WType, name: String,
                         default: WLiteral): WDictionaryMember =
    mkDictionaryMember(span, typ, name, some(default))
  def mkDictionaryMember(span: Span, typ: WType, name: String): WDictionaryMember =
    mkDictionaryMember(span, typ, name, none)

  def mkException(span: Span, name: String, parent: Option[WId],
                  members: JList[WExceptionMember]): WException =
    new WException(makeSpanInfo(span), name, toJavaOption(parent), members)
  def mkException(span: Span, name: String, parent: WId,
                  members: JList[WExceptionMember]): WException =
    mkException(span, name, some(parent), members)
  def mkException(span: Span, name: String, members: JList[WExceptionMember]): WException =
    mkException(span, name, none, members)
  def mkException(span: Span, name: String, parent: WId): WException =
    mkException(span, name, some(parent), toJavaList(List[WExceptionMember]()))
  def mkException(span: Span, name: String): WException =
    mkException(span, name, none, toJavaList(List[WExceptionMember]()))

  def mkEnum(span: Span, name: String, enumValueList: JList[WString]): WEnum =
    new WEnum(makeSpanInfo(span), name, enumValueList)

  def mkCallback(span: Span, name: String, returnType: WType,
                 args: JList[WArgument]): WCallback =
    new WCallback(makeSpanInfo(span), name, returnType, args)
  def mkCallback(span: Span, name: String, returnType: WType): WCallback =
    mkCallback(span, name, returnType, toJavaList(List[WArgument]()))

  def mkTypedef(span: Span, attrs: JList[WEAttribute], typ: WType, name: String): WTypedef =
    new WTypedef(makeSpanInfo(span), attrs, typ, name)
  def mkTypedef(span: Span, typ: WType, name: String): WTypedef =
    mkTypedef(span, toJavaList(List[WEAttribute]()), typ, name)

  def mkImplementsStatement(span: Span, name: String, parent: String): WImplementsStatement =
    new WImplementsStatement(makeSpanInfo(span), name, parent)

  def mkConst(span: Span, typ: WType, name: String, value: WLiteral): WConst =
    new WConst(makeSpanInfo(span), typ, name, value)

  def mkOperation(span: Span, attrs: JList[WEAttribute], qualifiers: JList[WQualifier],
                  typ: WType, name: Option[String], args: JList[WArgument], exns: JList[WQId]): WOperation =
    new WOperation(makeSpanInfo(span), attrs, qualifiers, typ, toJavaOption(name), args, exns)
  def mkOperation(span: Span, attrs: JList[WEAttribute], qualifiers: JList[WQualifier],
                  typ: WType, args: JList[WArgument]): WOperation =
    mkOperation(span, attrs, qualifiers, typ, None, args, toJavaList(List[WQId]()))
  def mkOperation(span: Span, attrs: JList[WEAttribute], qualifiers: JList[WQualifier],
                  typ: WType, name: String, args: JList[WArgument]): WOperation =
    mkOperation(span, attrs, qualifiers, typ, some(name), args, toJavaList(List[WQId]()))
  def mkOperationExn(span: Span, attrs: JList[WEAttribute], qualifiers: JList[WQualifier],
                     typ: WType, args: JList[WArgument], exns: JList[WQId]): WOperation =
    mkOperation(span, attrs, qualifiers, typ, None, args, exns)
  def mkOperationExn(span: Span, attrs: JList[WEAttribute], qualifiers: JList[WQualifier],
                     typ: WType, name: String, args: JList[WArgument], exns: JList[WQId]): WOperation =
    mkOperation(span, attrs, qualifiers, typ, some(name), args, exns)

  def mkAttribute(span: Span, attrs: JList[WEAttribute], typ: WType, name: String): WAttribute =
    mkAttribute(span, attrs, typ, name, toJavaList(List[WQId]()))

  def mkAttribute(span: Span, attrs: JList[WEAttribute], typ: WType, name: String, exns: JList[WQId]): WAttribute =
    new WAttribute(makeSpanInfo(span), attrs, typ, name, exns)

  def mkArgument(span: Span, attrs: JList[WEAttribute], typ: WType, name: String,
                 default: Option[WLiteral]): WArgument =
    new WArgument(makeSpanInfo(span), attrs, typ, name, toJavaOption(default))
  def mkArgument(span: Span, attrs: JList[WEAttribute], typ: WType,
                 name: String, default: WLiteral): WArgument =
    mkArgument(span, attrs, typ, name, some(default))
  def mkArgument(span: Span, attrs: JList[WEAttribute], typ: WType,
                 name: String): WArgument =
    mkArgument(span, attrs, typ, name, none)

  def mkExceptionField(span: Span, typ: WType, name: String) =
    new WExceptionField(makeSpanInfo(span), typ, name)

  /* Literals **********************************************************/
  val nullLt = new WNull(makeSpanInfo(makeSpan("null")))
  def nullL(): WNull = nullLt
  val infinityLt = new WFloat(makeSpanInfo(makeSpan("Infinity")), "Infinity")
  def infinity(): WFloat = infinityLt
  val minusInfinityLt = new WFloat(makeSpanInfo(makeSpan("-Infinity")), "-Infinity")
  def minusInfinity(): WFloat = minusInfinityLt
  val nanLt = new WFloat(makeSpanInfo(makeSpan("NaN")), "NaN")
  def nan(): WFloat = nanLt
  val trueLt = new WBoolean(makeSpanInfo(makeSpan("true")), true)
  def trueL(): WBoolean = trueLt
  val falseLt = new WBoolean(makeSpanInfo(makeSpan("false")), false)
  def falseL(): WBoolean = falseLt

  def mkString(span: Span, str: String) =
    new WString(makeSpanInfo(span), str)

  def mkId(span: Span, name: String) =
    new WId(makeSpanInfo(span), name)

  def mkQId(span: Span, names: JList[String]) =
    new WQId(makeSpanInfo(span), names)

  def mkFloat(span: Span, value: String): WFloat =
    new WFloat(makeSpanInfo(span), value)

  def mkInteger(span: Span, value: String): WInteger =
    new WInteger(makeSpanInfo(span), value)

  /* Types **********************************************************/
  def mkNamedType(span: Span, name: String, suffix: JList[WTypeSuffix]): WNamedType =
    new WNamedType(makeSpanInfo(span), suffix, name)
  def mkNamedType(span: Span, name: String): WNamedType =
    mkNamedType(span, name, toJavaList(List[WTypeSuffix]()))
  def mkNamedType(span: Span, name: String, suffix: WTypeSuffix): WNamedType =
    mkNamedType(span, name, toJavaList(List(suffix)))
  def mkNamedType(name: String): WNamedType =
    mkNamedType(makeSpan(name), name)
  val anyTyp = mkNamedType("any")
  def anyType(): WType = anyTyp
  def mkAnyType(span: Span, suffix: JList[WTypeSuffix]): WType =
    new WAnyType(makeSpanInfo(span), suffix)
  val domstringTyp = mkNamedType("DOMString")
  def domstringType(): WType = domstringTyp
  val dateTyp = mkNamedType("Date")
  def dateType(): WType = dateTyp
  val objectTyp = mkNamedType("object")
  def objectType(): WType = objectTyp
  val voidTyp = mkNamedType("void")
  def voidType(): WType = voidTyp
  // integer types
  val shortTyp = mkNamedType("short")
  def shortType(): WType = shortTyp
  val longTyp = mkNamedType("long")
  def longType(): WType = longTyp
  val longLongTyp = mkNamedType("long long")
  def longLongType(): WType = longLongTyp
  val floatTyp = mkNamedType("float")
  def floatType(): WType = floatTyp
  val doubleTyp = mkNamedType("double")
  def doubleType(): WType = doubleTyp
  val booleanTyp = mkNamedType("boolean")
  def booleanType(): WType = booleanTyp
  val byteTyp = mkNamedType("byte")
  def byteType(): WType = byteTyp
  val octetTyp = mkNamedType("octet")
  def octetType(): WType = octetTyp
  def unsinged(intType: WType): WType = intType match {
    case SWNamedType(info, s, name) =>
      SWNamedType(info, s, "unsigned "+name)
  }
  def unrestricted(floatType: WType): WType = floatType match {
    case SWNamedType(info, s, name) =>
      SWNamedType(info, s, "unrestricted "+name)
  }
  def anyArrayType(): WType = 
    mkAnyArrayType(makeSpan("any array"), toJavaList(List[WTypeSuffix]()))
  def mkAnyArrayType(span: Span, suffix: JList[WTypeSuffix]): WType = 
    mkArrayType(span, anyTyp, suffix)
  def mkArrayType(span: Span, typ: WType, suffix: JList[WTypeSuffix]): WType = 
    new WArrayType(makeSpanInfo(span), suffix, typ)

  def addSuffix(suffix: List[WTypeSuffix], suf: WTypeSuffix): List[WTypeSuffix] =
    suffix++List(suf)
  def addSuffix(suffix: List[WTypeSuffix], suf: JList[WTypeSuffix]): List[WTypeSuffix] =
    suffix++toList(suf)
  def addTypeSuffix(typ: WType, suf: JList[WTypeSuffix]): WType = typ match {
    case SWAnyType(info, suffix) =>
      SWAnyType(info, addSuffix(suffix, suf))
    case SWNamedType(info, suffix, name) =>
      SWNamedType(info, addSuffix(suffix, suf), name)
    case SWArrayType(info, suffix, typ) =>
      SWArrayType(info, addSuffix(suffix, suf), typ)
    case SWUnionType(info, suffix, types) =>
      SWUnionType(info, addSuffix(suffix, suf), types)
  }
  def questionType(typ: WType): WType = typ match {
    case SWAnyType(info, suffix) =>
      SWAnyType(info, addSuffix(suffix, tQuestion))
    case SWNamedType(info, suffix, name) =>
      SWNamedType(info, addSuffix(suffix, tQuestion), name)
    case SWArrayType(info, suffix, typ) =>
      SWArrayType(info, addSuffix(suffix, tQuestion), typ)
    case SWUnionType(info, suffix, types) =>
      SWUnionType(info, addSuffix(suffix, tQuestion), types)
  }
  def mkUnionType(span: Span, types: JList[WType], suffix: JList[WTypeSuffix]): WUnionType =
    new WUnionType(makeSpanInfo(span), suffix, types)
  def mkUnionType(span: Span, types: JList[WType]): WUnionType =
    mkUnionType(span, types, toJavaList(List[WTypeSuffix]()))
  def mkSequenceType(span: Span, typ: WType, suffix: JList[WTypeSuffix]): WType =
    new WSequenceType(makeSpanInfo(span), suffix, typ)

  /* ArgumentNameKeyword **********************************************************/
  def mkEAArgumentNameKeyword(name: String): WEAttribute =
    if (name.equals("attribute")) eaAttribute
    else if (name.equals("callback")) eaCallback
    else if (name.equals("const")) eaConst
    else if (name.equals("creator")) eaCreator
    else if (name.equals("deleter")) eaDeleter
    else if (name.equals("dictionary")) eaDictionary
    else if (name.equals("enum")) eaEnum
    else if (name.equals("exception")) eaException
    else if (name.equals("getter")) eaGetter
    else if (name.equals("implements")) eaImplements
    else if (name.equals("inherit")) eaInherit
    else if (name.equals("interface")) eaInterface
    else if (name.equals("legacycaller")) eaLegacycaller
    else if (name.equals("partial")) eaPartial
    else if (name.equals("setter")) eaSetter
    else if (name.equals("static")) eaStatic
    else if (name.equals("stringifier")) eaStringifier
    else if (name.equals("typedef")) eaTypedef
    else if (name.equals("unrestricted")) eaUnrestricted
    else throw new IllegalArgumentException("wrong ArgumentNameKeyword")

  /* Type suffixes **********************************************************/
   val tArray = new WTSArray()
   def tsArray(): WTypeSuffix = tArray
   val tQuestion = new WTSQuestion()
   def tsQuestion(): WTypeSuffix = tQuestion

  /* Extended attributes ***************************************************/
   def mkEAConstructor(args: JList[WArgument]): WEAttribute = new WEAConstructor(args)
   val eArray = new WEAArray()
   def eaArray(): WEAttribute = eArray
   val eNoInterfaceObject = new WEANoInterfaceObject()
   def eaNoInterfaceObject(): WEAttribute = eNoInterfaceObject
   val eCallbackFunctionOnly = new WEACallbackFunctionOnly()
   def eaCallbackFunctionOnly(): WEAttribute = eCallbackFunctionOnly
   def mkEAString(str: String): WEAttribute = new WEAString(str)
   val eQuestion = new WEAQuestion()
   def eaQuestion(): WEAttribute = eQuestion
   val eEllipsis = new WEAEllipsis()
   def eaEllipsis(): WEAttribute = eEllipsis
   val eOptional = new WEAOptional()
   def eaOptional(): WEAttribute = eOptional
   val eAttribute = new WEAAttribute()
   def eaAttribute(): WEAttribute = eAttribute
   val eCallback = new WEACallback()
   def eaCallback(): WEAttribute = eCallback
   val eConst = new WEAConst()
   def eaConst(): WEAttribute = eConst
   val eCreator = new WEACreator()
   def eaCreator(): WEAttribute = eCreator
   val eDeleter = new WEADeleter()
   def eaDeleter(): WEAttribute = eDeleter
   val eDictionary = new WEADictionary()
   def eaDictionary(): WEAttribute = eDictionary
   val eEnum = new WEAEnum()
   def eaEnum(): WEAttribute = eEnum
   val eException = new WEAException()
   def eaException(): WEAttribute = eException
   val eGetter = new WEAGetter()
   def eaGetter(): WEAttribute = eGetter
   val eImplements = new WEAImplements()
   def eaImplements(): WEAttribute = eImplements
   val eInherit = new WEAInherit()
   def eaInherit(): WEAttribute = eInherit
   val eReadonly = new WEAReadonly()
   def eaReadonly(): WEAttribute = eReadonly
   val eInterface = new WEAInterface()
   def eaInterface(): WEAttribute = eInterface
   val eLegacycaller = new WEALegacycaller()
   def eaLegacycaller(): WEAttribute = eLegacycaller
   val ePartial = new WEAPartial()
   def eaPartial(): WEAttribute = ePartial
   val eSetter = new WEASetter()
   def eaSetter(): WEAttribute = eSetter
   val eStatic = new WEAStatic()
   def eaStatic(): WEAttribute = eStatic
   val eStringifier = new WEAStringifier()
   def eaStringifier(): WEAttribute = eStringifier
   val eTypedef = new WEATypedef()
   def eaTypedef(): WEAttribute = eTypedef
   val eUnrestricted = new WEAUnrestricted()
   def eaUnrestricted(): WEAttribute = eUnrestricted

  /* Qualifiers **********************************************************/
  val wqStatic = new WQStatic()
  def qStatic(): WQualifier = wqStatic
  val wqGetter = new WQGetter()
  def qGetter(): WQualifier = wqGetter
  val wqSetter = new WQSetter()
  def qSetter(): WQualifier = wqSetter
  val wqCreator = new WQCreator()
  def qCreator(): WQualifier = wqCreator
  val wqDeleter = new WQDeleter()
  def qDeleter(): WQualifier = wqDeleter
  val wqLegacycaller = new WQLegacycaller()
  def qLegacycaller(): WQualifier = wqLegacycaller
}
