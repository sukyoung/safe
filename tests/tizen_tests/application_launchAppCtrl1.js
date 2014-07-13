/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
var appControl = new tizen.ApplicationControl(
    "http://tizen.org/appcontrol/operation/create_content",
    null,
    "image/jpeg");

 var appControlReplyCallback = {
    // callee sent a reply
    onsuccess: function(data) {
       __result1 = data.length;
    },
    // Something went wrong
    onfailure: function() {
       __result2 = 2;
    }
 }

 tizen.application.launchAppControl(
          appControl,
          null,
    function() {
      __result3 = 3;
    },
    function(e) {
      __result4 = e.name;
    },
    appControlReplyCallback );

var __expect1 = 0;
var __expect2 = 2;
var __expect3 = 3;
var __expect4 = "UnknownError";