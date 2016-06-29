/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() {
	function ff() {return 1}
	return ff()};

var __result1 = f();
var __expect1 = 1;