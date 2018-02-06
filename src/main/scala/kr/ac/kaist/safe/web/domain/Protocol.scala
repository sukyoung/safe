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

package kr.ac.kaist.safe.web.domain

import kr.ac.kaist.safe.analyzer.domain.AbsState

object Protocol {
  sealed trait Message
  case class Base(action: String) extends Message

  case class Run(cmd: String) extends Message
  case class Result(action: String, cmd: String, prompt: String, iter: Int, output: String, state: String, done: Boolean = false) extends Message

  case class InitialState(action: String = Actions.Initial) extends Message

  case class FetchBlockState(bid: String) extends Message
  case class BlockState(action: String, bid: String, insts: String, state: String) extends Message

  case class RunInst(bid: String, iid: String) extends Message
  case class InstState(action: String, bid: String, iid: String, state: String) extends Message

  case class FileUploadResp(status: String, reason: String = "") extends Message
}
