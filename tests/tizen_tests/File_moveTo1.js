/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onsuccess(files) {
   if (files[0].isDirectory == false)
     files[0].moveTo(files[0].fullPath,
                         "images/backup/" + files[0].name,
                         false,
                         function(){__result1 = 1;},
                         function(e){__result2 = e.name;}
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



var __expect1 = 1;
var __expect2 = "UnknownError";