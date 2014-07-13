/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var reqAppControl = tizen.application.getCurrentApplication().getRequestedAppControl();

var __result1 = reqAppControl.appControl.category;
var __expect1 = null
var __result2 = reqAppControl.appControl.data[0].key;
var __expect2 = "debug"
var __result3 = reqAppControl.appControl.data[0].value[0];
var __expect3 = "true"
var __result4 = reqAppControl.appControl.data[1].key;
var __expect4 = "pid"
var __result5 = reqAppControl.appControl.data[1].value[0];
var __expect5 = "2801"
var __result6 = reqAppControl.appControl.mime;
var __expect6 = null
var __result7 = reqAppControl.appControl.operation;
var __expect7 = "http://tizen.org/appcontrol/operation/default"
var __result8 = reqAppControl.appControl.uri;
var __expect8 = null
var __result9 = reqAppControl.callerAppId;
var __expect9 = ""