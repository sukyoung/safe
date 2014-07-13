/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import java.io.{BufferedWriter, FileWriter}
import java.util.{ArrayList, List => JList}
import scala.io.Source
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, SpanInfo}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes.{WIDLWalker => _WIDLWalker}
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.widl.{WIDLFactory => WF}
import kr.ac.kaist.jsaf.widl.WIDLToDB._

object DBToWIDL extends _WIDLWalker {

  object Done extends Exception
  val span = WF.makeSpan("from WIDL DB")

  ////////////////////////////////////////////////////////
  // read the DB to reconstruct WIDL
  ////////////////////////////////////////////////////////
  /**
   * Transform WIDL to a well formed format:
   *  - translate a type with the WTSArray type suffix
   *    to a WArrayType type
   */
  def wellFormed(widl: List[WDefinition]): List[WDefinition] =
    widl.map(d => walk(d).asInstanceOf[WDefinition])
  override def walk(node:Any): Any = node match {
    case t@SWAnyType(info, suffix) if WIDLHelper.isArray(t) =>
      SWArrayType(info, WIDLHelper.removeArraySuffix(suffix),
                  SWAnyType(info, WIDLHelper.emptySuffix))
    case t@SWNamedType(info, suffix, name) if WIDLHelper.isArray(t) =>
      SWArrayType(info, WIDLHelper.removeArraySuffix(suffix),
                  SWNamedType(info, WIDLHelper.emptySuffix, name))
    case t@SWArrayType(info, suffix, typ) if WIDLHelper.isArray(t) =>
      throw new RuntimeException("WArrayType is not expected.")
    case t@SWSequenceType(info, suffix, typ) if WIDLHelper.isArray(t) =>
      SWArrayType(info, WIDLHelper.removeArraySuffix(suffix),
                  SWSequenceType(info, WIDLHelper.emptySuffix,
                                 walk(typ).asInstanceOf[WType]))
    case t@SWUnionType(info, suffix, types) if WIDLHelper.isArray(t) =>
      SWArrayType(info, WIDLHelper.removeArraySuffix(suffix),
                  SWUnionType(info, WIDLHelper.emptySuffix,
                              types.map(t => walk(t).asInstanceOf[WType])))
    case sp:SpanInfo => sp
    case _ => super.walk(node)
  }

  /**
   * Read the DB to reconstruct WIDL
   */
  def readDB(fileName: String): List[WDefinition] = {
    val bs = Source.fromFile(fileName)
    val br = bs.getLines
    var result = List[WDefinition]()
    def readLine() = if (br.hasNext) br.next else throw Done
    def readInt() = Integer.parseInt(readLine)
    def readDefinition(): WDefinition = readInt match {
      case MODULE => readModule
      case INTERFACE => readInterface
      case CALLBACK => readCallback
      case DICTIONARY => readDictionary
      case EXCEPTION => readException
      case ENUM => readEnum
      case TYPEDEF => readTypedef
      case IMPLEMENTS => readImplements
    }
    def readModule(): WModule = {
      val name = readLine
      val defsNum = readInt
      var defs = new ArrayList[WDefinition](defsNum)
      for (i <- 0 until defsNum) defs.add(readDefinition)
      WF.mkModule(span, name, defs)
    }
    def readInterface(): WInterface = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val parentOpt = readLine
      val parent = if (parentOpt.isEmpty) none else some(WF.mkId(span, parentOpt))
      val memsNum = readInt
      var mems = new ArrayList[WInterfaceMember](memsNum)
      for (i <- 0 until memsNum) mems.add(readInterfaceMember)
      val dict = WF.mkInterface(span, name, parent, mems)
      WF.addAttrs(attrs, dict).asInstanceOf[WInterface]
    }
    def readInterfaceMember(): WInterfaceMember = readInt match {
      case CONST => readConst(INTERFACE)
      case ATTRIBUTE => readWAttribute
      case OPERATION => readOperation
      case _ => throw new IllegalArgumentException("wrong InterfaceMember")
    }
    def readCallback(): WCallback = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      val argsNum = readInt
      var args = new ArrayList[WArgument](argsNum)
      for (i <- 0 until argsNum) args.add(readArgument)
      val call = WF.mkCallback(span, name, typ, args)
      WF.addAttrs(attrs, call).asInstanceOf[WCallback]
    }
    def readDictionary(): WDictionary = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val parentOpt = readLine
      val parent = if (parentOpt.isEmpty) none else some(WF.mkId(span, parentOpt))
      val memsNum = readInt
      var mems = new ArrayList[WDictionaryMember](memsNum)
      for (i <- 0 until memsNum) mems.add(readDictionaryMember)
      val dict = WF.mkDictionary(span, name, parent, mems)
      WF.addAttrs(attrs, dict).asInstanceOf[WDictionary]
    }
    def readDictionaryMember(): WDictionaryMember = {
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      val name = readLine
      val defaultOpt = readLine
      val default = if (defaultOpt.isEmpty) none else some(readLiteral(defaultOpt))
      WF.addAttrs(attrs, WF.mkDictionaryMember(span, typ, name, default))
    }
    def readException(): WException = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val parentOpt = readLine
      val parent = if (parentOpt.isEmpty) none else some(WF.mkId(span, parentOpt))
      val memsNum = readInt
      var mems = new ArrayList[WExceptionMember](memsNum)
      for (i <- 0 until memsNum) mems.add(readExceptionMember)
      val dict = WF.mkException(span, name, parent, mems)
      WF.addAttrs(attrs, dict).asInstanceOf[WException]
    }
    def readExceptionMember(): WExceptionMember = readInt match {
      case CONST => readConst(EXCEPTION)
      case EXCEPTIONFIELD => readExceptionField
      case _ => throw new IllegalArgumentException("wrong ExceptionMember")
    }
    def readConst(parent: Int): WConst = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      val value = readLiteral(readLine)
      val const = WF.mkConst(span, typ, name, value)
      parent match {
        case INTERFACE =>
          WF.addAttrs(attrs, const.asInstanceOf[WInterfaceMember]).asInstanceOf[WConst]
        case EXCEPTION =>
          WF.addAttrs(attrs, const.asInstanceOf[WExceptionMember]).asInstanceOf[WConst]
      }
    }
    def readWAttribute(): WAttribute = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      val exnsNum = readInt
      var exns = new ArrayList[WQId](exnsNum)
      for (i <- 0 until exnsNum) exns.add(readExn)
      WF.addAttrs(attrs, WF.mkAttribute(span, attrs, typ, name, exns)).asInstanceOf[WAttribute]
    }
    def readOperation(): WOperation = {
      val nameOpt = readLine
      val name = if (nameOpt.isEmpty) none else some(nameOpt)
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val qualsNum = readInt
      var quals = new ArrayList[WQualifier](qualsNum)
      for (i <- 0 until qualsNum) quals.add(readQualifier)
      val typ = readType
      val argsNum = readInt
      var args = new ArrayList[WArgument](argsNum)
      for (i <- 0 until argsNum) args.add(readArgument)
      val exnsNum = readInt
      var exns = new ArrayList[WQId](exnsNum)
      for (i <- 0 until exnsNum) exns.add(readExn)
      WF.mkOperation(span, attrs, quals, typ, name, args, exns)
    }
    def readArgument(): WArgument = {
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      val name = readLine
      val defaultOpt = readLine
      val default = if (defaultOpt.isEmpty) none else some(readLiteral(defaultOpt))
      WF.mkArgument(span, attrs, typ, name, default)
    }
    def readExceptionField(): WExceptionField = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      WF.addAttrs(attrs, WF.mkExceptionField(span, typ, name)).asInstanceOf[WExceptionField]
    }
    def readEnum(): WEnum = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val memsNum = readInt
      var mems = new ArrayList[WString](memsNum)
      for (i <- 0 until memsNum) mems.add(readString)
      val enum = WF.mkEnum(span, name, mems)
      WF.addAttrs(attrs, enum).asInstanceOf[WEnum]
    }
    def readTypedef(): WTypedef = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val typ = readType
      WF.mkTypedef(span, attrs, typ, name)
    }
    def readImplements(): WImplementsStatement = {
      val name = readLine
      val attrsNum = readInt
      var attrs = new ArrayList[WEAttribute](attrsNum)
      for (i <- 0 until attrsNum) attrs.add(readAttribute)
      val parent = readLine
      val imp = WF.mkImplementsStatement(span, name, parent)
      WF.addAttrs(attrs, imp).asInstanceOf[WImplementsStatement]
    }
    def readExn(): WQId = {
      readLine;
      val namesNum = readInt
      var names = new ArrayList[String](namesNum)
      for (i <- 0 until namesNum) names.add(readLine)
      WF.mkQId(span, names)
    }
    def readString(): WString = { readLine; WF.mkString(span, readLine) }
    def readAttribute(): WEAttribute = readInt match {
      case EACONSTRUCTOR =>
        val argsNum = readInt
        var args = new ArrayList[WArgument](argsNum)
        for (i <- 0 until argsNum) args.add(readArgument)
        WF.mkEAConstructor(args)
      case EAARRAY => WF.eaArray
      case EANOINTERFACEOBJECT => WF.eaNoInterfaceObject
      case EASTRING => WF.mkEAString(readLine)
      case EAQUESTION => WF.eaQuestion
      case EAELLIPSIS => WF.eaEllipsis
      case EAOPTIONAL => WF.eaOptional
      case EAATTRIBUTE => WF.eaAttribute
      case EACALLBACK => WF.eaCallback
      case EACONST => WF.eaConst
      case EACREATOR => WF.eaCreator
      case EADELETER => WF.eaDeleter
      case EADICTIONARY => WF.eaDictionary
      case EAENUM => WF.eaEnum
      case EAEXCEPTION => WF.eaException
      case EAGETTER => WF.eaGetter
      case EAIMPLEMENTS => WF.eaImplements
      case EAINHERIT => WF.eaInherit
      case EAINTERFACE => WF.eaInterface
      case EAREADONLY => WF.eaReadonly
      case EALEGACYCALLER => WF.eaLegacycaller
      case EAPARTIAL => WF.eaPartial
      case EASETTER => WF.eaSetter
      case EASTATIC => WF.eaStatic
      case EASTRINGIFIER => WF.eaStringifier
      case EATYPEDEF => WF.eaTypedef
      case EAUNRESTRICTED => WF.eaUnrestricted
      case _ => throw new IllegalArgumentException("wrong EAttribute")
    }
    def readTypeSuffix(): WTypeSuffix = readInt match {
      case TSARRAY => WF.tsArray
      case TSQUESTION => WF.tsQuestion
      case _ => throw new IllegalArgumentException("wrong TypeSuffix")
    }
    def readQualifier(): WQualifier = readInt match {
      case QSTATIC => WF.qStatic
      case QGETTER => WF.qGetter
      case QSETTER => WF.qSetter
      case QCREATOR => WF.qCreator
      case QDELETER => WF.qDeleter
      case QLEGACYCALLER => WF.qLegacycaller
      case _ => throw new IllegalArgumentException("wrong Qualifier")
    }
    def readType(): WType = readInt match {
      case ANYTYPE =>
        val suffNum = readInt
        var suff = new ArrayList[WTypeSuffix](suffNum)
        for (i <- 0 until suffNum) suff.add(readTypeSuffix)
        WF.mkAnyType(span, suff)
      case NAMEDTYPE =>
        val suffNum = readInt
        var suff = new ArrayList[WTypeSuffix](suffNum)
        for (i <- 0 until suffNum) suff.add(readTypeSuffix)
        WF.mkNamedType(span, readLine, suff)
      case ARRAYTYPE =>
        val suffNum = readInt
        var suff = new ArrayList[WTypeSuffix](suffNum)
        for (i <- 0 until suffNum) suff.add(readTypeSuffix)
        val typ = readType
        WF.mkArrayType(span, typ, suff)
      case SEQUENCETYPE =>
        val suffNum = readInt
        var suff = new ArrayList[WTypeSuffix](suffNum)
        for (i <- 0 until suffNum) suff.add(readTypeSuffix)
        val typ = readType
        WF.mkSequenceType(span, typ, suff)
      case UNIONTYPE =>
        val suffNum = readInt
        var suff = new ArrayList[WTypeSuffix](suffNum)
        for (i <- 0 until suffNum) suff.add(readTypeSuffix)
        val typesNum = readInt
        var types = new ArrayList[WType](typesNum)
        for (i <- 0 until typesNum) types.add(readType)
        WF.mkUnionType(span, types, suff)
      case n => throw new IllegalArgumentException("wrong Type")
    }
    def readLiteral(lit: String): WLiteral = Integer.parseInt(lit) match {
      case BOOLEAN => readInt match {
        case TRUE => WF.trueL
        case FALSE => WF.falseL
      }
      case FLOAT => WF.mkFloat(span, readLine)
      case INTEGER => WF.mkInteger(span, readLine)
      case STRING => WF.mkString(span, readLine)
      case NULL => WF.nullL
      case _ => throw new IllegalArgumentException("wrong Literal")
    }
    try {
      while (true) result ++= List(readDefinition)
      wellFormed(result)
    } catch {
      case Done =>
        wellFormed(result)
      case e => throw e
    } finally {
      bs.close
    }
  }
}
