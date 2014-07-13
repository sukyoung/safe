var a = "foo";
var b = 1000;
var bb = 1000 + 42;
var ab = a + b;
//dumpValue(a);
//dumpValue(ab);
//dumpValue(bb);

//assert(a == "foo");
var __result1 = a;  // for SAFE
var __expect1 = "foo"  // for SAFE

//assert(ab == "foo1000");
var __result2 = ab;  // for SAFE
var __expect2 = "foo1000"  // for SAFE 

//assert(bb == 1042);
var __result3 = bb;  // for SAFE
var __expect3 = 1042;  // for SAFE

//dumpValue(this);
