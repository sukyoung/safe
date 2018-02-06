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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import scala.collection.immutable.HashMap

// XXX: we only support object environment record for global object
//      because other object environments with 'with' statments
//      are rewritten by using WithRewriter.

// concrete global environment record type
abstract class GlobalEnvRec extends EnvRec
case object GlobalEnvRec extends GlobalEnvRec
