/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function Class1 () {};
function Class2 () {};
Class2.prototype = new Class1();

var x = new Class1();
var y = new Class2();

var __result1 = x instanceof Class1
var __expect1 = true

var __result2 = y instanceof Class2
var __expect2 = true

var __result3 = y instanceof Class1
var __expect3 = true
