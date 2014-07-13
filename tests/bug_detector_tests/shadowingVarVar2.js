function f() {
    // outer x is shadowed
    var x = 1;
    {
        var x = 2;
        x; // 2
    }
    // x is reused
    x; // 2
}

function g() {
    // outer x is shadowed
    var x = 1;
    if(Math.random())
    {
        var x = 2;
        x; // 2
    }
    // x is reused
    x; // 2
}

function h() {
    // outer x is shadowed
    var x = 1;
    while (Math.random()) {
        var x = 2;
        x; // 2
    }
    // x is reused
    x; // 2
}

f();
g();
h();
