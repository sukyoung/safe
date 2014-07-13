/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib

import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.analysis.typing.ControlPoint

package object graph {
  type ENode = (Node, Kind)
  type GENode = (ControlPoint, Kind)
}
