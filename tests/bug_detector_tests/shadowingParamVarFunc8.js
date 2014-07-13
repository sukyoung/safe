function f(p) {
    function g() {
        function p() { } // function win
        p();
    }
    g();
}

f(1);
