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

var __result1 = profile.profileId;
var __expect1 = "1"
var __result2 = profile.profileName;
var __expect2 = "MyProfile"

tizen.datasync.update(profile);

var __result3 = profile.profileId;
var __expect3 = "1"
var __result4 = profile.profileName;
var __expect4 = "MyProfile"