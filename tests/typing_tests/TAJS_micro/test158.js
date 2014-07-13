var o = {x: 3}

var __result1;  // for SAFE
var __result2;  // for SAFE

if (Math.random() > 0.4) {
//	dumpValue(o.x)
	__result1 = o.x;  // for SAFE
	__result2 = o.x;  // for SAFE
}
var __expect1 = 3;  // for SAFE
var __expect2 = undefined;  // for SAFE

o.pro = "seje"
if (Math.random() > 0.4)
	delete o.pro
//dumpValue(o)
//dumpValue(o.pro)
var __result3 = o.pro;  // for SAFE
var __expect3 = "seje";  // for SAFE

var __result4 = o.pro;  // for SAFE
var __expect4 = undefined;  // for SAFE

o.dead = "i am so dead"