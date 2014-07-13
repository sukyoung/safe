function f(x,y) {
	return x.g(y) + -x.w; 
}

var a = { g: function(x) { return x; }, w: 42, e: null }

var h = f;

var result = h(a, "hello");

//assert(result == "hello-42");
var __result1 = result;  // for SAFE
var __expect1 = "hello-42";  // for SAFE
