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
tizen.datasync.add(profile);
var profs = tizen.datasync.getAll();

var __result1 = profs[0].profileId;
var __expect1 = profile.profileId;
var __result2 = profs[0].profileName;
var __expect2 = profile.profileName;
var __result3 = profs[0].syncInfo.url;
var __expect3 = profile.syncInfo.url;
var __result4 = profs[0].serviceInfo.length;
var __expect4 = profile.serviceInfo.length;