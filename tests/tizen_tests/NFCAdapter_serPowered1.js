/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapt;
function successCB(){
  __result1 = adapt.powered;
}
function errorCB(err){
  __result2 = err.name;
}
adapt = tizen.nfc.getDefaultAdapter();
adapt.setPowered(true, successCB, errorCB);

var __expect1 = true;
var __expect2 = "UnknownError"
