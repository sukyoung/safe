/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
 function notificationCallback(noti) {
    __result1 = noti.appData;
    __result2 = noti.alertMessage;
    __result3 = noti.date.toString();
  }

  // Requests for push service connection
  tizen.push.connectService(notificationCallback);


var __expect1 = "a"
var __expect2 = "a"
var __expect3 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
