var __result1;
var __expect1 = true;

try {
    undefined[1] = 0;
} catch(e) {
    __result1 = e instanceof @TypeErr;
}
