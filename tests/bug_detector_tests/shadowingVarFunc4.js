function f() {
    function g() {
        var f; // var win
        f;
    }
    g();
}

f();
