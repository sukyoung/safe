/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var req = new tizen.DownloadRequest("http://download.tizen.org/tools/README.txt", "a", "b", "WIFI", {Pragma: "no-cache"});

var __result1 = req.url;
var __expect1 = "http://download.tizen.org/tools/README.txt"
var __result2 = req.destination;
var __expect2 = "a"
var __result3 = req.fileName;
var __expect3 = "b"
var __result4 = req.networkType;
var __expect4 = "WIFI"
var __result5 = req.httpHeader.Pragma;
var __expect5 = "no-cache"
