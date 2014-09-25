/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Attr
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLDocument
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object DOMAttr extends DOM {
  private val name = "Attr"
  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  private val prop_ins: List[(String, AbsProperty)] = 
       DOMNode.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
      ("specified", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
      ("value", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
      ("isId", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
     )
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(DOMNode.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 

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
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())

  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case a: Attr => 
      // This instance object has all properties of the Node object
      DOMNode.getInsList(node) ++ List(
      ("@class", PropValue(AbsString.alpha("Object"))),
      ("@proto", PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("name",   PropValue(ObjectValue(AbsString.alpha(a.getNodeName), F, T, T))),
      ("specified",   PropValue(ObjectValue(if(a.getSpecified==true) T else F, F, T, T))),
      ("value",   PropValue(ObjectValue(AbsString.alpha(a.getValue), T, T, T))),
      // Introduced in DOM Level 3
      ("isId",   PropValue(ObjectValue(if(a.isId==true) T else F, T, T, T))))
      // TODO: ownerElement in DOM Level 2, schemaTypeInfo in DOM Level 3
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
 
  def getInsList(name: PropValue, specified: PropValue, value: PropValue, isId: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("name",         name),
    ("specified",    specified),
    ("value",        value),
    // Introduced in DOM Level 3
    ("isId",         isId)
   )
  
  def default_getInsList(name: PropValue, value: PropValue, childN : PropValue, child: PropValue): List[(String, PropValue)] = {    
    val nodeName = name
    val nodeValue = value
    val nodeType = PropValue(ObjectValue(AbsNumber.alpha(DOMNode.ATTRIBUTE_NODE), F, T, T))
    val parentNode = PropValue(ObjectValue(NullTop, F, T, T))
    val childNodes = childN
    val firstChild = child
    val lastChild = child
    val previousSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val nextSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val ownerDocument = PropValue(ObjectValue(HTMLDocument.getInstance().get, F, T, T))
    val namespaceURI = PropValue(ObjectValue(NullTop, F, T, T))
    val prefix = PropValue(ObjectValue(NullTop, T, T, T))
    val localName = PropValue(ObjectValue(NullTop, F, T, T))
    val textContent = value
 
    // This instance object has all properties of the Node object
    // could be more precise
    DOMNode.getInsList(nodeName, nodeValue, nodeType, parentNode, childNodes, firstChild, lastChild,
           previousSibling, nextSibling, ownerDocument, namespaceURI, prefix, localName, textContent) ++
    getInsList(name, PropValue(ObjectValue(BoolFalse, F, T, T)), value, PropValue(ObjectValue(BoolTop, T, T, T)))

  }


}
