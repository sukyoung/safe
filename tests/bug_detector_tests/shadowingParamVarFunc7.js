function f(p) {
    function g() {
        var p; // var win
    }
    g();
}

f(1);
