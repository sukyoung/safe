/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var msgattach1 = new tizen.MessageAttachment("images/myimage.png");
var msgattach2 = new tizen.MessageAttachment("images/myimage.png", "aa");

var __result1 = msgattach1.id;
var __expect1 = null
var __result2 = msgattach1.messageId;
var __expect2 = null
var __result3 = msgattach1.mimeType;
var __expect3 = null
var __result4 = msgattach1.filePath;
var __expect4 = "images/myimage.png"

var __result5 = msgattach2.id;
var __expect5 = null
var __result6 = msgattach2.messageId;
var __expect6 = null
var __result7 = msgattach2.mimeType;
var __expect7 = "aa"
var __result8 = msgattach2.filePath;
var __expect8 = "images/myimage.png"
