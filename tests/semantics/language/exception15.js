var __result1;
var __expect1 = true;

try {
    true[1] = x;
} catch(e) {
    __result1 = e instanceof @RefErr;
}

