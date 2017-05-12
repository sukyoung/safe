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

package kr.ac.kaist.safe.json

import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.json.CFGExprProtocol._
import kr.ac.kaist.safe.json.CFGInstProtocol._
import kr.ac.kaist.safe.errors.error.{
  CFGBlockParseError,
  ModelBlockToJsonError,
  LabelKindParseError
}

import spray.json._
import DefaultJsonProtocol._

object CFGBlockProtocol extends DefaultJsonProtocol {

  var func: CFGFunction = _

  implicit object LabelKindJsonFormat extends RootJsonFormat[LabelKind] {
    val map: Map[LabelKind, Int] = Map(
      NoLabel -> 0,
      LoopBreakLabel -> 1,
      LoopContLabel -> 2,
      BranchLabel -> 3,
      SwitchLabel -> 4,
      CaseLabel -> 5,
      DefaultLabel -> 6,
      TryLabel -> 7,
      CatchLabel -> 8
    )
    val imap: Map[Int, LabelKind] = map.map(_.swap)

    def write(kind: LabelKind): JsValue = kind match {
      case FinallyLabel(block) => JsArray(JsNumber(block.id))
      case UserLabel(name) => JsString(name)
      case _ => JsNumber(map(kind))
    }

    def read(value: JsValue): LabelKind = value match {
      case JsArray(Vector(JsNumber(id))) => FinallyLabel(
        func.getBlock(id.toInt) match {
          case Some(block) => block.asInstanceOf[NormalBlock]
          case None => throw LabelKindParseError(value)
        }
      )
      case JsString(name) => UserLabel(name)
      case JsNumber(n) => imap(n.toInt)
      case _ => throw LabelKindParseError(value)
    }
  }

  implicit object CFGBlockJsonFormat extends RootJsonFormat[CFGBlock] {

    def write(block: CFGBlock): JsValue = block match {
      case AfterCall(_, retVar, call) => JsArray(
        JsString("Call"),
        call.callInst.toJson,
        retVar.toJson
      )
      case NormalBlock(_, label) => JsArray(
        JsString("Normal"),
        label.toJson,
        JsArray(block.getInsts.reverse.map(_.asInstanceOf[CFGNormalInst].toJson).to[Vector])
      )
      case head @ LoopHead(_) => JsArray(
        JsNumber(head.breakBlock.id),
        JsNumber(head.contBlock.id)
      )
      case ModelBlock(_, _) => throw ModelBlockToJsonError
      case _ => JsNull
    }

    def read(value: JsValue): CFGBlock = throw CFGBlockParseError(value)

    def restoreBlock(func: CFGFunction, block: JsValue): CFGBlock = block match {
      case JsArray(Vector(JsString("Call"), callInst, retVar)) => {
        func.createCall(
          c => {
            CFGInstProtocol.block = c
            callInst.convertTo[CFGCallInst]
          },
          retVar.convertTo[CFGId]
        )
      }
      case JsArray(Vector(JsString("Normal"), label, JsArray(insts))) => {
        val b: NormalBlock = func.createBlock(label.convertTo[LabelKind])
        CFGInstProtocol.block = b
        for (inst <- insts)
          b.createInst(n => inst.convertTo[CFGNormalInst])
        b
      }
      case JsArray(Vector(JsNumber(breakBlock), JsNumber(contBlock))) => {
        val h: LoopHead = func.createLoopHead
        h.breakBlockId = breakBlock.toInt
        h.contBlockId = contBlock.toInt
        h
      }
      case _ => throw CFGBlockParseError(block)
    }
  }
}
