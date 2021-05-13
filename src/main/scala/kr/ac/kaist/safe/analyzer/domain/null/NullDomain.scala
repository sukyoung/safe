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

package kr.ac.kaist.safe.analyzer.domain

import spray.json._
import kr.ac.kaist.safe.util.UIdObjMap

// null abstract domain
trait NullDomain extends AbsDomain[Null] {
  // abstract null element
  type Elem <: ElemTrait

  // abstract null element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def StrictEquals(that: Elem): AbsBool
  }

  def fromJSON(json: JsValue)(implicit uomap: UIdObjMap): Elem
}
