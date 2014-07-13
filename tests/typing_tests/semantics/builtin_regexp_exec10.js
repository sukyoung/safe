/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o1 = new RegExp("\/");
var o2 = /\//;

var r1 = o1.exec("/");
var r2 = o2.exec("/");

var __result1 = r1[0]
var __expect1 = "/";
var __result2 = r1.length;
var __expect2 = 1;

var __result3 = r2[0]
var __expect3 = "/";
var __result4 = r2.length;
var __expect4 = 1;

var __result5 = o1.source == o2.source;
var __expect5 = false;

