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

package kr.ac.kaist.safe.json

import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.json.NodeProtocol._
import kr.ac.kaist.safe.json.CFGExprProtocol._
import kr.ac.kaist.safe.errors.error._

import spray.json._
import DefaultJsonProtocol._

object CFGInstProtocol extends DefaultJsonProtocol {

  var cfg: CFG = _
  var block: CFGBlock = _

  implicit object CFGNormalInstJsonFormat extends RootJsonFormat[CFGNormalInst] {

    def nBlock: NormalBlock = block.asInstanceOf[NormalBlock]

    def write(inst: CFGNormalInst): JsValue = {
      val name: String = inst.getClass.getSimpleName
      inst match {
        case CFGAlloc(ir, _, lhs, proto, asite) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          proto match {
            case Some(expr) => expr.toJson
            case None => JsNull
          },
          asite.toJson
        )
        case CFGAllocArray(ir, _, lhs, length, asite) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          JsNumber(length),
          asite.toJson
        )
        case CFGAllocArg(ir, _, lhs, length, asite) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          JsNumber(length),
          asite.toJson
        )
        case CFGEnterCode(ir, _, lhs, expr) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          expr.toJson
        )
        case CFGExprStmt(ir, _, lhs, right) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          right.toJson
        )
        case CFGDelete(ir, _, lhs, expr) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          expr.toJson
        )
        case CFGDeleteProp(ir, _, lhs, obj, index) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          obj.toJson,
          index.toJson
        )
        case CFGStore(ir, _, obj, index, rhs) => JsArray(
          JsString(name),
          ir.toJson,
          obj.toJson,
          index.toJson,
          rhs.toJson
        )
        case CFGStoreStringIdx(ir, _, obj, index, rhs) => JsArray(
          JsString(name),
          ir.toJson,
          obj.toJson,
          index.asInstanceOf[EJSVal].toJson,
          rhs.toJson
        )
        case CFGFunExpr(ir, _, lhs, nameOpt, func, asite1, asite2, asite3) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          nameOpt match {
            case Some(id) => id.toJson
            case None => JsNull
          },
          JsNumber(func.id),
          asite1.toJson,
          asite2.toJson,
          asite3 match {
            case Some(site) => site.toJson
            case None => JsNull
          }
        )
        case CFGAssert(ir, _, expr, flag) => JsArray(
          JsString(name),
          ir.toJson,
          expr.toJson,
          JsBoolean(flag)
        )
        case CFGCatch(ir, _, nameId) => JsArray(
          JsString(name),
          ir.toJson,
          nameId.toJson
        )
        case CFGReturn(ir, _, expr) => JsArray(
          JsString(name),
          ir.toJson,
          expr match {
            case Some(e) => e.toJson
            case None => JsNull
          }
        )
        case CFGThrow(ir, _, expr) => JsArray(
          JsString(name),
          ir.toJson,
          expr.toJson
        )
        case CFGNoOp(ir, _, desc) => JsArray(
          JsString(name),
          ir.toJson,
          JsString(desc)
        )
        case CFGInternalCall(ir, _, lhs, nameStr, arguments, asite) => JsArray(
          JsString(name),
          ir.toJson,
          lhs.toJson,
          JsString(nameStr),
          JsArray(arguments.map(_.toJson).to[Vector]),
          asite match {
            case Some(site) => site.toJson
            case None => JsNull
          }
        )
      }
    }

    def read(value: JsValue): CFGNormalInst = value match {
      case JsArray(Vector(JsString("CFGAlloc"), ir, lhs, proto, asite)) =>
        CFGAlloc(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          proto match {
            case JsNull => None
            case _ => Some(proto.convertTo[CFGExpr])
          },
          AllocSite.fromJson(asite)
        )
      case JsArray(Vector(JsString("CFGAllocArray"), ir, lhs, JsNumber(len), asite)) =>
        CFGAllocArray(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          len.toInt,
          AllocSite.fromJson(asite)
        )
      case JsArray(Vector(JsString("CFGAllocArg"), ir, lhs, JsNumber(len), asite)) =>
        CFGAllocArg(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          len.toInt,
          AllocSite.fromJson(asite)
        )
      case JsArray(Vector(JsString("CFGEnterCode"), ir, lhs, expr)) =>
        CFGEnterCode(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          expr.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGExprStmt"), ir, lhs, right)) =>
        CFGExprStmt(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          right.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGDelete"), ir, lhs, expr)) =>
        CFGDelete(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          expr.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGDeleteProp"), ir, lhs, obj, index)) =>
        CFGDeleteProp(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          obj.convertTo[CFGExpr],
          index.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGStore"), ir, obj, index, rhs)) =>
        CFGStore(
          ir.convertTo[IRNode],
          nBlock,
          obj.convertTo[CFGExpr],
          index.convertTo[CFGExpr],
          rhs.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGStoreStringIdx"), ir, obj, index, rhs)) =>
        CFGStoreStringIdx(
          ir.convertTo[IRNode],
          nBlock,
          obj.convertTo[CFGExpr],
          index.convertTo[EJSVal].asInstanceOf[EJSString],
          rhs.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGFunExpr"), ir, lhs, name, JsNumber(fid), a1, a2, a3)) =>
        CFGFunExpr(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          name match {
            case JsNull => None
            case _ => Some(name.convertTo[CFGId])
          },
          cfg.getFunc(fid.toInt) match {
            case Some(f) => f
            case None => throw FunctionNotFoundError("CFGInst", fid.toInt)
          },
          AllocSite.fromJson(a1),
          AllocSite.fromJson(a2),
          a3 match {
            case JsNull => None
            case _ => Some(AllocSite.fromJson(a3))
          }
        )
      case JsArray(Vector(JsString("CFGAssert"), ir, expr, JsBoolean(flag))) =>
        CFGAssert(
          ir.convertTo[IRNode],
          nBlock,
          expr.convertTo[CFGExpr],
          flag
        )
      case JsArray(Vector(JsString("CFGCatch"), ir, name)) =>
        CFGCatch(
          ir.convertTo[IRNode],
          nBlock,
          name.convertTo[CFGId]
        )
      case JsArray(Vector(JsString("CFGReturn"), ir, expr)) =>
        CFGReturn(
          ir.convertTo[IRNode],
          nBlock,
          expr match {
            case JsNull => None
            case _ => Some(expr.convertTo[CFGExpr])
          }
        )
      case JsArray(Vector(JsString("CFGThrow"), ir, expr)) =>
        CFGThrow(
          ir.convertTo[IRNode],
          nBlock,
          expr.convertTo[CFGExpr]
        )
      case JsArray(Vector(JsString("CFGNoOp"), ir, JsString(desc))) =>
        CFGNoOp(
          ir.convertTo[IRNode],
          nBlock,
          desc
        )
      case JsArray(Vector(JsString("CFGInternalCall"), ir, lhs,
        JsString(name), JsArray(args), asite)) =>
        CFGInternalCall(
          ir.convertTo[IRNode],
          nBlock,
          lhs.convertTo[CFGId],
          name,
          args.map(_.convertTo[CFGExpr]).to[List],
          asite match {
            case JsNull => None
            case _ => Some(AllocSite.fromJson(asite))
          }
        )
      case _ => throw CFGInstParseError(value)
    }
  }

  implicit object CFGCallInstJsonFormat extends RootJsonFormat[CFGCallInst] {

    def cBlock: Call = block.asInstanceOf[Call]

    def write(inst: CFGCallInst): JsValue = {
      val name: String = inst.getClass.getSimpleName
      inst match {
        case CFGCall(ir, _, fun, arg, args, asite) => JsArray(
          JsString(name),
          ir.toJson,
          fun.toJson,
          arg.toJson,
          args.toJson,
          asite.toJson
        )
        case CFGConstruct(ir, _, fun, arg, args, asite) => JsArray(
          JsString(name),
          ir.toJson,
          fun.toJson,
          arg.toJson,
          args.toJson,
          asite.toJson
        )
      }
    }

    def read(value: JsValue): CFGCallInst = value match {
      case JsArray(Vector(JsString("CFGCall"), ir, fun, arg, args, asite)) =>
        CFGCall(
          ir.convertTo[IRNode],
          cBlock,
          fun.convertTo[CFGExpr],
          arg.convertTo[CFGExpr],
          args.convertTo[CFGExpr],
          AllocSite.fromJson(asite)
        )
      case JsArray(Vector(JsString("CFGConstruct"), ir, fun, arg, args, asite)) =>
        CFGConstruct(
          ir.convertTo[IRNode],
          cBlock,
          fun.convertTo[CFGExpr],
          arg.convertTo[CFGExpr],
          args.convertTo[CFGExpr],
          AllocSite.fromJson(asite)
        )
      case _ => throw CFGInstParseError(value)
    }
  }
}
