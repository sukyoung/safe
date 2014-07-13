/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

 function folderArrayCB(folders) {
    __result1 = folders[0].name;
  }
 function errorCallback(error) {
   __result2 = 2;
 }

function serviceListCB(services){
    var filter = new tizen.AttributeFilter("serviceId", "EXACTLY", services[0].id);
    services[0].messageStorage.findFolders(filter, folderArrayCB, errorCallback);
}

tizen.messaging.getMessageServices("messaging.sms", serviceListCB);

var __expect1 = "aa";
var __expect2 = 2;