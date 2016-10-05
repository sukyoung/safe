for(x in function __func(){return {a:1};}()){
    var __reached = x;
};

var __result1 = true;
if (__reached !== "a") {
    var __result1 = false;
}
var __expect1 = true;
