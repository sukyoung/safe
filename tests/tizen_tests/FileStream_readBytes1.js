/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function filestreamsuccess(fs){
  var b = fs.readBytes(3);
  __result1 = b[0];
  __result2 = b[1];
  __result3 = b[2];
}


function onsuccess(files) {
     if (files[0].isFile){
         files[0].openStream(
             "w",
             filestreamsuccess
             , function(e){

             }, "UTF-8"
         );
     }
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



var __expect1 = 1;
var __expect2 = 2;
var __expect3 = 3;