var o = {};

if (@Top) {
	o.x = 1;
}

var __result1 = delete o.x;
var __expect1 = true;

var __result2 = o.x;
var __expect2 = undefined;
