/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var obj = {p1:10};
function Class() {this.p1 = 20};
Class.prototype = obj; 

var x = new Class();

var __result1 = x.p1; 
var __expect1 = 20;

var __result2 = Object.getPrototypeOf(x).p1;
var __expect2 = 10;
