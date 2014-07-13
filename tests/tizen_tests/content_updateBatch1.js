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
function findCB(contents) {
  // Increase the offset as much as the count and then find content again.
  contents[0].rating++;
  tizen.content.updateBatch([contents[0]], successCB, errorCB);
}
function errorCB(e){
  __result2 = e.name;
}


tizen.content.find(findCB, errorCB);

var __expect1 = 1;
var __expect2 = "UnknownError";
