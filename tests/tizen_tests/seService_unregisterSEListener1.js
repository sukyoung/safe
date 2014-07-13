/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var seListener = tizen.seService.registerSEListener({
   onSEReady:function(reader) {
    }, onSENotReady:function(reader) {
    }});

var __result1 = tizen.seService.unregisterSEListener(seListener);
var __expect1 = undefined;
