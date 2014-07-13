/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var msg1 = new tizen.NDEFMessage();
//var msg2 = new tizen.NDEFMessage([new tizen.NDEFRecordURI("http://www.samsungmobile.com/")]);
var raw = msg1.toByte();

var __result1 = raw[0];
var __expect1 = 0;