/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f(x,y) { return x+y; }

var __result1 = f.apply(this, [1,2]);
var __expect1 = 3;

