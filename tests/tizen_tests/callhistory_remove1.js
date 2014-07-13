/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
function onSuccess(results) {
     if (results.length > 0)
         __result1 = tizen.callhistory.remove(results[0]);
 }

 // Defines error callback
 function onError(error) {

 }

 // Makes the query and wires up the callbacks
 tizen.callhistory.find(onSuccess, onError);

var __expect1 = undefined;
