var o = {p:1, pp:2}

var x = 0;
// toObject(o)
for (y in o)
	x += o[y]

var __result1 = x;
var __expect1 = 3;