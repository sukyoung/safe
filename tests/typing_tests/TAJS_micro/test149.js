var t;
var x;

var f = function() {
   t.next = x;	
}

x = null;
t = {}; // 14
f();
x = t;

t = {}; // 20
f();
x = t;

t = {}; // 26
f();
x = t;

t = {}; // 32
f();
x = t;

t = {}; // 38
f();
x = t;

var __result1 = (x !== null);  // for SAFE
var __expect1 = true;  // for SAFE

var __result2 = (x.next !== null);  // for SAFE
var __expect2 = true;  // for SAFE

var __result3 = (x.next.next !== null);  // for SAFE
var __expect3 = true;  // for SAFE

var __result4 = (x.next.next.next !== null);  // for SAFE
var __expect4 = true;  // for SAFE

var __result5 = (x.next.next.next.next !== null);  // for SAFE
var __expect5 = true;  // for SAFE

var __result6 = (x.next.next.next.next.next !== null);  // for SAFE
var __expect6 = false;  // for SAFE
