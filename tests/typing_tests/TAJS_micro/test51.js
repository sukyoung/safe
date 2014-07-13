var empty = {}
var q = "this should remain"
for (t in empty)
    q = t;
//dumpValue(q)
var __result1 = q;  // for SAFE
var __expect1 = "this should remain";  // for SAFE
