/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function successCallback(msg) {
  __result1 = msg.filePath;
}
// Define the error callback.
function errorCallback(error) {
  __result2 = error.name;
}
function serviceListCB(services) {
   if (services.length > 0) {
     // SMS sending example
     var msg = new tizen.MessageAttachment("/msg/msg.sms");
     services[0].loadMessageAttachment(msg, successCallback, errorCallback);
   }
 }
 tizen.messaging.getMessageServices("messaging.sms", serviceListCB);



var __expect1 = "/msg/msg.sms"
var __expect2 = "NetworkError"