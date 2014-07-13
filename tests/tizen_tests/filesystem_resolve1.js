/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
tizen.filesystem.resolve(
   'images',
   function(dir) {
     __result1 = dir.path;
     __result2 = dir.readOnly;
   }, function(e) {
     __result3 = e.name;
   }, "r"
 );

var __expect1 = "images/";
var __expect2 = true;
var __expect3 = "NotFoundError";