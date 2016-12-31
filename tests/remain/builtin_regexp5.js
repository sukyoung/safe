/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o_1 = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$");
var o_2;
var __result1;
try {
	o_2 = new RegExp(o_1, 1);
} catch(e) {
	__result1 = e.name;
}

var __expect1 = "TypeError";

