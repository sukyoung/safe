/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() {
    if (Math.random()) aaa = 123;
}
f();

var __result1 = this.aaa;
var __expect1 = 123;

var __result2 = this.aaa;
var __expect2 = undefined;
