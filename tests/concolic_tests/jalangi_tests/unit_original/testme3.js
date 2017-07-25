
var p = {f: 1, g: 2};

function foo(x) {
    if (x > 100) {
        if (x == 200) {
            p = {f: 2}
            _<>_print("1");
        } else {
            _<>_print("2");
        }
    } else {
        _<>_print("3");
    }
}

foo();

if (p.f == 2) {
    _<>_print("4");
}
_<>_print("5");

