/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1 = void 1;
var __expect1 = undefined;

var __result2 = void "1";
var __expect2 = undefined;

var __result3 = void undefined;
var __expect3 = undefined;

var __result2 = + 1;
var __expect2 = 1;

var __result3 = + (-1);
var __expect3 = -1;

var __result4 = - 2;
var __expect4 = -2;

var __result5 = - (-3);
var __expect5 = 3;

var __result6 = ~ 1;
var __expect6 = -2;

var __result7 = ~ 11;
var __expect7 = -12;

var __result8 = ! 111;
var __expect8 = false;

var __result9 = ! 0;
var __expect9 = true;

