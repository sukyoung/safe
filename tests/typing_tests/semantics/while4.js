/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var a = [1,2];

var sum = 0;

for (var x in a) {
	sum += a[x];
}

var __result1 = sum;
var __expect1 = 3;