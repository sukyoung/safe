var x = {a:42};
//dumpValue(delete x.a); // true
var __result1 = delete x.a;  // for SAFE
var __expect1 = true;  // for SAFE

//dumpValue(x.a); // Undef
var __result2 = x.a;  // for SAFE
var __expect2 = undefined;  // for SAFE

//dumpValue(delete x.b); // true
var __result3 = delete x.b;  // for SAFE
var __expect3 = true;  // for SAFE

//dumpValue(x.b); // Undef
var __result4 = x.b;  // for SAFE
var __expect4 = undefined;  // for SAFE

//dumpValue(delete x); // false, cannot delete because property has [[DontDelete]]
var __result5 = delete x;  // for SAFE
var __expect5 = false;  // for SAFE

//dumpValue(x);
var __result6 = x instanceof Object;  // for SAFE
var __expect6 = true;  // for SAFE

y = 7;
//dumpValue(delete y); // true
var __result7 = delete y;  // for SAFE
var __expect7 = true;  // for SAFE

// dumpValue(y); // ReferenceError
try {  // for SAFE
	y;  // for SAFE
} catch (e) {  // for SAFE
	__result8 = e.name;  // for SAFE
}
__expect8 = "ReferenceError";  // for SAFE

(function() {
	var x = 7;
//	dumpValue(delete x); // false, cannot delete because property has [[DontDelete]]
	__result9 = delete x;  // for SAFE

//	dumpValue(x); // not deleted
	__result10 = x;  // for SAFE

	z = 8;
//	dumpValue(delete z); // true
	__result11 = delete z;  // for SAFE

	// dumpValue(z); // ReferenceError
	try {  // for SAFE
		z;  // for SAFE
	} catch (e) {  // for SAFE
		__result12 = e.name;  // for SAFE
	}
})();
__expect9 = false;  // for SAFE
__expect10 = 7;  // for SAFE
__expect11 = true;  // for SAFE
__expect12 = "ReferenceError";  // for SAFE

//dumpValue(delete q); // true
var __result13 = delete q;  // for SAFE
var __expect13 = true;  // for SAFE

function f() {}
//dumpValue(delete f); // false, cannot delete because property has [[DontDelete]]
var __result14 = delete f;  // for SAFE
var __expect14 = false;  // for SAFE

