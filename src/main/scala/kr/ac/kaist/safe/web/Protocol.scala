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

import kr.ac.kaist.safe.json.JsonImplicits._

object Protocol {
  sealed trait Message
  case class Run(cmd: String) extends Message
  case class Result(cmd: String, prompt: String, iter: Int, output: String, state: String, done: Boolean = false) extends Message
}
