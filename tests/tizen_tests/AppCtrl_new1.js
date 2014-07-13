/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var appControl =
       new tizen.ApplicationControl(
                  "http://tizen.org/appcontrol/operation/share",
                  "share.html",
                  "image/*",
                  null,
                  [new tizen.ApplicationControlData("images",
                                                    ["1"])] );

var __result1 = appControl.operation;
var __expect1 = "http://tizen.org/appcontrol/operation/share";
var __result2 = appControl.uri;
var __expect2 = "share.html";
var __result3 = appControl.mime;
var __expect3 = "image/*";
var __result4 = appControl.category;
var __expect4 = null;
var __result5 = appControl.data[0].key;
var __expect5 = "images";


