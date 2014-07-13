/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var appEventCallback = {
    oninstalled: function(application) {
      __result1 = application.id;
    },
    onupdated: function(application) {
      __result2 = application.id;
    },
    onuninstalled: function(appid) {
      __result3 = appid;
    }
 };
 var watchId =
    tizen.application.addAppInfoEventListener(appEventCallback);

var __expect1 = "a";
var __expect2 = "a";
var __expect3 = "a";
var __result4 = watchId;
var __expect4 = 1;