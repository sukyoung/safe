function f(x) {
    if (x <= 0)
        return 1;
    else
        return x * f(x - 1);
}

if (f(0) != 1) throw 0;
if (f(1) != 1) throw 1;
if (f(2) != 2) throw 2;
if (f(3) != 6) throw 3;
if (f(4) != 24) throw 4;
if (f(5) != 120) throw 5;
if (f(6) != 720) throw 6;

"PASS"
