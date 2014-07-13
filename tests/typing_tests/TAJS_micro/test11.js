function foo(x,y) {
	return arguments[0] + arguments["1"] + arguments[2];
}
function foo2(x,y) {
	return arguments[0] + arguments["1"] + arguments[2];
}
function foo3(x,y) {
	return arguments[0] + arguments["1"] + arguments[2];
}

function foo4(x,y) {
	return arguments[0] + arguments["1"] + arguments[2];
}

var qqq1 = foo(101, 102, 103);
//dumpValue(qqq1);
//assert(qqq1 == 306);
var __result1 = qqq1;  // for SAFE
var __expect1 = 306;  // for SAFE

var qqq2 = foo("x101", "x102", "x103");
//dumpValue(qqq2);
//assert(qqq2 == "x101x102x103");
var __result2 = qqq2;  // for SAFE
var __expect2 = "x101x102x103";  // for SAFE

var qqq22 = foo2("x101", "x102", "x103");
//dumpValue(qqq22);
//assert(qqq22 == "x101x102x103");
var __result3 = qqq22;  // for SAFE
var __expect3 = "x101x102x103";  // for SAFE

var qqq3 = foo3(101, 102, 103, 104);
//dumpValue(qqq3);
//assert(qqq3 == 306);
var __result4 = qqq3;  // for SAFE
var __expect4 = 306;  // for SAFE

var qqq4 = foo4(101, 102);
//dumpValue(qqq4);
//assert(isNaN(qqq4));
//dumpValue(isNaN(qqq4));
var __result5 = qqq4;  // for SAFE
var __expect5 = NaN;  // for SAFE

function bar(x,y) {
	this.qwerty = arguments[0] + arguments[1] + arguments[2];
}

var qqq1 = new bar(101, 102, 103);
//dumpValue(qqq1.qwerty);
var __result6 = qqq1.qwerty;  // for SAFE
var __expect6 = 306.0;  // for SAFE
