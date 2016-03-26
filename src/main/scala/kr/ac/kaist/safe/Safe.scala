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

import kr.ac.kaist.safe.phase.Help
import kr.ac.kaist.safe.config.ArgParse

object Safe {
  ////////////////////////////////////////////////////////////////////////////////
  // Main entry point
  ////////////////////////////////////////////////////////////////////////////////
  def main(tokens: Array[String]): Unit = {
    // Get config and corresponding phase from shell parameters.
    ArgParse(tokens.toList) match {
      case Some((config, phase)) =>
        // Set the start time.
        val startTime = System.currentTimeMillis

        // Execute phase
        phase(config)

        // Print duration time if time option is switch on.
        if (config.time) {
          val duration = System.currentTimeMillis - startTime
          println("Command " + config.command + " took " + duration + "ms.")
        }
      // Print usage message if parsing arguments failed.
      case None => Help.printUsageMessage
    }
  }
}
