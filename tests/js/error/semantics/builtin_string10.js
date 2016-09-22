/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = "12345";

var __result1 = x.concat()
var __expect1 = "12345"

var __result2 = x.concat("6", "7", 8, 9)
var __expect2 = "123456789"
