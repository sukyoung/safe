/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var func = [];
for(var i = 0; i < 10; i++) {
	func[i] = function(){ };
	if (i>0) {
		func[i].prototype = new func[i-1];
    }
} 
var o = new func[9];

var __result1 = o.x;
var __expect1 = undefined;
