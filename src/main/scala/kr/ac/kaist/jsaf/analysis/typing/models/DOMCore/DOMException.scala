/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMException extends DOM {
  private val name = "DOMException"

  // ExceptionCode
  val INDEX_SIZE_ERR = 1
  val DOMSTRING_SIZE_ERR = 2
  val HIERARCHY_REQUEST_ERR = 3
  val WRONG_DOCUMENT_ERR = 4
  val INVALID_CHARACTER_ERR = 5
  val NO_DATA_ALLOWED_ERR = 6
  val NO_MODIFICATION_ALLOWED_ERR = 7
  val NOT_FOUND_ERR = 8
  val NOT_SUPPORTED_ERR = 9
  val INUSE_ATTRIBUTE_ERR = 10
  val INVALID_STATE_ERR = 11
  val SYNTAX_ERR = 12
  val INVALID_MODIFICATION = 13
  val NAMESPACE_ERR = 14
  val INVALID_ACCESS_ERR = 15
  val VALIDATION_ERR = 16
  val TYPE_MISMATCH_ERR = 17

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* DOM error instance */
  val DOMErrIndexSize             = newSystemLoc("DOMErrIndexSize", Old)
  val DOMErrDomstringSize         = newSystemLoc("DOMErrDomstringSize", Old)
  val DOMErrHierarchyRequest      = newSystemLoc("DOMErrHierarchyRequest", Old)
  val DOMErrWrongDocument         = newSystemLoc("DOMErrWrongDocument", Old)
  val DOMErrInvalidCharacter      = newSystemLoc("DOMErrInvalidCharacter", Old)
  val DOMErrNoDataAllowed         = newSystemLoc("DOMErrNoDataAllowed", Old)
  val DOMErrNoModificationAllowed = newSystemLoc("DOMErrNoModificationAllowed", Old)
  val DOMErrNotFound              = newSystemLoc("DOMErrNotFound", Old)
  val DOMErrNotSupported          = newSystemLoc("DOMErrNotSupported", Old)
  val DOMErrInuseAttribute        = newSystemLoc("DOMErrInuseAttribute", Old)
  val DOMErrInvalidState          = newSystemLoc("DOMErrInvalidState", Old)
  val DOMErrSyntax                = newSystemLoc("DOMErrSyntax", Old)
  val DOMErrInvalidModification   = newSystemLoc("DOMErrInvalidModification", Old)
  val DOMErrNamespace             = newSystemLoc("DOMErrNamespace", Old)
  val DOMErrInvalidAccess         = newSystemLoc("DOMErrInvalidAccess", Old)
  val DOMErrValidation            = newSystemLoc("DOMErrValidation", Old)
  val DOMErrTypeMismatch          = newSystemLoc("DOMErrTypeMismatch", Old)

  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    // DOM Level 1
    ("INDEX_SIZE_ERR",              AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("DOMSTRING_SIZE_ERR",          AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("HIERARCHY_REQUEST_ERR",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T)))),
    ("WRONG_DOCUMENT_ERR",          AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(4), F, T, T)))),
    ("INVALID_CHARACTER_ERR",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(5), F, T, T)))),
    ("NO_DATA_ALLOWED_ERR",         AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(6), F, T, T)))),
    ("NO_MODIFICATION_ALLOWED_ERR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(7), F, T, T)))),
    ("NOT_FOUND_ERR",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(8), F, T, T)))),
    ("NOT_SUPPORTED_ERR",           AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(9), F, T, T)))),
    ("INUSE_ATTRIBUTE_ERR",         AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(10), F, T, T)))),
    // DOM Level 2
    ("INVALID_STATE_ERR",           AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(11), F, T, T)))),
    ("SYNTAX_ERR",                  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(12), F, T, T)))),
    ("INVALID_MODIFICATION_ERR",    AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(13), F, T, T)))),
    ("NAMESPACE_ERR",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(14), F, T, T)))),
    ("INVALID_ACCESS_ERR",          AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(15), F, T, T)))),
    ("VALIDATION_ERR ",             AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(16), F, T, T)))),
    ("TYPE_MISMATCH_ERR",           AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(17), F, T, T))))
    )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )


  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  ) ++ insLocPropList

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }

  /* semantics */
  // no function


  /* instance */
  private def insLocPropList: List[(Loc, List[(String, AbsProperty)])] = {
    val list_loc_prop = List(
      (DOMErrIndexSize, INDEX_SIZE_ERR),
      (DOMErrDomstringSize, DOMSTRING_SIZE_ERR),
      (DOMErrHierarchyRequest, HIERARCHY_REQUEST_ERR),
      (DOMErrWrongDocument, WRONG_DOCUMENT_ERR),
      (DOMErrInvalidCharacter, INVALID_CHARACTER_ERR),
      (DOMErrNoDataAllowed, NO_DATA_ALLOWED_ERR),
      (DOMErrNoModificationAllowed, NO_MODIFICATION_ALLOWED_ERR),
      (DOMErrNotFound, NOT_FOUND_ERR),
      (DOMErrNotSupported, NOT_SUPPORTED_ERR),
      (DOMErrInuseAttribute, INUSE_ATTRIBUTE_ERR),
      (DOMErrInvalidState, INVALID_STATE_ERR),
      (DOMErrSyntax, SYNTAX_ERR),
      (DOMErrInvalidModification, INVALID_MODIFICATION),
      (DOMErrNamespace, NAMESPACE_ERR),
      (DOMErrInvalidAccess, INVALID_ACCESS_ERR),
      (DOMErrValidation, VALIDATION_ERR),
      (DOMErrTypeMismatch, TYPE_MISMATCH_ERR)
    )
    list_loc_prop.foldLeft[List[(Loc, List[(String, AbsProperty)])]](List())((l, lp) =>
      l :+ ((lp._1, getInsList(lp._2)))
    )
  }
  /* list of properties in the instance object */
  def getInsList(code: Int): List[(String, AbsProperty)] = List (
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("code",   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(code), T, T, T))))
  )
}
