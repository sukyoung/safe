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

package kr.ac.kaist.safe.errors.warning

import kr.ac.kaist.safe.errors.SafeException

abstract class SafeWarning(msg: String) extends SafeException(msg)
