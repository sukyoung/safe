/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

// return value must be stored in divided space for each procedure.
// However, because we store them into a space, the return value can be merged by finally block.
var x, y, z;

function aaa() {
  return true;
}

function bar() {
  try {
    return "20";
  } finally {
    z = aaa();
  }
}

function foo() {
  try {
    return 10;
  } finally {
    y = bar();
  }
}

x = foo();

var __result1 = x;
var __expect1 = 10;
var __result2 = y;
var __expect2 = "20";
var __result3 = z;
var __expect3 = true;

