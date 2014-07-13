/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1, p2:2};

var x = Object.freeze(o);

var __result1 = delete x.p1;
var __expect1 = false;

var __result2 = delete x.p2;
var __expect2 = false;

x.p1 = 111;
var __result3 = x.p1;
var __expect3 = 1;

x.p2 = 222;
var __result4 = x.p2;
var __expect4 = 2;

x.p3 = 3;
var __result5 = x.p3;
var __expect5 = undefined;

var __result6 = Object.isSealed(x);
var __expect6 = true;

var __result7 = Object.isFrozen(x);
var __expect7 = true;

var __result8 = Object.isExtensible(x);
var __expect8 = false;