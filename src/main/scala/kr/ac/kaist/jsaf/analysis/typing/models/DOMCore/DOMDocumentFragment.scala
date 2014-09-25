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
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLDocument
import org.w3c.dom.{DocumentFragment, Node}
import kr.ac.kaist.jsaf.Shell

object DOMDocumentFragment extends DOM {
  private val name = "DocumentFragment"

  /* predefined locations */
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

  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
       DOMNode.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue)))
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

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case d: DocumentFragment =>
      // This instance object has all properties of the Node object
      DOMNode.getInsList(node) ++ List(
      ("@class",  PropValue(AbsString.alpha("Object"))),
      ("@proto",  PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue))
      )
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot be an instance of DocumentFragment.")
      List()
    }
  }


  override def default_getInsList(): List[(String, PropValue)] = {
    val nodeName = PropValue(ObjectValue(AbsString.alpha("#document-fragment"), F, T, T))
    val nodeValue = PropValue(ObjectValue(NullTop, F, T, T))
    val nodeType = PropValue(ObjectValue(AbsNumber.alpha(DOMNode.DOCUMENT_FRAGMENT_NODE), F, T, T))
    val parentNode = PropValue(ObjectValue(NullTop, F, T, T))
    val childNodes = PropValue(ObjectValue(NullTop, F, T, T))
    val firstChild = PropValue(ObjectValue(NullTop, F, T, T))
    val lastChild = PropValue(ObjectValue(NullTop, F, T, T))
    val previousSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val nextSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val ownerDocument = PropValue(ObjectValue(HTMLDocument.getInstance().get, F, T, T))
    val namespaceURI = PropValue(ObjectValue(NullTop, F, T, T))
    val prefix = PropValue(ObjectValue(NullTop, T, T, T))
    val localName = PropValue(ObjectValue(NullTop, F, T, T))
    val textContent = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val attributes = PropValue(ObjectValue(NullTop, F, T, T))
    // This instance object has all properties of the Node object
    DOMNode.getInsList(nodeName, nodeValue, nodeType, parentNode, childNodes, firstChild, lastChild,
           previousSibling, nextSibling, ownerDocument, namespaceURI, prefix, localName, textContent, attributes) ++ List(
      ("@class",  PropValue(AbsString.alpha("Object"))),
      ("@proto",  PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)))
  }
}
