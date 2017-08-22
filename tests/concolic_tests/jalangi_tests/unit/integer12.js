function testme(a, b, c) {

    _<>_print("a, b, c = "+a+", "+b+", "+c);

    var x = 0, y = 0, z = 0;

    if (a) {
        x = -2;
    }
    if (b < 5) {
        if (!a && c) {
            y = 1;
            if (a) {
                y = 2*y;
            }
        }
        z = 2;
    }

    if (x + y + z === 3) {
        _<>_print("Sum is 3");
    } else {
        _<>_print("Sum is not 3");
    }
}

// testme (0, 0, 0);
// testme (0, 5, 0);
// ...

testme(1,1,1);

