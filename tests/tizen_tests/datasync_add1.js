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

tizen.datasync.add(profile);

var __result2 = profile.profileId;
var __expect2 = ""

var __result3;
try{
  tizen.datasync.add();
} catch(e){
  __result3 = e.name;
}
var __expect3 = "TypeMismatchError"

