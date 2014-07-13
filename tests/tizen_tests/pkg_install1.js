/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
var onInstallation = {
  onprogress: function(packageId, percentage) {
    __result1 = packageId;
    __result2 = percentage;
  },
  oncomplete: function(packageId) {
    __result3 = packageId;
  }
}

var onError = function (err) {
  __result4 = err.name;
}

tizen.package.install("http://tizen.org/download/aa.pkg", onInstallation, onError);

var __expect1 = "1"
var __expect2 = 2
var __expect3 = "1"
var __expect4 = "NotFoundError"