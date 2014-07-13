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
var profile = new tizen.SyncProfileInfo("MyProfile", syncInfo, [contactInfo]);
tizen.datasync.add(profile);
var statistics = tizen.datasync.getLastSyncStatistics(profile.profileId);

var __result1 = statistics[0].syncStatus;
var __expect1 = "SUCCESS"
var __result2 = statistics[0].serviceType;
var __expect2 = "CONTACT"
var __result3 = statistics[0].lastSyncTime.toString();
var __expect3 = ""
var __result4 = statistics[0].serverToClientTotal;
var __expect4 = 1
var __result5 = statistics[0].serverToClientAdded;
var __expect5 = 1
var __result6 = statistics[0].serverToClientUpdated;
var __expect6 = 1
var __result7 = statistics[0].serverToClientRemoved;
var __expect7 = 1
var __result8 = statistics[0].clientToServerTotal;
var __expect8 = 1
var __result9 = statistics[0].clientToServerAdded;
var __expect9 = 1
var __result10 = statistics[0].clientToServerUpdated;
var __expect10 = 1
var __result11 = statistics[0].clientToServerRemoved;
var __expect11 = 1