/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3];
var y = [1,2,3,4];

x.reverse();
y.reverse();

var __result1 = x.toString();
var __expect1 = "3,2,1"

var __result2 = y.toString();
var __expect2 = "4,3,2,1";
