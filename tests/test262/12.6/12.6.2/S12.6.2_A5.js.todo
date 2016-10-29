// TODO eval: statement (rewritten)
//__evaluated = eval("while(1) {__in__do__before__break=1; break; __in__do__after__break=2;}");
while(1) {__evaluated = __in__do__before__break=1;
 break;
 __in__do__after__break=2;
}

var __result1 = true;
if (__in__do__before__break !== 1) {
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if (typeof __in__do__after__break !== "undefined") {
    var __result2 = false;
}
var __expect2 = true;

var __result3 = true;
if (__evaluated !== 1) {
    var __result3 = false;
}
var __expect3 = true;
