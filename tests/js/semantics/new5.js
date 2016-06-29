/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function Foo(x) {
  this.x = x;
}
Foo.prototype.bar = function() {
  this.y = this.x;
}

var o = new Foo(10);
o.bar();

var __result1 = o.x;
var __expect1 = 10;
var __result2 = o.y;
var __expect2 = 10;

