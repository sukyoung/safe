/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var __result1;
var __expect1 = undefined;
var __result2;
var __expect2;

try {
	__result2 = delete true;
} catch(e) {
	__result1 = e;
}

__expect2 = true;

