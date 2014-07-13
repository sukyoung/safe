/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function errorCB(err) {
  __result2 = err.name;
}

function successCB(path) {
  __result1 = path;
}

var path = "file:///opt/usr/media/tizen.jpg";
tizen.content.scanFile(path, successCB, errorCB);



var __expect1 = "file:///opt/usr/media/tizen.jpg";
var __expect2 = "UnknownError";
