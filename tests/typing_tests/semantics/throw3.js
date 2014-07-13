/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var x = 0;
try {
  throw 1;
} catch(e) {
  try {
    throw "2";
  } catch(e) {
    x = e;
  }
  x = e;
}

var __result1 = x;
var __expect1 = 1;

