/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

 function successCallback() {
   __result1 = 1;
 }
 function errorCallback(error) {
   __result2 = 2;
 }

function serviceListCB(services){
    var msg1 = new tizen.Message("messaging.sms", {to: ["+34666666666", "+34888888888"]});
    services[0].messageStorage.updateMessages([msg1], successCallback, errorCallback);
}

tizen.messaging.getMessageServices("messaging.sms", serviceListCB);

var __expect1 = 1;
var __expect2 = 2;