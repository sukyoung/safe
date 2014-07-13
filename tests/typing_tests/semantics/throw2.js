/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var e = 0;
try {
  throw 1;
} catch(e) {
  e = 2;
}

var __result1 = e;
var __expect1 = 0;
