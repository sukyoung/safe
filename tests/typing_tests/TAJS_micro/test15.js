function f(x) {
	//dumpState();
	return x + 1;
}

var q = f(7);

//assert(q == 8);
//dumpValue(q);
var __result1 = q;  // for SAFE
var __expect1 = 8;  // for SAFE

var obj = {aaa:7}
//assert(obj.aaa === 7);
//dumpValue(obj.aaa);
var __result2 = obj.aaa;  // for SAFE
var __expect2 = 7;  // for SAFE
