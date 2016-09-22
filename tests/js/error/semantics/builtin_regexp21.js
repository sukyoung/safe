/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = new RegExp("\/");

var __result1 = o.source;
var __expect1 = "/";

var o2 = /\//;
var __result2 = o2.source;
var __expect2 = "\\/";

var o3 = new RegExp("/");

var __result3 = o3.source;
var __expect3 = "/";

