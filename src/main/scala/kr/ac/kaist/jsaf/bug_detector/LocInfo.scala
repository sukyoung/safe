/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.{HashSet => MHashSet}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._

class LocInfo(loc: Loc) {
  var createdInst: CFGInst                      = null
  val containingVar                             = new MHashSet[(CFGInst, BugVar0)]()
}
