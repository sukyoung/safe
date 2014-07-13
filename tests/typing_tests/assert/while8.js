/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x = {a:1}
while("a" in x) {
  delete x.a;
}

var __result1 = x.a;
var __expect1 = undefined; // 1, undefined

var __result3 = "a" in x;
var __expect3 = false; // __BoolTop;

