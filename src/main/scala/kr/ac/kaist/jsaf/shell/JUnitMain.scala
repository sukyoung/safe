/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import scala.collection.JavaConversions
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.tests.FileTests

////////////////////////////////////////////////////////////////////////////////
// junit Test
////////////////////////////////////////////////////////////////////////////////
object JUnitMain {
  def junit: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to run junit tests.")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    _root_.junit.textui.TestRunner.run(FileTests.suiteFromListOfFiles(fileNames, "", "", "", true, false))
    0
  }
}
