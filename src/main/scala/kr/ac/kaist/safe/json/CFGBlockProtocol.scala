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

import spray.json._
import DefaultJsonProtocol._

object CFGBlockProtocol extends DefaultJsonProtocol {

  // TODO implement CFGBlock <-> Json
  implicit object CFGBlockJsonFormat extends RootJsonFormat[CFGBlock] {

    def write(block: CFGBlock): JsValue = JsNull

    def read(value: JsValue): CFGBlock = null
  }
}
