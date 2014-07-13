/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
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
var notification = new tizen.StatusNotification("SIMPLE",
                                         "Simple notification", notificationDict);
tizen.notification.post(notification);
// Uses a variable for the previously posted notification.
__result1 = tizen.notification.removeAll();

var __expect1 = undefined;
