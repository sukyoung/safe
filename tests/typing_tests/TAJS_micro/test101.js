var x;
if (Math.random()) {
	x = {a:RegExp};
} else {
	x = {b:Date};
}

//dumpValue(x.a);
var __result1 = x.a;  // for SAFE
var __expect1 = RegExp;  // for SAFE

var temp = x.a;
if (temp != null) {
//	assumeNonNullUndef("temp");
	if (temp != undefined) {  // for SAFE
//		dumpValue(temp); // expected: [REGEXP]  (not Undef!!!!)
		var __result2 = temp;  // for SAFE
	}
}
var __expect2 = RegExp;  // for SAFE
