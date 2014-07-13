/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMEvent

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMDocument
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

object DocumentEvent extends DOM {
  
  /* predefined locatoins */
  val loc_proto = ObjProtoLoc
  // no locations

  /* prorotype */
  private val prop_document_proto: List[(String, AbsProperty)] = List(
    ("createEvent", AbsBuiltinFunc("DocumentEvent.createEvent", 1))
  )

  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (DOMDocument.loc_proto, prop_document_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "DocumentEvent.createEvent"
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "DocumentEvent.createEvent"
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "DocumentEvent.createEvent"
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "DocumentEvent.createEvent"
    )
  }

  /* instance */
  //def instantiate() = Unit // not yet implemented
  // intance of DocumentEvent should have no property.
}
