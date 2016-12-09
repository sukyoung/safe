var foo;

var __result1;
var __expect1 = 123;

function f() {
    foo = g;
    main();
}

function g() {
    var x = 123;
    function capture() {
        return x;
    }
    main();
    __result1 = x;
}

foo = f;

function main() {
    if (@Top) foo();
}

main();
