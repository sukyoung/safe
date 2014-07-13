/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;

 var convChangeCallback = {
         conversationsadded: function(messages) {
           __result1 = 1;
           },
         conversationsupdated: function(messages) {
           __result2 = 2;
           },
         conversationsremoved: function(messages) {
           __result3 = 3;
           }
 };

function serviceListCB(services){
    services[0].messageStorage.addConversationsChangeListener(convChangeCallback);
}

tizen.messaging.getMessageServices("messaging.sms", serviceListCB);

var __expect1 = 1;
var __expect2 = 2;
var __expect3 = 3;