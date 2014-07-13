/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var packageEventCallback = {
    oninstalled: function(packageInfo) {
       __result1 = packageInfo.id;
    },
    onupdated: function(packageInfo) {
       __result2 = packageInfo.id;
    },
    onuninstalled: function(packageId) {
       __result3 = packageId;
    }
 };

 tizen.package.setPackageInfoEventListener(packageEventCallback);


var __expect1 = "org.tizen.calculator"
var __expect2 = "org.tizen.calculator"
var __expect3 = "org.tizen.calculator"

