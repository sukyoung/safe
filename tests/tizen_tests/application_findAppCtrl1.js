/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var appControl = new tizen.ApplicationControl(
    "http://tizen.org/appcontrol/operation/create_content",
    null,
    "image/jpeg");

 function successCB(appInfos, appControl)
 {
   __result1 = appInfos[0].id;
   __result2 = appControl.operation;
 }
 function errorCB(e){
   __result3 = e.name;
 }
 tizen.application.findAppControl(appControl, successCB, errorCB);


var __expect1 = "a";
var __expect2 = appControl.operation;
var __expect3 = "UnknownError";
