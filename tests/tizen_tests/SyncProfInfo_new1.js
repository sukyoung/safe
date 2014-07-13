/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var syncInfo = new tizen.SyncInfo("http://example.com/sync", "myId", "myPassword", "MANUAL", "TWO_WAY");
// Syncs both contacts and events
var contactInfo = new tizen.SyncServiceInfo(true, "CONTACT", "serverContact");
var eventInfo = new tizen.SyncServiceInfo(true, "EVENT", "serverEvent");
var serviceInfo = [contactInfo, eventInfo];
var profile = new tizen.SyncProfileInfo("MyProfile", syncInfo, serviceInfo);

var __result1 = profile.profileId;
var __expect1 = null
var __result2 = profile.profileName;
var __expect2 = "MyProfile"
var __result3 = profile.syncInfo.url;
var __expect3 = "http://example.com/sync"
var __result4 = profile.syncInfo.id;
var __expect4 = "myId"
var __result5 = profile.syncInfo.password;
var __expect5 = "myPassword"
var __result6 = profile.syncInfo.mode;
var __expect6 = "MANUAL"
var __result7 = profile.syncInfo.type;
var __expect7 = "TWO_WAY"
var __result8 = profile.syncInfo.interval;
var __expect8 = null
var __result9 = profile.serviceInfo[0].serviceType;
var __expect9 = "CONTACT"
var __result10 = profile.serviceInfo[1].serviceType;
var __expect10 = "EVENT"
