/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function scb() { __result1 = 1;}
function ecb(e) { __result2 = e.name; }
var statuscb = {
   onsuccess: function() {
     tizen.networkbearerselection.releaseRouteToHost("CELLULAR", "www.google.com", scb, ecb);
   },
   ondisconnected: function() {  }
 };
 tizen.networkbearerselection.requestRouteToHost("CELLULAR", "www.google.com", statuscb, ecb);


var __expect1 = 1;
var __expect2 = "UnknownError";
