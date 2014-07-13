/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var v;
if (true)
	v = 3
else
	v = "abc"
	
var x = new Array(v);

var __result1 = x.length;
var __expect1 = 3;
var __result3 = x[0];
var __expect3 = undefined;

