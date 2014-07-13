/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
 // Defines the error callback.
 function errorCallback(err) {
   __result1 = err.name;
 }

 // Defines the registration success callback
 function unregisterSuccessCallback() {
   __result2 = 1;
 }

 // Requests registration.
 tizen.push.unregisterService(unregisterSuccessCallback, errorCallback);


var __expect1 = "UnknownError"
var __expect2 = 1

