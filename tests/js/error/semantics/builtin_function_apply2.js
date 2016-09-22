/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f(y,z) { return this.x+y+z; }

var __result1 = f.apply({x:1}, [2,3]);
var __expect1 = 6;

