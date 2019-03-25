var o = {p1:1, p2:2};

var x = Object.keys(o);

// order is implementation dependent. 
var __result1 = x[0];
var __expect1 = "p1";

var __result2 = x[1];
var __expect2 = "p2";
