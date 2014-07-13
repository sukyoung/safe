/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1};
var props = {p2:{value:2, writable:true, enumerable:true, configurable:false}, 
		     p3:{value:3, writable:true, enumerable:true, configurable:true},}

var x = Object.defineProperties(o, props);

var __result1 = x.p1;
var __expect1 = 1;

var __result2 = x.p2;
var __expect2 = 2;

var __result3 = x.p3;
var __expect3 = 3;

var __result4 = delete x.p2;
var __expect4 = false;

var __result5 = delete x.p3;
var __expect5 = true;