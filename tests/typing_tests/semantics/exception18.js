/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x, z;

try {
    x = 1;
    try {
        y;
        x = 2;
    } finally {
        x = 3;
    }
} catch (e) {
    z = "A";
} finally {
    z = "B";
}

var __result1 = z;
var __expect1 = "B";