/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import kr.ac.kaist.jsaf.clone_detector.CloneDetector
import kr.ac.kaist.jsaf.ProjectProperties
import java.io.File
import scala.sys.process.Process
import scala.sys.process.ProcessIO

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
    val pb = Process(base + "bin/jscd.sh")
    val pio = new ProcessIO(_ => (),
                        stdout => scala.io.Source.fromInputStream(stdout)
                          .getLines.foreach(println),
                        _ => ())
    val p = pb.run(pio)
    p.exitValue
  }
}
