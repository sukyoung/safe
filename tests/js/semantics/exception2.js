/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o1 = {};
var o2 = {};
try {
	o1 instanceof o2;
}
catch (e) {
	var __result1 = e.name;
	var __expect1 = "TypeError"
}