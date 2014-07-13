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
var serviceInfo = [contactInfo];

var profile = new tizen.SyncProfileInfo("MyProfile", syncInfo, serviceInfo);
tizen.datasync.add(profile);

var syncCallback = {
   onprogress: function(profileId, serviceType, isFromServer, totalPerType, syncedPerType) {
   },
   oncompleted: function(profileId) {
   },
   onstopped: function(profileId) {
   },
   onfailed: function(profileId, error) {
   }
 };

 // Starts the sync operation with the corresponding callbacks
 tizen.datasync.startSync(profile.profileId, syncCallback);
var __result1 = tizen.datasync.stopSync(profile.profileId);
var __expect1 = undefined;