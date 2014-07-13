/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.nodes_util

import java.lang.Double
import java.lang.String
import java.math.BigInteger
import java.io.Writer
import java.util.Collections
import java.util.List
import java.util.Map
import java.util.ArrayList
import java.util.LinkedList
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.useful._
import edu.rice.cs.plt.tuple.Option

class SpanInfo(span: Span) extends UIDObject {
  def getSpan() = span
}
