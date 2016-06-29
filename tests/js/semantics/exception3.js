/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function Class() {this.p = 1}
Class.prototype = 1;

var o = new Class();

try {
	o instanceof Class;
}
catch (e) {
	var __result1 = e.name;
	var __expect1 = "TypeError"
}