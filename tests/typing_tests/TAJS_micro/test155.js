f = function() {
    a = {};
};

g = function() {
    b = a;
    c = b.p + 1;
};

f();
a.p = 42;
g(); 
//dumpValue(a.p);
var __result1 = a.p;  // for SAFE
var __expect1 = 42;  // for SAFE

//dumpValue(c);
var __result2 = c;  // for SAFE
var __expect2 = 43;  // for SAFE
