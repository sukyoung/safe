/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
function filestreamsuccess(fs){
  __result1 = fs.close();
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



var __expect1 = undefined;
