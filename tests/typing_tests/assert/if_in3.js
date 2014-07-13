/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x = {};
x[__TOP] = "asdf";
x.a = 1;
if(__TOP) {
} else {
delete x.a
}
if("a" in x) {
x.a = 2;
var __result1 = x.a;
} else {
delete x.a;
var __result2 = x.a;
}
x[__TOP] = "";
var __result4 = x.a;

var __expect1 = 2;
var __expect2 = "asdf"; // by x[__TOP], it can be "asdf"
var __expect4 = 2;
