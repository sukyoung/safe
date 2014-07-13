/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4, __result5;
var file;
 tizen.filesystem.resolve(
     'documents',
     function(dir){
       file = dir.resolve("helloWorld.doc");
       __result1 = file.isDirectory;
       __result2 = file.isFile;
       __result3 = file.path;
       __result4 = file.fileSize;
       __result5 = file.length;
     }, function(e) {

     }, "rw"
 );



var __expect1 = false;
var __expect2 = true;
var __expect3 = "helloWorld.doc";
var __expect4 = 100;
var __expect5 = undefined;