// FAIL: fails because scope object of g is incorrectly localized.
// (scope object generated at the call edge is not propagated into g's entry).

function f() {
    var x = 123;
    function g() {
        function h() {
            return x;
        }
        return h;
    }
    return g;
}

var __result1 = f()()();
var __expect1 = 123;
