/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1};

// o is prototype of x
var x = Object.create(o);

var __result1 = o.isPrototypeOf(x);
var __expect1 = true;
