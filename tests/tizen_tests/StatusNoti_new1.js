/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var appControl = new tizen.ApplicationControl(
               "http://tizen.org/appcontrol/operation/create_content",
               null,
               "image/jpg",
               null);
var notificationDict = {
          content : "This is a simple notification.",
          iconPath : "images/image1.jpg",
          soundPath : "music/Over the horizon.mp3",
          vibration : true,
          appControl : appControl};

var noti = new tizen.StatusNotification("SIMPLE",
          "Simple notification", notificationDict);


var __result1 = noti.statusType;
var __expect1 = "SIMPLE";
var __result2 = noti.title;
var __expect2 = "Simple notification";
var __result3 = noti.content;
var __expect3 = "This is a simple notification.";
var __result4 = noti.iconPath;
var __expect4 = "images/image1.jpg";
var __result5 = noti.soundPath;
var __expect5 = "music/Over the horizon.mp3";
var __result6 = noti.vibration;
var __expect6 = true;
var __result7 = noti.appControl.operation;
var __expect7 = "http://tizen.org/appcontrol/operation/create_content";
var __result8 = noti.appControl.uri;
var __expect8 = null;
var __result9 = noti.appControl.mime;
var __expect9 = "image/jpg";
var __result10 = noti.appControl.category;
var __expect10 = null;
var __result11 = noti.id;
var __expect11 = undefined;