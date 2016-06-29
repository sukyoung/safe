/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

// As same as that of 'return6.js', throw value also be stored in a separated space for each procedure.
var x;
var y = "";
var z;
try {
  try {
    throw 1;
  } finally {
	try {
      try {
        throw true;
      } finally {
        try {
          throw "2";
        } catch(e) {
          y = e;
        }
      }
    } catch(e) {
      z = e;
	}
  }
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;
var __result2 = y;
var __expect2 = "2";
var __result3 = z;
var __expect3 = true;

