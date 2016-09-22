/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o_1 = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$");
o_1.lastIndex = 1;
var o_2 = new RegExp(o_1, undefined);

var __result1 = o_1 == o_2;
var __expect1 = false;
var __result2 = o_1.source == o_2.source;
var __expect2 = true;
var __result3 = o_1.global == o_2.global;
var __expect3 = true;
var __result4 = o_1.ignoreCase == o_2.ignoreCase;
var __expect4 = true;
var __result5 = o_1.multiline == o_2.multiline;
var __expect5 = true;
var __result6 = o_1.lastIndex == o_2.lastIndex;
var __expect6 = false;

