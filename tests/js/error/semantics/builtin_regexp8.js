/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;
var __result1;
try {
	o = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*)$");
} catch(e) {
	__result1 = e.name;
}

var __expect1 = "SyntaxError";

