var __result1;
var __expect1 = true;

try {
    var o = {};
    o();
} catch(e) {
    __result1 = e instanceof @TypeErr;
}
