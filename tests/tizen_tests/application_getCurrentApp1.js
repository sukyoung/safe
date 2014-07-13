/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var a = tizen.application.getCurrentApplication();

var __result1 = a.contextId
var __expect1 = "4644"
var __result2 = a.appInfo.categories.length
var __expect2 = 0
var __result3 = a.appInfo.iconPath
var __expect3 = "/opt/share/icons/default/small/1AxeWAQ0Ro.aaaa.png"
var __result4 = a.appInfo.id
var __expect4 = "1AxeWAQ0Ro.aaaa"
var __result5 = a.appInfo.installDate.toString()
var __expect5 = "Thu May 30 2013 17:16:16 GMT+0900 (KST)"
var __result6 = a.appInfo.name
var __expect6 = "aaaa"
var __result7 = a.appInfo.packageId
var __expect7 = "1AxeWAQ0Ro"
var __result8 = a.appInfo.show
var __expect8 = true
var __result9 = a.appInfo.size
var __expect9 = 339968
var __result10 = a.appInfo.version
var __expect10 = "1.0.0"