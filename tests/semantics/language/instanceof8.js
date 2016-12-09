var __result1;
var __result2;

var o;
if (@Top) {
    o = {};
} else {
    o = function() {};
}

try {
    __result1 = 1 instanceof o;
} catch(e) {
    __result2 = e instanceof @TypeErr;
}

var __expect1 = false;
var __expect2 = true;
