/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var obj = {p1:10};
function Class1() {this.p1 = 20};
Class1.prototype = obj; 

function Class2() {this.p1 = 30};
Class2.prototype = new Class1();

var x = new Class2();

var __result1 = x.p1; 
var __expect1 = 30;

var __result2 = Object.getPrototypeOf(x).p1;
var __expect2 = 20;

var __result3 = Object.getPrototypeOf(Object.getPrototypeOf(x)).p1;
var __expect3 = 10;
