var x = 42;
y = 43;
this.z = 44;

function f() {
	this.q = 45;
	return this;
}
var global = f();

//dumpValue(global.x);
var __result1 = global.x;  // for SAFE
var __expect1 = 42;  // for SAFE

//dumpValue(global.y);
var __result2 = global.y;  // for SAFE
var __expect2 = 43;  // for SAFE

//dumpValue(global.z);
var __result3 = global.z;  // for SAFE
var __expect3 = 44;  // for SAFE

//dumpValue(global.q);
var __result4 = global.q;  // for SAFE
var __expect4 = 45;  // for SAFE

//dumpValue(global.f);
var __result5 = global.f;  // for SAFE
var __expect5 = f;  // for SAFE

//dumpValue(global.global);
var __result6 = global.global;  // for SAFE
var __expect6 = this;  // for SAFE

//dumpValue(global);
var __result7 = global;  // for SAFE
var __expect7 = this;  // for SAFE
