/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o = {p:1, pp:2}

var x = 0;
// toObject(o)
for (y in o)
	x += o[y]

var __result1 = x;
var __expect1 = 3;