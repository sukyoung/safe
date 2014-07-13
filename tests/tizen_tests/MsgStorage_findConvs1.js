/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

function conversationsArrayCB(conversations) {
    __result1 = conversations[0].from;
}

 // Define error callback.
function errorCallback(error) {
    __result2 = error.name;
}

function serviceListCB(services){
    var filter = new tizen.AttributeFilter('from', 'CONTAINS', '2593');
    services[0].messageStorage.findConversations(filter, conversationsArrayCB, errorCallback);
}

tizen.messaging.getMessageServices("messaging.sms", serviceListCB);

var __expect1 = "2593";
var __expect2 = "UnknownError";
