/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function Foo() {
  this.x = 10;
}

var o = new Foo();

var __result1 = o.x;
var __expect1 = 10;

