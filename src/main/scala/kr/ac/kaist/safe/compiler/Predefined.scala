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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.util.{ NodeUtil => NU }

class Predefined(params: ShellParameters) {
  val doms = List(
    // DOM non-functions
    "CanvasGradient",
    "CanvasRenderingContext2D",
    "DOMException",
    "devicePixelRatio",
    "document",
    "frameElement",
    "frames",
    "history",
    "innerHeight",
    "innerWidth",
    "length",
    "localStorage",
    "location",
    "name",
    "navigator",
    "onerror",
    "onload",
    "opener",
    "outerHeight",
    "outerWidth",
    "pageXOffset",
    "pageYOffset",
    "parent",
    "screen",
    "screenX",
    "screenY",
    "scrollMaxX",
    "scrollMaxY",
    "scrollX",
    "scrollY",
    "self",
    "sessionStorage",
    "status",
    "top",
    "window",
    "console",
    "XMLHttpRequest",
    // DOM (possibly) functions
    "Attr",
    "CDATASection",
    "CharacterData",
    "Comment",
    "DOMConfiguration",
    "DOMError",
    "DOMImplementation",
    "DOMImplementationList",
    "DOMImplementationRegistry",
    "DOMImplementationSource",
    "DOMLocator",
    "DOMStringList",
    "Document",
    "DocumentFragment",
    "DocumentType",
    "Element",
    "Entity",
    "EntityReference",
    "Event",
    "EventException",
    "HTMLAnchorElement",
    "HTMLAppletElement",
    "HTMLAreaElement",
    "HTMLBRElement",
    "HTMLBaseElement",
    "HTMLBaseFontElement",
    "HTMLBodyElement",
    "HTMLButtonElement",
    "HTMLCanvasElement",
    "HTMLCollection",
    "HTMLDataListElement",
    "HTMLDListElement",
    "HTMLDirectoryElement",
    "HTMLDivElement",
    "HTMLDocument",
    "HTMLElement",
    "HTMLFieldSetElement",
    "HTMLFontElement",
    "HTMLFormElement",
    "HTMLFrameElement",
    "HTMLFrameSetElement",
    "HTMLHRElement",
    "HTMLHeadElement",
    "HTMLHeadingElement",
    "HTMLHtmlElement",
    "HTMLIFrameElement",
    "HTMLImageElement",
    "HTMLInputElement",
    "HTMLIsIndexElement",
    "HTMLLIElement",
    "HTMLLabelElement",
    "HTMLLegendElement",
    "HTMLLinkElement",
    "HTMLMapElement",
    "HTMLMenuElement",
    "HTMLMetaElement",
    "HTMLModElement",
    "HTMLOListElement",
    "HTMLObjectElement",
    "HTMLOptGroupElement",
    "HTMLOptionsCollection",
    "HTMLOptionElement",
    "HTMLParagraphElement",
    "HTMLParamElement",
    "HTMLPreElement",
    "HTMLQuoteElement",
    "HTMLScriptElement",
    "HTMLSelectElement",
    "HTMLStyleElement",
    "HTMLTableCaptionElement",
    "HTMLTableCellElement",
    "HTMLTableColElement",
    "HTMLTableElement",
    "HTMLTableRowElement",
    "HTMLTableSectionElement",
    "HTMLTextAreaElement",
    "HTMLTitleElement",
    "HTMLUListElement",
    "HTMLUnknownElement",
    "KeyboardEvent",
    "MessageEvent",
    "MouseEvent",
    "MutationEvent",
    "NameList",
    "NamedNodeMap",
    "Node",
    "NodeList",
    "Notation",
    "ProcessingInstruction",
    "Text",
    "TypeInfo",
    "UIEvent",
    "UserDataHandler",
    "addEventListener",
    "alert",
    "atob",
    "back",
    "blur",
    "btoa",
    "clearInterval",
    "clearTimeout",
    "close",
    "confirm",
    "dispatchEvent",
    "escape",
    "focus",
    "foward",
    "getComputedStyle",
    "home",
    "maximize",
    "minimize",
    "moveBy",
    "moveTo",
    "open",
    "postMessage",
    "print",
    "prompt",
    "removeEventListener",
    "resizeBy",
    "resizeTo",
    "scroll",
    "scrollBy",
    "scrollByLines",
    "scrollByPages",
    "scrollTo",
    "setInterval",
    "setTimeout",
    "stop",
    "unescape",
    "Image"
  )

  val vars = List(
    // 4.2 Language Overview
    "Object",
    "Function",
    "Array",
    "String",
    "Boolean",
    "Number",
    "Math",
    "Date",
    "RegExp",
    "JSON",
    "Error",
    "EvalError",
    "RangeError",
    "ReferenceError",
    "SyntaxError",
    "TypeError",
    "URIError",
    // 15.1.1 Value Properties of the Global Object
    "NaN",
    "Infinity",
    "undefined",
    // predefined constant variables from IR
    NU.VAR_TRUE,
    NU.VAR_ONE,
    NU.freshGlobalName("global")
  )

  val varsAll = params.command match {
    case ShellParameters.CMD_HTML => vars ++ doms
    case ShellParameters.CMD_HTML_SPARSE => vars ++ doms
    case ShellParameters.CMD_WEBAPP_BUG_DETECTOR => vars ++ doms
    case _ => vars
  }

  val funs = List(
    // 15.1.2 Function Properties of the Global Object
    "eval",
    "parseInt",
    "parseFloat",
    "isNaN",
    "isFinite",
    // 15.1.3 URI Handling Function Properties
    "decodeURI",
    "decodeURIComponent",
    "encodeURI",
    "encodeURIComponent"
  )
  val tizens =
    if (params.opt_Tizen == true)
      List("tizen")
    else List()

  val jquery =
    if (params.opt_jQuery == true)
      List("$", "jQuery")
    else List()

  //val all = varsAll ++ funs ++ tizens ++ jquery
  val all = varsAll ++ funs ++ tizens

  def contains(name: String): Boolean =
    varsAll.contains(name) || funs.contains(name)
}
