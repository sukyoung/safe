function A() {
    // variable f is shadowed
    var f;
    {
        function f() { }
    }
    f();
}

function B() {
    // variable f is shadowed
    var f;
    if(Math.random()) {
        function f() { }
    }
    f();
}

function C() {
    // variable f is shadowed
    var f;
    while (Math.random()) {
        function f() { }
    }
    f();
}

A();
B();
C();
