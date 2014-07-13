/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Creates a sync info
var msg1 = new tizen.NDEFRecordText("aa", "es-US");
//var msg2 = new tizen.NDEFMessage([new tizen.NDEFRecordURI("http://www.samsungmobile.com/")]);

var __result1 = msg1.text;
var __expect1 = "aa";
var __result2 = msg1.languageCode;
var __expect2 = "es-US";
var __result2 = msg1.encoding;
var __expect2 = "UTF8";