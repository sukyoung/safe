/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var obj = {};

var __result1;
if (obj.foo == null) {
    __result1 = 123;
} else {
    __result1 = "ABC";
}

var __expect1 = 123;
