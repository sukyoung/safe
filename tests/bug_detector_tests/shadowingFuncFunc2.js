function A() {
    // first f is shadowed
    function f() { }
    {
        function f() { }
    }
    f();
}

function B() {
    // first f is shadowed
    function f() { }
    if(Math.random())
    {
        function f() { }
    }
    f();
}

function C() {
    // first f is shadowed
    function f() { }
    while (Math.random()) {
        function f() { }
    }
    f();
}

A();
B();
C();
