/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

import _root_.java.lang.{Integer => JInteger}
import xtc.parser.ParseError
import xtc.parser.ParserBase
import xtc.tree.Location

class ParserError(parseError: ParseError, parser: ParserBase, start: JInteger)
      extends StaticError(parseError.msg, None) {

  def typeDescription() = "Parse Error"

  override def description() = {
    var result = parseError.msg
    val size = result.length
    if (size > 8 && result.substring(size-8,size).equals("expected"))
      result = "Syntax Error"
    else {
      if (!result.equals("")) result = "Syntax Error: " + result
      else result = "Syntax Error"
    }
    if (result.equals("")) result = "Unspecified cause"
    result
  }

  override def at =
    if (parseError.index == -1) "Unspecified location"
    else {
      val loc = parser.location(parseError.index)
      new Location(loc.file, loc.line + start, loc.column).toString
    }
  override def toString = String.format("%s:\n    %s", at, description)
}
