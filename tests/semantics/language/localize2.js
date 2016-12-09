var obj;
function foo(x) { obj = x; }

var __result1;
var __expect1 = 123;

var __result2;
var __expect2 = "ABC";

if (@Top) {
    foo({p: 123});
    __result1 = obj.p;
} else {
    foo({p: "ABC"});
    __result2 = obj.p;
}
