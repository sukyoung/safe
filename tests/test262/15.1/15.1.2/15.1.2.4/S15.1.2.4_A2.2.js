// Copyright 2009 the Sputnik authors.  All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/**
 * The length property of isNaN has the attribute DontDelete
 *
 * @path ch15/15.1/15.1.2/15.1.2.4/S15.1.2.4_A2.2.js
 * @description Checking use hasOwnProperty, delete
 * @noStrict
 */

var __result1 = isNaN.hasOwnProperty('length');
var __expect1 = true;

delete isNaN.length;
var __result2 = isNaN.hasOwnProperty('length');
var __expect2 = true;

var __result3 = isNaN.length === undefined;
var __expect3 = false;
