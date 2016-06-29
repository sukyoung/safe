/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function Foo(x) {
  this.x = x;
}

var o = new Foo(10);

var __result1 = o.x;
var __expect1 = 10;

