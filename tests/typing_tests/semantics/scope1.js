/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

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
    if (Math.random()) foo();
}

main();
