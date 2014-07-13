/******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import java.util.{List => JList}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.models._
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.domain.Obj

object WIDLHelper {
  // Integer
  val decimalGrammer = "-?[1-9][0-9]*"
  val decimalPattern = decimalGrammer.r.pattern
  val hexadecimalGrammer = "-?0[Xx][0-9A-Fa-f]+"
  val hexadecimalPattern = hexadecimalGrammer.r.pattern
  val octalGrammer = "-?0[0-7]*"
  val octalPattern = octalGrammer.r.pattern

  def getFirstAttr(node: WDefinition): String = {
    val attrs = node.getAttrs
    if(attrs.size() > 0) attrs.get(0) match {
      case SWEAString(str) => return str
      case _ =>
    }
    ""
  }
  def getOptionalParameterCount(params: List[WArgument]): Int = {
    var count = 0
    for(param <- params) if(isOptional(param)) count+= 1
    count
  }
  def getOptionalParameterCount(params: JList[WArgument]): Int = {
    var count = 0
    val i = params.iterator()
    while(i.hasNext) if(isOptional(i.next())) count+= 1
    count
  }
  def hasStrAttr(attrs: JList[WEAttribute], str1: String): Boolean = {
    val i = attrs.iterator()
    while(i.hasNext) {
      i.next() match {
        case SWEAString(str2) if str1 == str2 => return true
        case _ =>
      }
    }
    false
  }
  def isNullable(typ: WType): Boolean = {
    val suffixes: JList[WTypeSuffix] = typ.getSuffix
    for (i <- 0 until suffixes.size)
      suffixes.get(i).isInstanceOf[WTSQuestion] match {
        case true => return true
        case _ =>
      }
    false
  }
  def isArray(typ: WType): Boolean = {
    val suffixes: JList[WTypeSuffix] = typ.getSuffix
    for (i <- 0 until suffixes.size)
      suffixes.get(i).isInstanceOf[WTSArray] match {
        case true => return true
        case _ =>
      }
    false
  }
  def isCallback(interface: WInterface): Boolean = hasStrAttr(interface.getAttrs, "Callback")
  def isConstructor(interface: WInterface): Boolean = toList(interface.getAttrs).exists(_.isInstanceOf[WEAConstructor])
  def isNoInterfaceObject(interface: WInterface): Boolean = interface.getAttrs.contains(WIDLFactory.eaNoInterfaceObject)
  def isOptional(argument: WArgument): Boolean = {
    val i = argument.getAttributes.iterator()
    while(i.hasNext) if(i.next().isInstanceOf[WEAOptional]) return true
    false
  }
  def isReadOnly(member: WMember): Boolean = {
    val i = member.getAttrs.iterator()
    while(i.hasNext) if(i.next().isInstanceOf[WEAReadonly]) return true
    false
  }
  def isStatic(operation: WOperation): Boolean = {
    val i = operation.getQualifiers.iterator()
    while(i.hasNext) if(i.next().isInstanceOf[WQStatic]) return true
    false
  }
  def isUnforgeable(attribute: WAttribute): Boolean = hasStrAttr(attribute.getAttrs, "Unforgeable")

  val emptySuffix: List[WTypeSuffix] = List[WTypeSuffix]()
  def removeArraySuffix(suffix: List[WTypeSuffix]): List[WTypeSuffix] =
      suffix.foldLeft(List[WTypeSuffix]())((l,ts) => ts match {
                                             case _:WTSArray => l
                                             case _ => l++List(ts)
                                           })

  def WLiteral2Value(literal: WLiteral): Value = {
    literal match {
      case SWBoolean(info, value) => Value(AbsBool.alpha(value))
      case SWFloat(info, value) => Value(AbsNumber.alpha(value.toDouble))
      case SWInteger(info, value) =>
        if(value == "0") return Value(AbsNumber.alpha(0))
        if(decimalPattern.matcher(value).matches) return Value(AbsNumber.alpha(Integer.parseInt(value, 10)))
        if(hexadecimalPattern.matcher(value).matches) return Value(AbsNumber.alpha(Integer.parseInt(value.substring(2), 16)))
        if(octalPattern.matcher(value).matches) return Value(AbsNumber.alpha(Integer.parseInt(value, 8)))
        throw new RuntimeException("Cannot parse the \"" + value + "\" as an integer.")
      case SWString(info, str) => Value(AbsString.alpha(str))
      case SWNull(info) => Value(NullTop)
    }
  }
  def WType2Value(type1: WType, widlModel: WIDLModel): Option[Value] = {
    type1 match {
      case SWAnyType(info, suffix) =>
      case SWNamedType(info, suffix, name2) =>
        name2 match {
          case "any" => return Some(Value(PValueTop))
          case "void" => return Some(Value(UndefTop))
          case "boolean" => return Some(Value(BoolTop))
          case "byte" | "octet" | "short" | "unsigned short" |
               "long" | "unsigned long" | "long long" | "unsigned long long" |
               "float" | "unrestricted float" | "double" | "unrestricted double" =>
            return Some(Value(NumTop))
          // case "object" =>
          case "DOMString" => return Some(Value(StrTop))
          case "Date" =>
            val memberInstanceName = widlModel.getNextInterfaceName(name2)
            val memberLocProps = widlModel.newDateLocProps(memberInstanceName)
            return Some(Value(memberLocProps._1))
          case _ =>
            // enum
            WIDLTypeMap.enumMap.get(name2) match {
              case Some(enumNode) =>
                var absStr: AbsString = StrBot
                val i = enumNode.getEnumValueList.iterator()
                while(i.hasNext) absStr+= AbsString.alpha(i.next().getStr)
                return Some(Value(absStr))
              case None =>
            }
            // typedef
            WIDLTypeMap.typedefMap.get(name2) match {
              case Some(typedefNode) =>
                return WType2Value(typedefNode.getTyp, widlModel)
              case None =>
            }
            // interface
            val (locProps, protoLocProps) = widlModel.createInterfaceFromName(-1, name2)
            if(locProps != null && protoLocProps != null) {
              val memberInstanceName = widlModel.getNextInterfaceName(name2)
              val memberLocProps = widlModel.newObjectLocProps(memberInstanceName, protoLocProps._1)
              return Some(Value(memberLocProps._1))
            }
        }
      case SWArrayType(info, suffix, type2) =>
        WType2Value(type2, widlModel) match {
          case Some(defaultNumber) =>
            val memberInstanceName = widlModel.getNextInterfaceName("WIDLSequence")
            val memberLocProps = widlModel.newArrayLocProps(memberInstanceName, defaultNumber)
            return Some(Value(memberLocProps._1))
          case None =>
        }
      case SWSequenceType(info, suffix, type2) =>
        WType2Value(type2, widlModel) match {
          case Some(defaultNumber) =>
            val memberInstanceName = widlModel.getNextInterfaceName("WIDLSequence")
            val memberLocProps = widlModel.newArrayLocProps(memberInstanceName, defaultNumber)
            return Some(Value(memberLocProps._1))
          case None =>
        }
      case SWUnionType(info, suffix, types) =>
        var value: Value = ValueBot
        for(typ <- types) {
          WType2Value(typ, widlModel) match {
            case Some(v) => value+= v
            case None =>
          }
        }
        return Some(value)
    }
    None
  }
}
