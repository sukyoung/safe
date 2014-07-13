t = {i : 21}

foo = function() {
    t.i = t.i - 1;
    if (t.i > 0) {
        bar();
    }
}

bar = function() {
    foo()
}

foo();

//dumpValue(t.i)
var __result1 = t.i;  // for SAFE
var __expect1 = 0;  // for SAFE
