supreme=5;

var __result1 = true;
try {
    //var __evaluated =  eval("for(var count=0;;) {if (count===supreme)break;else count++; }");
    for(var count=0;;) {if (count===supreme)break;else var __evaluated = count++; }
    if (__evaluated !== 4) {
        var __result1 = false;
    }
} catch (e) {
    var __result1 = false;
}
var __expect1 = true;
