/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function successCB(){
  __result1 = 1;
}
function errCB(er){
  __result2 = er.name;
}

function onSuccess(results) {
     if (results.length > 0)
         tizen.callhistory.removeBatch(results, successCB, errCB);
}

// Defines error callback
function onError(error) {

}

 // Makes the query and wires up the callbacks
tizen.callhistory.find(onSuccess, onError);

var __expect1 = 1;
var __expect2 = "UnknownError";
