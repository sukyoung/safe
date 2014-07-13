/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();

function chatServiceSuccessCb(recordHandler) {
   __result1 = recordHandler.uuid;
};

var CHAT_SERVICE_UUID = "5BCE9431-6C75-32AB-AFE0-2EC108A30860";
adapter.registerRFCOMMServiceByUUID(CHAT_SERVICE_UUID, "Chat service", chatServiceSuccessCb,
// Error handler
function(e) {
   __result2 = e.name;
});


var __expect1 = "5BCE9431-6C75-32AB-AFE0-2EC108A30860";
var __expect2 = "SecurityError";