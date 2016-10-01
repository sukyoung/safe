function f() { 
	return 10;
}

delete f;

var __result1 = f();
var __expect1 = 10;

function g() {
	function h1() {
		return "ABC";
	}

	function h2() { 
		return h1(); 
	}
	
	delete h1;
	return h2();
}

var __result2 = g();
var __expect2 = "ABC";
