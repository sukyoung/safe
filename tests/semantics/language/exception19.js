function f() { throw "x" }

var __result1, __result2;
var y;
try {
    if(true) {
        y = 2;
        f();
        y = 3;
    }
} catch(e) {
    __result1 = y;
    __result2 = e;
}

var __expect1 = 2;
var __expect2 = "x";

