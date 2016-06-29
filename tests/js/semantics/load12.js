/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function Class1() {this.x = "str"}
function Class2() {this.y = 2}
Class2.prototype = new Class1(); 

var o = new Class2();

var __result1 = o.x;
var __expect1 = "str";

var __result2 = o.y;
var __expect2 = 2;

var __result3 = o["constructor"];
var __expect3 = Class1;
