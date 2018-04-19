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

package kr.ac.kaist.safe.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.util.ArgParser

sealed abstract class PhaseList[Result] {
  def getRunner(
    parser: ArgParser
  ): Try[SafeConfig => Try[Result]]

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = PhaseCons(this, phase)

  val nameList: List[String]
  override def toString: String = nameList.reverse.mkString(" >> ")
}

case object PhaseNil extends PhaseList[Unit] {
  def getRunner(
    parser: ArgParser
  ): Try[SafeConfig => Try[Unit]] = Success(_ => Success(()))

  val nameList: List[String] = Nil
}

case class PhaseCons[P, C <: Config, R](
    prev: PhaseList[P],
    phase: PhaseObj[P, C, R]
) extends PhaseList[R] {
  def getRunner(
    parser: ArgParser
  ): Try[SafeConfig => Try[R]] = {
    prev.getRunner(parser).flatMap {
      case prevRunner => phase.getRunner(parser).map {
        case phaseRunner => safeConfig => {
          prevRunner(safeConfig).flatMap {
            case input => phaseRunner(input, safeConfig)
          }
        }
      }
    }
  }

  val nameList: List[String] = phase.name :: prev.nameList
}

