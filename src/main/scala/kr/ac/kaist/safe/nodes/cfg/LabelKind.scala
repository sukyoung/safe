/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes.cfg

// label kind
sealed abstract class LabelKind(str: String) {
  override def toString: String = str
  def isOutLabel: Boolean = this match {
    case (
      LoopBreakLabel | UserLabel(_) // break
      | LoopContLabel // continue
      | CatchLabel // catch
      ) => true
    case _ => false
  }
}
case object NoLabel extends LabelKind("Block")
case object LoopBreakLabel extends LabelKind("LBreak")
case object LoopContLabel extends LabelKind("LCont")
case object BranchLabel extends LabelKind("Branch")
case object SwitchLabel extends LabelKind("Switch")
case object CaseLabel extends LabelKind("Case")
case object DefaultLabel extends LabelKind("Default")
case object TryLabel extends LabelKind("Try")
case object FinallyLabel extends LabelKind("Finally")
case object CatchLabel extends LabelKind("Catch")
case class UserLabel(name: String) extends LabelKind(s"Label($name)")
