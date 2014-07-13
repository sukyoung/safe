/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = {};

function f() { return x; }

x.a = 123;
f();

x.a = "ABC";
f();

var __result1 = x.a;
var __expect1 = "ABC";
