var z = {}
//dumpValue(z);

var x = { abc: 99999, def: "dfgcfdc", qqqqq: null, wwwww: undefined, e: true, rrrr: z };
//dumpValue(x.qqqqq);
var __result1 = x.qqqqq;  // for SAFE
var __expect1 = null;  // for SAFE

//dumpValue(x.rrrr);
var __result2 = x.rrrr;  // for SAFE
var __expect2 = z;  // for SAFE

//dumpValue(x.e);
var __result3 = x.e;  // for SAFE
var __expect3 = true;  // for SAFE

//dumpValue(x.abc);
var __result4 = x.abc;  // for SAFE
var __expect4 = 99999.0;  // for SAFE

//dumpValue(x.def);
var __result5 = x.def;  // for SAFE
var __expect5 = "dfgcfdc";  // for SAFE

var x = {foo: 12345};
//dumpValue(x.foo + 5678);
var __result6 = x.foo + 5678;  // for SAFE
var __expect6 = 18023.0;  // for SAFE


var bar = function() {
	return 777;
}
var foo = bar() * 1234;
//dumpValue(foo);
var __result7 = foo;  // for SAFE
var __expect7 = 958818.0;  // for SAFE

var q = (function(x) {
	return x + 777;
})(1234) * 5678;
//dumpValue("x" + q);
var __result8 = "x" + q;  // for SAFE
var __expect8 =  "x11418458";  // for SAFE

var bar = {def: 88888}.def * 45678;
//dumpValue(bar);
var __result9 = bar;  // for SAFE
var __expect9 = 4.060226064E9;  // for SAFE

var xyz = { abc: 99999 }
//dumpValue(xyz.abc);
var __result10 = xyz.abc;  // for SAFE
var __expect10 = 99999.0;  // for SAFE
