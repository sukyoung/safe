function f() {
    {
        var x = 1;
    }
    {
        var x = 2;
    }
    // x is reused
    x; // 2
}

function g() {
    {
        var x = 1;
    }
    {
        var x = 2;
    }
    // x is not reused
}

f();
g();
