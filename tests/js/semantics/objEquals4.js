/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o1 = {a:1};
var o2;

var __result1 = o1 == o2;
var __expect1 = false;

var __result2 = o2 == o1;
var __expect2 = false;

var __result3 = o1 === o2;
var __expect3 = false;

var __result4 = o2 === o1;
var __expect4 = false;


var __result5 = o1 != o2;
var __expect5 = true;

var __result6 = o2 != o1;
var __expect6 = true;

var __result7 = o1 !== o2;
var __expect7 = true;

var __result8 = o2 !== o1;
var __expect8 = true;