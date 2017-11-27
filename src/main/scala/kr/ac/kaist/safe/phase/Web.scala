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

package kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.web.WebServer

import scala.util.{Success, Try}

// Help phase
case object Web extends PhaseObj[Unit, WebConfig, Unit] {
  val name = "Web Server"
  val help = "Run SAFE web for interactive step-by-step analysis"

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: WebConfig
  ): Try[Unit] = {
    // interactive analysis using web server
    WebServer.run(config.port)
    Success("Done")
  }
  def defaultConfig: WebConfig = WebConfig()
  val options: List[PhaseOption[WebConfig]] = List()
}

case class WebConfig(var port: Int = 8080) extends Config
