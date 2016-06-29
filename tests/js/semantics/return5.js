/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var x;

function foo() {
  try {
    return 10;
  } finally {
    return;
  }
  return 20;
}

x = foo();

var __result1 = x;
var __expect1 = undefined;

