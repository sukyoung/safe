/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onSuccess(){
  __result1 = 1;
}
function onError(e){
  __result2 = e.name;
}
tizen.application.launch("org.tizen.calculator", onSuccess, onError);

var __expect1 = 1;
var __expect2 = "UnknownError"