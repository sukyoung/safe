/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var contactInfo = new tizen.SyncServiceInfo(true, "CONTACT", "serverContact");
var eventInfo = new tizen.SyncServiceInfo(true, "EVENT", "serverEvent", "1", "1234");

var __result1 = contactInfo.enable;
var __expect1 = true
var __result2 = contactInfo.serviceType;
var __expect2 = "CONTACT"
var __result3 = contactInfo.serverDatabaseUri;
var __expect3 = "serverContact"
var __result4 = contactInfo.id;
var __expect4 = null
var __result5 = contactInfo.password;
var __expect5 = null

var __result6 = eventInfo.enable;
var __expect6 = true
var __result7 = eventInfo.serviceType;
var __expect7 = "EVENT"
var __result8 = eventInfo.serverDatabaseUri;
var __expect8 = "serverEvent"
var __result9 = eventInfo.id;
var __expect9 = "1"
var __result10 = eventInfo.password;
var __expect10 = "1234"