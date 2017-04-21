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
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.json.NodeProtocol._
import kr.ac.kaist.safe.json.CFGInstProtocol._
import kr.ac.kaist.safe.json.CFGFunctionProtocol._

import spray.json._
import DefaultJsonProtocol._

object CFGProtocol extends DefaultJsonProtocol {

  implicit object AllocSiteJsonFormat extends RootJsonFormat[AllocSite] {

    def write(aSite: AllocSite): JsValue = aSite match {
      case UserAllocSite(id) => JsNumber(id)
      case PredAllocSite(name) => JsString(name)
    }

    def read(value: JsValue): AllocSite = value match {
      case JsNumber(id) => UserAllocSite(id.toInt)
      case JsString(name) => PredAllocSite(name)
    }
  }

  implicit object CFGJsonFormat extends RootJsonFormat[CFG] {

    def write(cfg: CFG): JsValue = {
      val info: ASTNodeInfo = ASTNodeInfo(cfg.span, cfg.comment)
      val ast: ASTNode = Comment(info, "")
      val ir: IRNode = IRRoot(ast, Nil, Nil, Nil)

      JsArray(
        ir.toJson,
        JsArray(cfg.globalFunc.localVars.map(_.toJson).to[Vector]),
        // store except the first func (globalFunc)
        JsArray(cfg.getAllFuncs.tail.map(_.toJson).to[Vector]),
        JsNumber(cfg.getUserASiteSize),
        JsArray(cfg.getPredASiteSet.map(_.toJson).to[Vector])
      )
    }

    def read(value: JsValue): CFG = value match {
      case JsArray(Vector(
        ir,
        JsArray(vars),
        JsArray(funcs),
        JsNumber(user),
        JsArray(pred))
        ) => {
        val cfg: CFG = new CFG(ir.convertTo[IRNode], vars.map(_.convertTo[CFGId]).to[List])
        for (func <- funcs)
          cfg.addFunction(func.convertTo[CFGFunction])
        cfg.setUserASiteSize(user.toInt)
        for (aSite <- pred)
          cfg.registerPredASite(aSite.convertTo[AllocSite])
        cfg
      }
    }
  }
}
