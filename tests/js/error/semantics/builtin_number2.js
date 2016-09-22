/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = new Number(123);
var __result1 = x.valueOf();
var __expect1 = 123;

var y = new Number();
var __result2 = y.valueOf(); 
var __expect2 = 0;

var z = new Number(undefined);
var __result3 = z.valueOf();
var __expect3 = NaN;