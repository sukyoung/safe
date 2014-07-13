/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var statuscb = {
   onsuccess: function() { __result1 = 1; },
   ondisconnected: function() { __result2 = 2; }
 };
 function ecb(e) { __result3 = e.name; }
 tizen.networkbearerselection.requestRouteToHost("CELLULAR", "www.google.com", statuscb, ecb);

var __expect1 = 1;
var __expect2 = 2;
var __expect3 = "UnknownError";
