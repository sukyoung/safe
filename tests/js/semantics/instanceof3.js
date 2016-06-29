/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;

if (__TOP)
	o = Object;
else
	o = Array;

var __result1 = ({}) instanceof o
var __expect1 = __BoolTop

var __result2 = ([]) instanceof o
var __expect2 = true
