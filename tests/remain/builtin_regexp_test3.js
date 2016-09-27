/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = new RegExp("(aaa)|(abc)", "g");

var r = o.test("aabcaaa");
var __result1 = r;
var __expect1 = true;
var __result2 = o.lastIndex;
var __expect2 = 4;

