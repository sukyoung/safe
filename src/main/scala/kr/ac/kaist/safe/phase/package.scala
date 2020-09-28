/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe

import scala.util.Try
import kr.ac.kaist.safe.util.OptionKind
import kr.ac.kaist.safe.nodes.cfg._

package object phase {
  type Regex = scala.util.matching.Regex
  type ArgRegex[PhaseConfig <: Config] = (Regex, Regex, (PhaseConfig, String) => Try[Unit])
  type PhaseOption[PhaseConfig <: Config] = (String, OptionKind[PhaseConfig], String)
  type CCFG = Map[FunctionId, Map[BlockId, (Map[CFGEdgeType, List[BlockId]], String)]]
  type CFGSpanList = List[List[String]]
}
