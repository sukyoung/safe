/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onsuccess(files) {
   if (files[0].isDirectory == false)
      files[0].readAsText(
         function(str){
           __result1 = str;
         }, function(e){
           __result2 = e.name;
         }, "UTF-8"
      );
   }

function onerror(error) {

}

 tizen.filesystem.resolve(
     'documents',
     function(dir){
       dir.listFiles(onsuccess,onerror);
     }, function(e) {

     }, "rw"
 );



var __expect1 = "A";
var __expect2 = "UnknownError";