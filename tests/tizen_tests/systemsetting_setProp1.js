/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
// Define the success callback
function successCallback() {
   __result1 = 1;
}

// Define the error callback.
function errorCallback(error) {
   __result2 = 2;
}

tizen.systemsetting.setProperty("HOME_SCREEN", "images/image1.jpg", successCallback, errorCallback);

var __expect1 = 1;
var __expect2 = 2;
