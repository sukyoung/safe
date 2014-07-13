function p() { }
function f(p) {
    function g() { p; } // parameter win (not bug)
    g();
}

p();
f(1);