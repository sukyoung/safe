/**
 * ****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 * *
 * Use is subject to license terms.
 * *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import java.io.File

import kr.ac.kaist.safe
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.clone_detector.CloneDetector
import kr.ac.kaist.safe.clone_detector.util.{ CloneConfiguration, Util }
import kr.ac.kaist.safe.util.BoolOption

import scala.sys.process._
import scala.util.{ Failure, Success, Try }

////////////////////////////////////////////////////////////////////////////////
// Clone Detector
////////////////////////////////////////////////////////////////////////////////
case object CloneDetect extends PhaseObj[Unit, CloneDetectConfig, Int] {
  /**
   * Reports detected clones in the file.
   */

  val name: String = "cloneDetector"
  val help: String = "Detect clones in JavaScript source files."

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    detectConfig: CloneDetectConfig
  ): Try[Int] = {
    val SEP = File.separator
    val base = safe.BASE_DIR
    val jscdConfig = List(safe.BASE_DIR, "bin", "jscd_config").mkString(SEP)

    System.out.print("\n==== Configuration checking...")
    var exitCode = List(base, "bin", "jscd_configure").mkString(SEP) !

    if (exitCode == 0) {
      System.out.print("Done.\n\n")
      System.out.println("==== Start clone detection ====\n")

      val file = new File(jscdConfig)
      if (!file.exists) {
        Failure(new Exception("Error: no config file in the current directory"))
      } else {
        val config = new CloneConfiguration(file)
        Util.funClones = detectConfig.function

        System.out.println("Vector generation...\n")
        exitCode = CloneDetector.doit(config)
        if (exitCode != 0) {
          Failure(new Exception("Error: problem in vec generator step. Stop and check logs in " + config.getTimeDirectory))
        } else {
          System.out.println("Vector generation done. Logs in " + config.getTimeDirectory + SEP + "vgen_*")
          System.out.println("Vector files in " + config.getVectorDirectory + SEP + "vdb_*\n")

          exitCode = List(base, "bin", "cluster.sh").mkString(SEP) !

          if (detectConfig.xml) {
            val out = List(base, "bin", "jscd_out2xml.sh").mkString(SEP) !

            if (out == 0) Success(out)
            else Failure(new Exception("Failed to convert clone detection results to XML"))
          } else if (exitCode == 0) Success(exitCode)
          else Failure(new Exception("Failed to detect clones"))
        }
      }
    } else if (exitCode == 0) Success(exitCode)
    else Failure(new Exception("Failed to configure clone detector"))
  }

  override def defaultConfig: CloneDetectConfig = CloneDetectConfig()

  override val options: List[PhaseOption[CloneDetectConfig]] = List(
    ("xml", BoolOption(c => c.xml = true),
      "the clone detection results will be converted to XML format."),
    ("function", BoolOption(c => c.function = true),
      "only function clones will be detected.")
  )
}

// CloneDetect phase config
case class CloneDetectConfig(
  var xml: Boolean = false,
  var function: Boolean = false
) extends Config
