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
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import org.w3c.dom.Node
import org.w3c.dom.Text
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLDocument
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMText extends DOM {
  private val name = "Text"

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
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(DOMCharacterData.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("splitText",        AbsBuiltinFunc("DOMText.splitText", 1)),
    ("replaceWholeText", AbsBuiltinFunc("DOMText.replaceWholeText", 1))
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
      //TODO: not yet implemented
      //case "DOMText.splitText" => ((h,ctx),(he,ctxe))
      //case "DOMText.replaceWholeText" => ((h,ctx),(he,ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMText.splitText" => ((h,ctx),(he,ctxe))
      //case "DOMText.replaceWholeText" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMText.splitText" => ((h,ctx),(he,ctxe))
      //case "DOMText.replaceWholeText" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMText.splitText" => ((h,ctx),(he,ctxe))
      //case "DOMText.replaceWholeText" => ((h,ctx),(he,ctxe))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case t: Text => 
      // This instance object has all properties of the CharacterData object
      DOMCharacterData.getInsList(node) ++ List(
      ("@class",  PropValue(AbsString.alpha("Object"))),
      ("@proto",  PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // Introduced in DOM Level 3
      ("isElementContentWhitespace",   PropValue(ObjectValue((if(t.isElementContentWhitespace==true) T else F), F, T, T))),
      ("wholeText",   PropValue(ObjectValue(AbsString.alpha(t.getWholeText), F, T, T))))

    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot be an instance of Text.")
      List()
    }
  }
 
  def getInsList(isElementContentWhitespace: PropValue, wholeText: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 3
    ("isElementContentWhitespace",         isElementContentWhitespace),
    ("wholeText",     wholeText)
   )
  
  def default_getInsList(data: PropValue, parent: PropValue, childN: PropValue): List[(String, PropValue)] = {    
    val nodeName = PropValue(ObjectValue(AbsString.alpha("#text"), F, T, T))
    val nodeValue = data
    val nodeType = PropValue(ObjectValue(AbsNumber.alpha(DOMNode.TEXT_NODE), F, T, T))
    val parentNode = parent
    val childNodes = childN
    val firstChild = PropValue(ObjectValue(NullTop, F, T, T))
    val lastChild = PropValue(ObjectValue(NullTop, F, T, T))
    val previousSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val nextSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val ownerDocument = PropValue(ObjectValue(HTMLDocument.getInstance().get, F, T, T))
    val namespaceURI = PropValue(ObjectValue(NullTop, F, T, T))
    val prefix = PropValue(ObjectValue(NullTop, T, T, T))
    val localName = PropValue(ObjectValue(NullTop, F, T, T))
    val textContent = data
 
    // This instance object has all properties of the CharacterData object and the Node object
    // could be more precise
    DOMCharacterData.getInsList(data) ++
    DOMNode.getInsList(nodeName, nodeValue, nodeType, parentNode, childNodes, firstChild, lastChild,
           previousSibling, nextSibling, ownerDocument, namespaceURI, prefix, localName, textContent) ++
    getInsList(PropValue(ObjectValue(BoolTop, F, T, T)), PropValue(ObjectValue(StrTop, F, T, T)))

  }

}
