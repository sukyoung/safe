/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var syncInfo1 = new tizen.SyncInfo("http://example.com/sync", "myId", "myPassword", "MANUAL", "TWO_WAY");
var syncInfo2 = new tizen.SyncInfo("http://example.com/sync", "myId", "myPassword", "MANUAL", "5_MINUTES");

var __result1 = syncInfo1.url;
var __expect1 = "http://example.com/sync"
var __result2 = syncInfo1.id;
var __expect2 = "myId"
var __result3 = syncInfo1.password;
var __expect3 = "myPassword"
var __result4 = syncInfo1.mode;
var __expect4 = "MANUAL"
var __result5 = syncInfo1.type;
var __expect5 = "TWO_WAY"
var __result6 = syncInfo1.interval;
var __expect6 = null

var __result7 = syncInfo2.url;
var __expect7 = "http://example.com/sync"
var __result8 = syncInfo2.id;
var __expect8 = "myId"
var __result9 = syncInfo2.password;
var __expect9 = "myPassword"
var __result10 = syncInfo2.mode;
var __expect10 = "MANUAL"
var __result11 = syncInfo2.type;
var __expect11 = null
var __result12 = syncInfo2.interval;
var __expect12 = "5_MINUTES"