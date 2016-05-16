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

import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.phase.Help
import kr.ac.kaist.safe.config.ArgParse

object Safe {
  ////////////////////////////////////////////////////////////////////////////////
  // Main entry point
  ////////////////////////////////////////////////////////////////////////////////
  def main(tokens: Array[String]): Unit = {
    // Get the config and its corresponding phase from the shell parameters.
    ArgParse(tokens.toList) match {
      case Success((config, phase)) =>
        // Set the start time.
        val startTime = System.currentTimeMillis

        // Execute the phase.
        phase(config)

        // Print the time spent if the time option is set.
        if (config.time) {
          val duration = System.currentTimeMillis - startTime
          println("Command " + config.command + " took " + duration + "ms.")
        }
      // Print the usage message if parsing arguments failed.
      case Failure(ex) =>
        Console.err.print(ex.toString)
        Help.printUsageMessage
    }
  }
}
