/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() { return this; }
var x = f.call(10);

var __result1 = x.valueOf();
var __expect1 = 10;
