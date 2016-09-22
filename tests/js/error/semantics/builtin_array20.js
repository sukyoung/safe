/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

// FAIL: isArray is not working correctly for primitive values.

var __result1 = Array.isArray(3);
var __expect1 = false;
