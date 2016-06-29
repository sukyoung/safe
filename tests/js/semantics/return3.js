/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function foo() {
  try {
    return 10;
  } finally {
    return 20;
  }
}

var x = foo();

var __result1 = x;
var __expect1 = 20;

