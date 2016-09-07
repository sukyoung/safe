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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.CFG

// User Function Model
class UserFuncModel(
  override val name: String,
  override val props: List[PropDesc] = Nil,
  val protoProps: List[PropDesc] = Nil,
  override val code: Code = EmptyCode()
) extends FuncModel(name, props, code, Some(code), Some((ObjModel(
  name = s"$name.prototype",
  props = protoProps
), T, F, F)))

object UserFuncModel {
  def apply(
    name: String,
    props: List[PropDesc] = Nil,
    protoProps: List[PropDesc] = Nil,
    code: Code = EmptyCode()
  ): FuncModel = new UserFuncModel(name, props, protoProps, code)
}
