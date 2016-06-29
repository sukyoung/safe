/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function Class1() {this.x = 1;}
function Class2() {};
Class2.prototype = new Class1();

var o = new Class2();

var __result1 = "x" in o; 
var __expect1 = true;
