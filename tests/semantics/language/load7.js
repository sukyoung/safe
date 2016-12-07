var o1 = {p1:123};
var o2 = {p1:456};
var o3;

if (@Top) {
	o3 = o1; 
} else {
	o3 = o2;
}

var __result1 = o3.p1; 
var __expect1 = 123;

var __result2 = o3.p1; 
var __expect2 = 456;
