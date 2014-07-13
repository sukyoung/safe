/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var appEventCallback = {
    oninstalled: function(application) {
    },
    onupdated: function(application) {
    },
    onuninstalled: function(appid) {
    }
 };
 var watchId =
    tizen.application.addAppInfoEventListener(appEventCallback);
var __result1 = tizen.application.removeAppInfoEventListener(watchId);
var __expect1 = undefined;
