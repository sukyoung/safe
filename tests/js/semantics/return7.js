var x = 10;
function f() { }
function g() {
    try {
        return 20;
    } finally {
        x = f();
    }
}

g();

var __result1 = x;
var __expect1 = undefined;
