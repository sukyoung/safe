/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function messageSent(recipients) {
  __result1 = recipients[0];
}
// Define the error callback.
function messageFailed(error) {
  __result2 = error.name;
}
function serviceListCB(services) {
   if (services.length > 0) {
     // SMS sending example
     var msg = new tizen.Message("messaging.sms", {to: ["+34666666666", "+34888888888"]});
     // Send request
     services[0].sendMessage(msg, messageSent, messageFailed);
   }
 }
 tizen.messaging.getMessageServices("messaging.sms", serviceListCB);



var __expect1 = "a"
var __expect2 = "NetworkError"