/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var x, y;

function bar() {
  return "20";
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
