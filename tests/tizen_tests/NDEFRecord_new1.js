/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var msg1 = new tizen.NDEFRecord(1, [1, 2, 3], [2, 3, 4]);
//var msg2 = new tizen.NDEFMessage([new tizen.NDEFRecordURI("http://www.samsungmobile.com/")]);

var __result1 = msg1.tnf;
var __expect1 = 1;
var __result2 = msg1.type[0];
var __expect2 = 1;
var __result3 = msg1.payload[0];
var __expect3 = 2;