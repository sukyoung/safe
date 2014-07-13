/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;


var seListener = tizen.seService.registerSEListener({
   onSEReady:function(reader) {
     __result1 = reader.isPresent;
    }, onSENotReady:function(reader) {
     __result2 = reader.isPresent;
    }});

var __result3 = seListener;

var __expect1 = true
var __expect2 = true
var __expect3 = 1;