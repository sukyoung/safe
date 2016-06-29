/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = {};

if (__TOP)
	o.x = 123;
else
	o.y = 456;

var __result1 = "x" in o
var __expect1 = __BoolTop
