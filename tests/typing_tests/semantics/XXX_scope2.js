/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

// FAIL: fails because scope object of g is incorrectly localized.
// (scope object generated at the call edge is not propagated into g's entry).

function f(a) {
    var x = a;
    function g() { 
        function h() {
            return x;
        }
        return h;
    }
    g1 = g;
    return null;
};

f(123);

var foo;
if (Math.random()) foo = g1; else foo = f;

var h1 = foo("ABC");

var __result1 = h1();
var __expect1 = 123;
