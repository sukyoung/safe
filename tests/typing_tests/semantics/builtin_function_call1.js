/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f(x) { return x+1; }

var __result1 = f.call(undefined,10);
var __expect1 = 11;

