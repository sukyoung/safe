/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE, PreSemanticsExpr => PSE, AccessHelper => AH, _}
import org.w3c.dom.Node
import kr.ac.kaist.jsaf.nodes_util.IRFactory

trait DOM extends ModelData {
  /* legacy code */
  //def getCons(): Option[(String,Loc)] = None
  val loc_proto: Loc 
  def getProto(): Option[Loc] = None
  // Returns a new location for the instance object of a DOM object 
  def getInstance(cfg: CFG): Option[Loc] = None
  // Returns a list of properties in the instance object
  def getInsList(node: Node): List[(String, PropValue)] = List()
  // Returns a list of properties in the instance object with default values
  // this method is used for the implementation of 'createElement'
  def default_getInsList(): List[(String, PropValue)] = List()
}

