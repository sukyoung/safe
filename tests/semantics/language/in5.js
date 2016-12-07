var o1 = {};
var o2 = {x:123};
var o3 = {x:123, y:456};
var p;

if (@Top)
	p = "x" 
else
	p = "y"

var __result1 = p in o1;
var __expect1 = false;

var __result2 = p in o2;
var __expect2 = true;
var __result3 = p in o2;
var __expect3 = false;


var __result4 = p in o3;
var __expect4 = true;
