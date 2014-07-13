/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function errorCallback(error) {
  __result1 = error.name;
}
function successCallback(services) {
  __result2 = services.length;
}

tizen.messaging.getMessageServices("messaging.email", successCallback, errorCallback);

var __expect1 = "UnknownError";
var __expect2 = 1;