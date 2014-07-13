var x;
function f(x) {
    function g() { x; } // parameter win (not bug)
    g();
}

f(1);
