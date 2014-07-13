/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var adapter = tizen.bluetooth.getDefaultAdapter();
function chatServiceSuccessCb(handler) {
    handler.unregister(function() {
     __result1 = handler.isConnected;
    },
    function(e) {
     __result2 = handler.isConnected;
     __result3 = e.name;
    });

 }

var CHAT_SERVICE_UUID = "5BCE9431-6C75-32AB-AFE0-2EC108A30860";
adapter.registerRFCOMMServiceByUUID(CHAT_SERVICE_UUID, "Chat service", chatServiceSuccessCb,
      // Error handler
      function(e) {

      });




var __expect1 = false;
var __expect2 = true;
var __expect3 = "UnknownError";