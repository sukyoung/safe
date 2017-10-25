/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

package kr.ac.kaist.safe.web

import spray.json.DefaultJsonProtocol

case class Request(cmd: String)
case class Response(prompt: String, output: String, done: Boolean = false)

object ReqRespJsonFormat extends DefaultJsonProtocol {
  implicit val reqFormat = jsonFormat1(Request)
  implicit val respFormat = jsonFormat3(Response)
}