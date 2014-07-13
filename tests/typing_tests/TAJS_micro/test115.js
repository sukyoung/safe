var __expect1 = "HERE";  // for SAFE
try {  // for SAFE
	var x;
	if (Math.random())
	 x = this;
	else
	 x = function() {};
	x();
	var __result1 = "HERE";  // for SAFE
} catch(e1) {  // for SAFE
	var __result2 = e1.name;  // for SAFE
}  // for SAFE
var __expect2 = "TypeError";  // for SAFE

var __expect3 = "HERE";  // for SAFE
try {  // for SAFE
	if (Math.random())
	 y = null;
	else
	 y = {};

	y.a;
	var __result3 = "HERE";  // for SAFE
} catch (e2) {  // for SAFE  
	var __result4 = e2.name  // for SAFE
} 
var __expect4 = "TypeError"  // for SAFE
