/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function onsuccess(files) {
   __result1 = files.length;
   __result2 = files[0].parent.isFile;
 }

 function onerror(error) {
   __result3 = error.name;
 }

 var filter = {
   name: "aa"
 };
 tizen.filesystem.resolve(
     "documents",
     function(dir){
       dir.listFiles(onsuccess, onerror, filter);
     }, function(e){

     }, "r"
 );



var __expect1 = 1;
var __expect2 = false;
var __expect3 = "InvalidValuesError";