/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = new Date();

var __result1 = x.toString();
var __expect1 = "Tue Oct 30 2012 18:25:50 GMT+0900 (KST)";

var __result2 = x.toDateString();
var __expect2 = "Tue Oct 30 2012";

var __result3 = x.toTimeString();
var __expect3 = "18:25:50 GMT+0900 (KST)";

var __result4 = x.toLocaleString()
var __expect4 = "Tue Oct 30 2012 18:25:50 GMT+0900 (KST)";

var __result5 = x.toLocaleDateString();
var __expect5 = "Tuesday, October 30, 2012";

var __result6 = x.toLocaleTimeString();
var __expect6 = "18:25:50"

var __result7 = x.toUTCString();
var __expect7 = "Tue, 30 Oct 2012 09:25:50 GMT";

var __result8 = x.toISOString();
var __expect8 = "2012-10-30T09:25:50.167Z";
