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
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.analyzer.Initialize
import kr.ac.kaist.safe.json.NodeProtocol._
import kr.ac.kaist.safe.json.CFGExprProtocol._
import kr.ac.kaist.safe.json.CFGInstProtocol._
import kr.ac.kaist.safe.json.CFGFunctionProtocol._
import kr.ac.kaist.safe.errors.error.{
  CFGParseError,
  CFGEdgeParseError,
  BlockNotFoundError
}

import spray.json._
import DefaultJsonProtocol._

object CFGProtocol extends DefaultJsonProtocol {

  var jsModel = false

  implicit object CFGJsonFormat extends RootJsonFormat[CFG] {

    private val map: Map[CFGEdgeType, Int] = Map(
      CFGEdgeNormal -> 0,
      CFGEdgeExc -> 1,
      CFGEdgeCall -> 2,
      CFGEdgeRet -> 3
    )
    private val imap: Map[Int, CFGEdgeType] = map.map(_.swap)

    private def edgeToJson(
      edges: Map[CFGEdgeType, List[CFGBlock]]
    ): JsValue = JsArray(
      edges.to[Vector].map(_ match {
        case (edgeType, blocks) => JsArray(
          JsNumber(map(edgeType)),
          JsArray(blocks.reverse.map(block => JsArray(
            JsNumber(block.func.id),
            JsNumber(block.id)
          )).to[Vector])
        )
      })
    )
    private def blockEdgeToJson(block: CFGBlock): JsValue = JsArray(
      JsNumber(block.func.id),
      JsNumber(block.id),
      edgeToJson(block.getAllSucc),
      edgeToJson(block.getAllPred)
    )

    private def jsonToEdge(
      cfg: CFG,
      block: CFGBlock,
      edgeType: CFGEdgeType,
      blocks: Vector[JsValue],
      succ: Boolean
    ): Unit = {
      for (b <- blocks)
        b match {
          case JsArray(Vector(JsNumber(fid), JsNumber(bid))) =>
            cfg.getBlock(fid.toInt, bid.toInt) match {
              case Some(edgeBlock) =>
                if (succ)
                  block.addSucc(edgeType, edgeBlock)
                else
                  block.addPred(edgeType, edgeBlock)
              case None => throw BlockNotFoundError("CFG", fid.toInt, bid.toInt)
            }
          case _ => throw CFGEdgeParseError(b)
        }
    }
    private def jsonToBlockEdge(
      cfg: CFG,
      fid: FunctionId,
      bid: BlockId,
      succ: Vector[JsValue],
      pred: Vector[JsValue]
    ): Unit = {
      val succAndPred: Map[Boolean, Vector[JsValue]] = Map(
        true -> succ,
        false -> pred
      )
      cfg.getBlock(fid, bid) match {
        case Some(block) =>
          for ((isSucc, edges) <- succAndPred)
            for (edge <- edges)
              edge match {
                case JsArray(Vector(JsNumber(edgeType), JsArray(blocks))) =>
                  jsonToEdge(cfg, block, imap(edgeType.toInt), blocks, isSucc)
                case _ => throw CFGEdgeParseError(edge)
              }
        case None => throw BlockNotFoundError("CFG", fid, bid)
      }
    }

    def write(cfg: CFG): JsValue = {
      val info: ASTNodeInfo = ASTNodeInfo(cfg.span, cfg.comment)
      val ast: ASTNode = Comment(info, "")
      val ir: IRNode = IRRoot(ast, Nil, Nil, Nil)

      JsArray(
        ir.toJson,
        JsArray(cfg.globalFunc.localVars.map(_.toJson).to[Vector]),
        JsArray(cfg.getAllFuncs.reverse.filter(_.isUser).map(_.toJson).to[Vector]),
        JsNumber(cfg.getUserASiteSize),
        // store edge information for whole blocks
        JsArray(cfg.getAllBlocks.map(blockEdgeToJson(_)).to[Vector])
      )
    }

    def read(value: JsValue): CFG = value match {
      case JsArray(Vector(
        ir,
        JsArray(vars),
        JsArray(funcs),
        JsNumber(user),
        JsArray(edges)
        )) => {
        val cfg: CFG = new CFG(ir.convertTo[IRNode], vars.map(_.convertTo[CFGId]).to[List])
        CFGInstProtocol.cfg = cfg
        CFGFunctionProtocol.restoreGlobalFunc(cfg.globalFunc, funcs(0))
        for (func <- funcs.tail)
          cfg.addFunction(func.convertTo[CFGFunction])
        for (func <- cfg.getAllFuncs)
          CFGFunctionProtocol.restoreBlock(func)
        cfg.setUserASiteSize(user.toInt)
        Initialize(cfg, jsModel)
        for (edge <- edges)
          edge match {
            case JsArray(Vector(
              JsNumber(fid),
              JsNumber(bid),
              JsArray(succ),
              JsArray(pred)
              )) => jsonToBlockEdge(cfg, fid.toInt, bid.toInt, succ, pred)
            case _ => throw CFGParseError(value)
          }
        cfg
      }
      case _ => throw CFGParseError(value)
    }
  }
}
