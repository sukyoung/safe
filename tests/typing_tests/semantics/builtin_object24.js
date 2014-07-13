/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1};
var prop = {value:2, writable:true, enumerable:false, configurable:true}

var x = Object.defineProperty(o, "p2", prop);

var __result1 = x.propertyIsEnumerable("p1");
var __expect1 = true;

var __result2 = x.propertyIsEnumerable("p2");
var __expect2 = false;