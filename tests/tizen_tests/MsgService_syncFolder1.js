/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var emailService;
 function errorCallback(err) {
 }

 function serviceCallback(services) {
   emailService = services[0];
   var filter = new tizen.AttributeFilter("serviceId", "EXACTLY", emailService.id);

   emailService.messageStorage.findFolders(filter, folderQueryCallback);
 }

 // Define the success callback.
 function folderSynced() {
   __result1 = 1;
 }
 function folderQueryCallback(folders) {
    emailService.syncFolder(folders[0], folderSynced, null, 30);
 }

 tizen.messaging.getMessageServices("messaging.email", serviceCallback, errorCallback);




var __expect1 = 1;
