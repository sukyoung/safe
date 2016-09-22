/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1, p2:2};

var x = Object.preventExtensions(o);


x.p3 = 3;
var __result1 = x.p3;
var __expect1 = undefined;

var __result2 = Object.isExtensible(x);
var __expect2 = false;
