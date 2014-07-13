/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4, __result5;
var listener = {
   onprogress: function(id, receivedSize, totalSize) {
     __result1 = id;
   },
   onpaused: function(id) {
     __result2 = id;
   },
   oncanceled: function(id) {
     __result3 = id;
   },
   oncompleted: function(id, fileName) {
     __result4 = id;
   },
   onfailed: function(id, error) {
     __result5 = id;
   }
 };

 // Start downloading the html file on the web with the corresponding callbacks.
 var downloadRequest = new tizen.DownloadRequest("http://download.tizen.org/tools/README.txt", "documents");
 downloadId = tizen.download.start(downloadRequest);

 // Add the listener.
 tizen.download.setListener(downloadId, listener);

 var __expect1 = downloadId;
 var __expect2 = downloadId;
 var __expect3 = downloadId;
 var __expect4 = downloadId;
 var __expect5 = downloadId;