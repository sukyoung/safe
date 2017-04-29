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
import kr.ac.kaist.safe.errors.error.{ CFGBlockParseError, LabelKindParseError }

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
        func.getBlock(id.toInt).asInstanceOf[NormalBlock]
      )
      case JsString(name) => UserLabel(name)
      case JsNumber(n) => imap(n.toInt)
      case _ => throw LabelKindParseError(value)
    }
  }

  implicit object CFGBlockJsonFormat extends RootJsonFormat[CFGBlock] {

    def blockToJson(block: CFGBlock): JsValue = {
      JsArray()
    }

    def write(block: CFGBlock): JsValue = block match {
      // do not convert Entry, Exit ExitExc to Json
      // since they always exist 
      case call @ Call(_) => JsArray(
        JsNumber(call.afterCall.id),
        JsNumber(call.afterCatch.id),
        call.callInst.asInstanceOf[CFGInst].toJson
      )
      case AfterCall(_, retVar, _) => JsArray(
        retVar.toJson
      )
      case AfterCatch(_, _) => JsArray()
      case NormalBlock(_, label) => JsArray(
        label.toJson +:
          block.getInsts.map(_.toJson).to[Vector]
      )
      case head @ LoopHead(_) => JsArray(
        JsNumber(head.breakBlock.id),
        JsNumber(head.contBlock.id)
      )
      case ModelBlock(_, _) => JsNull
      case _ => throw CFGBlockParseError(JsNull)
    }

    def read(value: JsValue): CFGBlock = value match {
      case _ => throw CFGBlockParseError(value)
    }
  }
}
