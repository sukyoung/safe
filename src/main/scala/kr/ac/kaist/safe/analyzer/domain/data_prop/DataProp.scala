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

// concrete data property type
case class DataProp(
    value: Value,
    writable: Bool,
    enumerable: Bool,
    configurable: Bool
) {
  def +(other: DataProp): DataProp = {
    // can be several option
    other
  }
  override def toString: String = {
    var w = "F"
    var e = "F"
    var c = "F"
    if (writable) w = "T"
    if (enumerable) e = "T"
    if (configurable) c = "T"
    s"<$value, $w, $e, $c>"
  }
}
