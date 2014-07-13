/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var appControlData = new tizen.ApplicationControlData("image", ["1"]);


var __result1 = appControlData.key;
var __expect1 = "image";
var __result2 = appControlData.value[0];
var __expect2 = "1";

var appControlData1 = new tizen.ApplicationControlData(null, ["1"]);


var __result3 = appControlData1.key;
var __expect3 = "null";