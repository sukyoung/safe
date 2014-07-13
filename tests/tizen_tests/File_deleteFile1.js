/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
var documentsDir;
 function onsuccess(files) {
     if (files[0].isDirectory) {
       documentsDir.deleteDirectory(
           files[0].fullPath,
           false,
           function(){
             __result3 = 3;
           }, function(e) {
             __result4 = 4;
           });
     } else {
       documentsDir.deleteFile(
          files[0].fullPath,
          function(){
            __result1 = 1;
          }, function(e) {
            __result2 = 2;
          });

     }

 }

 function onerror(error) {

 }

 tizen.filesystem.resolve(
     'documents',
     function(dir){
       documentsDir = dir;
       dir.listFiles(onsuccess,onerror);
     }, function(e) {

     }, "rw"
 );




var __expect1 = 1;
var __expect2 = 2;
var __expect3 = 3;
var __expect4 = 4;
