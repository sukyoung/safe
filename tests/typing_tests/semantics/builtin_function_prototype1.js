/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

// FAIL: Function.prototype should be a the following function.
// ECMA 5th 15.3.4:
// "The Function prototype object is itself a Function object
//  that, when invoked, accepts any arguments and returns undefined."

var __result1 = Function.prototype();
var __expect1 = undefined;
