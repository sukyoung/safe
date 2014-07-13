var temp = 10;
function f() {temp++; return temp;}
function g(x,y) {}

try {
    g(y, f())
} catch (e) {}

_<>_print(temp)

"PASS";
