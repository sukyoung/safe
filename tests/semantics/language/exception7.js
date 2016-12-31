var __result1;
var __expect1 = true;

function foo() {
    return x;
}

try {
    foo();
} catch(e) {
    __result1 = e instanceof @RefErr;
}
