/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes_util.JSFromUrl
import kr.ac.kaist.jsaf.Shell

////////////////////////////////////////////////////////////////////////////////
// JavaScript from URL
////////////////////////////////////////////////////////////////////////////////
object URLMain {
  /**
   * Extracts JavaScript source code from a url, if any.
   * If -out file is given, the extracted source code will be written to the file.
   */
  def url: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The url command needs a url of html page.")

    new JSFromUrl(Shell.params.FileNames(0), Shell.params.opt_OutFileName).doit
    0
  }
}
