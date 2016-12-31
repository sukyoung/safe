/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$");

var r = o.exec("#abc");
var __result1 = r[0];
var __expect1 = "#abc";
var __result2 = r[1];
var __expect2 = undefined;
var __result3 = r[2];
var __expect3 = "abc";
var __result4 = r.index;
var __expect4 = 0;
var __result5 = r.input;
var __expect5 = "#abc";
var __result6 = r.length;
var __expect6 = 3;

