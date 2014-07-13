/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var messageStorage;
 function successCallback() {
   __result1 = 1;
 }
 function errorCallback(error) {
   __result2 = 2;
 }

function conversationsArrayCB(conversations) {
     messageStorage.removeConversations(conversations, successCallback,
                                                       errorCallback);
 }

 // Define error callback.
 function queryErrorCB(error) {
 }

// Define service query success callback.
 function serviceListCB(services) {
   if (services.length > 0) {
     messageStorage = services[0].messageStorage;
     var filter = new tizen.AttributeFilter('from', 'CONTAINS', '2593');
     messageStorage.findConversations(filter, conversationsArrayCB,
                                           queryErrorCB);

   }
 }
 tizen.messaging.getMessageServices("messaging.sms", serviceListCB);


var __expect1 = 1;
var __expect2 = 2;