/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 0;

function foo() {
  throw 1;
}

try {
  foo();
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;
