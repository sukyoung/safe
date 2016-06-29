/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o1,o2,o3;
o1 = { };
o2 = { };
if (__TOP) {
    o3 = o1;
} else {
    o3 = o2;
}
o3.p1 = 123;

var __result1 = o1.p1; 
var __expect1 = 123;

var __result2 = o2.p1; 
var __expect2 = 123;
