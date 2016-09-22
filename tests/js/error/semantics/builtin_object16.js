/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1};
var prop = {value:2, writable:true, enumerable:true, configurable:false}

var x = Object.defineProperty(o, "p2", prop);

var __result1 = x.p1;
var __expect1 = 1;

var __result2 = x.p2;
var __expect2 = 2;

var __result3 = delete x.p2;
var __expect3 = false;