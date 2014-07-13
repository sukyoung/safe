/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.typing.domain.PropValue

sealed abstract class AbsProperty
case class AbsBuiltinFunc(id: String, length: Double) extends AbsProperty
case class AbsBuiltinFuncAftercall(id: String, length: Double) extends AbsProperty
case class AbsBuiltinFuncAftercallOptional(id: String, length: Double) extends AbsProperty
case class AbsBuiltinFuncCallback(id: String, length: Double) extends AbsProperty
case class AbsInternalFunc(id: String) extends AbsProperty
case class AbsInternalFuncAftercallOptional(id: String) extends AbsProperty
case class AbsConstValue(v: PropValue) extends AbsProperty
