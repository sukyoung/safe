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

package kr.ac.kaist.safe

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.util.NodeUtil._
import scala.collection.immutable.HashMap

package object analyzer {
  // internal API value
  lazy val internalValueMap: Map[String, AbsValue] = HashMap(
    INTERNAL_TOP -> AbsValue.Top,
    INTERNAL_UINT -> AbsNumber.UInt
  )
}
