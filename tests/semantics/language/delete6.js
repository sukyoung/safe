if (@Top) {
    x = 1;
}

var __result1 = delete x;
var __expect1 = true;

var __result2 = "bot";
var __result3;
try {
    __result2 = x;
} catch(e) {
    __result3 = e instanceof @RefErr;
}
var __expect2 = "bot";
var __expect3 = true;
