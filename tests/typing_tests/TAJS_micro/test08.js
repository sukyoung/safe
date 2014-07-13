function person() {
    glob = 234;
    this.age = 10;
}

function test() {
    return 42;
}
glob = 9;
var g = test();
var t = new person();
g = t.age;

//assert(g == 10);
var __result1 = g;  // for SAFE
var __expect1 = 10;  // for SAFE

//assert(glob == 234);
var __result2 = glob;  // for SAFE
var __expect2 = 234;  // for SAFE
