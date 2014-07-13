/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1, p2:2};

var x = Object.seal(o);

var __result1 = delete x.p1;
var __expect1 = false;

var __result2 = delete x.p2;
var __expect2 = false;

x.p3 = 3;
var __result3 = x.p3;
var __expect3 = undefined;

var __result4 = Object.isSealed(x);
var __expect4 = true;

var __result5 = Object.isExtensible(x);
var __expect5 = false;