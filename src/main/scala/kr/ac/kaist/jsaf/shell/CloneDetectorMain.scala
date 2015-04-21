/******************************************************************************
    Copyright (c) 2012-2015, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.File

import scala.sys.process.Process
import scala.sys.process.ProcessIO

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.clone_detector.CloneDetector
import kr.ac.kaist.jsaf.clone_detector.util.CloneConfiguration

////////////////////////////////////////////////////////////////////////////////
// Clone Detector
////////////////////////////////////////////////////////////////////////////////
object CloneDetectorMain {
  /**
   * Reports detected clones in the file.
   */
  def cloneDetector: Int = {
    val SEP = File.separator
    val base = ProjectProperties.BASEDIR + SEP
    val jscd_config = ProjectProperties.BASEDIR + SEP + "bin" + SEP + "jscd_config"

    System.out.print("\n==== Configuration checking...")
    var exitCode = runScript(base, "bin" + SEP + "jscd_configure").exitValue
    if (exitCode == 0) {
      System.out.print("Done.\n\n")
      System.out.println("==== Start clone detection ====\n")
      
      val file = new File(jscd_config)
      if (!file.exists) {
        System.err.println("Error: no config file in the current directory")
        1
      } else {
        val config = new CloneConfiguration(file)
        System.out.println("Vector generation...\n")

        exitCode = CloneDetector.doit(config)
        if (exitCode != 0) {
          System.out.println("Error: problem in vec generator step. Stop and check logs in " + config.getTimeDirectory)
          1
        } else {
          System.out.println("Vector generation done. Logs in " + config.getTimeDirectory + SEP + "vgen_*")
          System.out.println("Vector files in " + config.getVectorDirectory + SEP + "vdb_*\n")

          val main = runScript(base, "bin" + SEP + "cluster.sh")
          exitCode = main.exitValue
          if (Shell.params.opt_XML) {
            val out = runScript(base, "bin" + SEP + "jscd_out2xml.sh")
            out.exitValue
          } else
            exitCode
        }
      }
    } else
      exitCode
  }

  private def runScript(base: String, script: String): Process = {
    val pb = Process(base + script)
    val pio = new ProcessIO(_ => (),
      stdout => scala.io.Source.fromInputStream(stdout)
        .getLines.foreach(println),
      _ => ())
    val p = pb.run(pio)
    p
  }
}
