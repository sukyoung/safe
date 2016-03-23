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

package kr.ac.kaist.safe.proc

sealed abstract class OptionKind
case class BoolOption(assign: () => Unit) extends OptionKind
case class NumOption(assign: Int => Unit) extends OptionKind
case class StrOption(assign: String => Unit) extends OptionKind

trait ConfigOption {
  val prefix: String
  val optMap: Map[String, OptionKind]
  def getOptMap: Option[OptRegexMap] = {
    def error(msg: String): None.type = {
      Console.err.println(msg)
      None
    }
    optMap.foldLeft[Option[OptRegexMap]](Some(Map())) {
      case (Some(map), (opt, kind)) if !map.contains(opt) =>
        val name = prefix + opt
        Some(map + (name -> (kind match {
          case BoolOption(ass) => List(
            (("-" + name).r, "".r, (_: String) => Some(ass())),
            (("-" + name + "=").r, ".*".r, (_: String) =>
              error("The option '-" + name + "' not allowed assignment."))
          )
          case NumOption(ass) => List(
            (("-" + name + "=").r, "[0-9]+".r, (s: String) => Some(ass(s.toInt))),
            (("-" + name + "=").r, ".*".r, (_: String) => error("The option '" + name + "' needs number assignment.")),
            (("-" + name).r, "".r, (_: String) => error("The option '" + name + "' needs number assignment."))
          )
          case StrOption(ass) => List(
            (("-" + name + "=").r, ".+".r, (s: String) => Some(ass(s))),
            (("-" + name + "=").r, ".*".r, (_: String) => error("The option '" + name + "' needs string assignment.")),
            (("-" + name).r, "".r, (_: String) => error("The option '" + name + "' needs string assignment."))
          )
          // TODO List type, etc.
        })))
      case _ => None
    }
  }
}
