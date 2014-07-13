var b = false;

function f() {y = 42;}

if (Math.random()) {
  f();
}

//dumpValue(y);
var __result1 = y;  // for SAFE
var __expect1 = 42;  // for SAFE

try {  // for SAFE
	y;  // for SAFE
} catch(e) {  // for SAFE
	__result2 = e.name;  // for SAFE
}
__expect2 = "ReferenceError";  // for SAFE