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

package kr.ac.kaist.safe.web

import akka.actor.ActorRef
import kr.ac.kaist.safe.analyzer.Fixpoint

sealed trait Event
case class ReceivedCmd(uid: String, cmd: String) extends Event
case class ParticipantLeft(uid: String) extends Event
case class NewParticipant(uid: String, subscriber: ActorRef) extends Event
case class UpdateFixpoint(uid: String, f: Fixpoint) extends Event