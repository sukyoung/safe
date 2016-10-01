var o = {};

if (__TOP)
	o.x = 123;
else
	o.y = 456;

var __result1 = "x" in o
var __expect1 = __BoolTop
