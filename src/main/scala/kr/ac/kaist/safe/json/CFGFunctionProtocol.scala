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

import scala.collection.mutable.{ MutableList => MList }
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.json.NodeProtocol._
import kr.ac.kaist.safe.json.CFGExprProtocol._
import kr.ac.kaist.safe.json.CFGBlockProtocol._
import kr.ac.kaist.safe.errors.error.{ CFGFunctionParseError, BlockNotFoundError }

import spray.json._
import DefaultJsonProtocol._

object CFGFunctionProtocol extends DefaultJsonProtocol {

  implicit object CFGFunctionJsonFormat extends RootJsonFormat[CFGFunction] {

    def write(func: CFGFunction): JsValue = func match {
      case CFGFunction(ir, argsName, argVars, localVars, name, isUser) => JsArray(
        ir.toJson,
        JsString(argsName),
        JsArray(argVars.map(_.toJson).to[Vector]),
        JsArray(localVars.map(_.toJson).to[Vector]),
        JsString(name),
        JsBoolean(isUser),
        JsArray(func.getAllBlocks.reverse.drop(3).map(_.toJson).filter(_ != JsNull).to[Vector]),
        JsArray(func.getCaptured.reverse.map(_.toJson).to[Vector])
      )
    }

    def read(value: JsValue): CFGFunction = value match {
      case JsArray(Vector(
        ir,
        JsString(argsName),
        JsArray(argVars),
        JsArray(localVars),
        JsString(name),
        JsBoolean(isUser),
        JsArray(blocks),
        JsArray(captured)
        )) => {
        val func = CFGFunction(
          ir.convertTo[IRNode],
          argsName,
          argVars.map(_.convertTo[CFGId]).to[List],
          localVars.map(_.convertTo[CFGId]).to[List],
          name,
          isUser
        )
        for (captId <- captured)
          func.addCaptured(captId.convertTo[CFGId])
        func.blockData = blocks
        func
      }
      case _ => throw CFGFunctionParseError(value)
    }
  }

  def restoreGlobalFunc(func: CFGFunction, value: JsValue): Unit = value match {
    case JsArray(Vector(_, _, _, _, _, _, JsArray(blocks), JsArray(captured))) => {
      for (captId <- captured)
        func.addCaptured(captId.convertTo[CFGId])
      func.blockData = blocks
    }
    case _ => throw CFGFunctionParseError(value)
  }

  def restoreBlock(func: CFGFunction): Unit = {
    CFGBlockProtocol.func = func
    for (block <- func.blockData)
      CFGBlockJsonFormat.restoreBlock(func, block)
    for (block <- func.getAllBlocks)
      block match {
        case head @ LoopHead(_) => {
          func.getBlock(head.breakBlockId) match {
            case Some(b) => head.breakBlock = b.asInstanceOf[NormalBlock]
            case None => throw BlockNotFoundError("CFGFunction", func.id, head.breakBlockId)
          }
          func.getBlock(head.contBlockId) match {
            case Some(b) => head.contBlock = b.asInstanceOf[NormalBlock]
            case None => throw BlockNotFoundError("CFGFunction", func.id, head.contBlockId)
          }
        }
        case _ => 0
      }
  }
}
