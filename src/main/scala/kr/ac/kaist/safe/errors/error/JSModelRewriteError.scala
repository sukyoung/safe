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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.util.UserAllocSite

sealed abstract class JSModelRewriteError(msg: String) extends SafeError(msg)

case class UserAllocSiteError(u: UserAllocSite) extends JSModelRewriteError({
  s"[UserAllocSiteError]: $u."
})
