/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function bar() {
  return foo();
}

function foo() {
  return 10;
}

var x = bar();

var __result1 = x;
var __expect1 = 10;

