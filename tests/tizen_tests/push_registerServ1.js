/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
// Defines the data to be used when this process is launched by notification service.
 var service = new tizen.ApplicationControl("http://tizen.org/appcontrol/operation/push_test");

 // Defines the error callback.
 function errorCallback(err) {
   __result1 = err.name;
 }

 // Defines the registration success callback
 function registerSuccessCallback(id) {
   __result2 = id;
 }

 // Requests registration.
 tizen.push.registerService(service, registerSuccessCallback, errorCallback);

var __expect1 = "UnknownError"
var __expect2 = "aa"

