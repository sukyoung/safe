/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = {p1: 1};
var y = {p1: x};

var __result1 = y.p1.p1
var __expect1 = 1
