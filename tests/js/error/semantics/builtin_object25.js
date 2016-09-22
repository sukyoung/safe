/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

// FAIL: Object.getOwnPropertyDescriptor should return undefined if no property was found.

var obj = { };

var __result1 = Object.getOwnPropertyDescriptor(obj, "foo");
var __expect1 = undefined;
