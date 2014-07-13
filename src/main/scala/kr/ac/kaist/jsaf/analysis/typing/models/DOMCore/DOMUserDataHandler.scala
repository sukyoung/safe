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
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMUserDataHandler extends DOM {
  private val name = "UserDataHandler"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("NODE_CLONED",   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("NODE_IMPORTED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("NODE_DELETED",  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T)))),
    ("NODE_RENAMED",  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(4), F, T, T)))),
    ("NODE_ADOPTED",  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(5), F, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("handle", AbsBuiltinFunc("DOMUserDataHandler.handle", 5))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // case "DOMUserDataHandler.handle" =>
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // case "DOMUserDataHandler.handle" =>
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      // case "DOMUserDataHandler.handle" =>
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      // case "DOMUserDataHandler.handle" =>
    )
  }

  /* instance */
  //def instantiate() = Unit // not yet implemented
  // intance of DOMUserDataHandler should have no property
}
