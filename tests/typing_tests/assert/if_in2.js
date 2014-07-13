/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x = {}
x.a = 1;
if("a" in x) {
var __result1 = x.a;
}
else {
var __result2 = x.a
}
var __expect1 = 1;
var __expect2 = undefined
