/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result2, __result3;
function onSuccess(){
  __result2 = 1;
}
function onError(er){
  __result3 = er.name;
}

var __result1 = tizen.callhistory.removeAll(onSuccess, onError);

var __expect1 = undefined;
var __expect2 = 1;
var __expect3 = "UnknownError";
