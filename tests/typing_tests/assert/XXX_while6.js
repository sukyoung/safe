/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x = {a:1}
var i = 0;
while("a" in x) {
  if(i > 10) {
     x[i] = i;
  } else { delete x.a }
}

var __result1 = "a" in x;
var __expect1 = false; //__BoolTop

var __result2 = i;
var __expect2 = 0;

var __result3 = x[100];
var __expect3 = undefined;

var __result4 = x[0];
var __expect4 = undefined; // undefined, 0

var __result5 = x[1];
var __expect5 = undefined;

var __result6 = x[2];
var __expect6 = undefined;
