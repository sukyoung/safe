/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4, __result5, __result6, __result7, __result8, __result9;
// Creates a sync info
var syncInfo = new tizen.SyncInfo("http://example.com/sync", "myId", "myPassword", "MANUAL", "TWO_WAY");
// Syncs both contacts and events
var contactInfo = new tizen.SyncServiceInfo(true, "CONTACT", "serverContact");
var serviceInfo = [contactInfo];

var profile = new tizen.SyncProfileInfo("MyProfile", syncInfo, serviceInfo);
tizen.datasync.add(profile);

var syncCallback = {
   onprogress: function(profileId, serviceType, isFromServer, totalPerType, syncedPerType) {
      __result1 = profileId;
      __result2 = serviceType;
      __result3 = isFromServer;
      __result4 = totalPerType;
      __result5 = syncedPerType;
   },
   oncompleted: function(profileId) {
      __result6 = profileId;
   },
   onstopped: function(profileId) {
      __result7 = profileId;
   },
   onfailed: function(profileId, error) {
      __result8 = profileId;
      __result9 = error.name;
   }
 };

 // Starts the sync operation with the corresponding callbacks
 tizen.datasync.startSync(profile.profileId, syncCallback);

 var __expect1 = profile.profileId;
 var __expect2 = "CONTACT";
 var __expect3 = false;
 var __expect4 = 1;
 var __expect5 = 1;
 var __expect6 = profile.profileId;
 var __expect7 = profile.profileId;
 var __expect8 = profile.profileId;
 var __expect9 = "UnknownError";
