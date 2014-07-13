/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var listener = {
   onprogress: function(id, receivedSize, totalSize) {

   },
   onpaused: function(id) {

   },
   oncanceled: function(id) {
   },
   oncompleted: function(id, fullPath) {
   },
   onfailed: function(id, error) {
   }
 };

 // Starts downloading of the file from the Web with the corresponding callbacks.
 var downloadRequest = new tizen.DownloadRequest("http://download.tizen.org/tools/README.txt", "documents");
 var downloadId = tizen.download.start(downloadRequest, listener);
 var state = tizen.download.getState(downloadId);

var __result1 = state;
var __expect1 = "DOWNLOADING";