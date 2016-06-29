/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o1;
var o2;

var __result1 = o1 == o2;
var __expect1 = true;

var __result2 = o1 === o2;
var __expect2 = true;

var __result3 = o1 != o2;
var __expect3 = false;

var __result4 = o1 !== o2;
var __expect4 = false;