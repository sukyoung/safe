var r = 0;

function foo(a) {
    if (a > 100) {
        if (a == 200) {
            r++;
            _<>_print("1");
        } else {
            _<>_print("2");
        }
    } else {
        _<>_print("3");
    }
}

foo(0);

_<>_print("4");
