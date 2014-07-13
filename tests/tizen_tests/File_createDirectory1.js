/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4, __result5;
var newDir;
 tizen.filesystem.resolve(
     'documents',
     function(dir){
       newDir = dir.createDirectory("newDir");
       __result1 = newDir.isDirectory;
       __result2 = newDir.isFile;
       __result3 = newDir.path;
       __result4 = newDir.fileSize;
       __result5 = newDir.length;
     }, function(e) {

     }, "rw"
 );



var __expect1 = true;
var __expect2 = false;
var __expect3 = "newDir";
var __expect4 = undefined;
var __expect5 = 0;