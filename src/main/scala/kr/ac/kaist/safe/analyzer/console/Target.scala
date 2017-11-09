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

package kr.ac.kaist.safe.analyzer.console

import kr.ac.kaist.safe.nodes.cfg.CFGBlock

sealed abstract class Target
case class TargetIter(iter: Int) extends Target
case class TargetBlock(block: CFGBlock) extends Target
case object TargetStart extends Target
case object NoTarget extends Target
