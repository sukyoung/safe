/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {a:1, b:2, c:3}

var names = Object.getOwnPropertyNames(o);

// order??
var __result1 = names[0];
var __expect1 = "a";

var __result2 = names[1];
var __expect2 = "b";

var __result3 = names[2];
var __expect3 = "c";