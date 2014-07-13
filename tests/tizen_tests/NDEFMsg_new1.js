/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var msg1 = new tizen.NDEFMessage();
var msg2 = new tizen.NDEFMessage([new tizen.NDEFRecordURI("http://www.samsungmobile.com/")]);

var __result1 = msg1.recordCount;
var __expect1 = 0;
var __result2 = msg1.records.length;
var __expect2 = 0;

var __result3 = msg2.recordCount;
var __expect3 = 1;
var __result4 = msg2.records[0].uri;
var __expect4 = "http://www.samsungmobile.com/";