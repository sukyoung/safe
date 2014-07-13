function foo(r, s) {
    r.f = 1;
    s.f = 2;
    if (r.f != 1) {
        if (x === 100) {
						_<>_print("1");
        } else {
						_<>_print("2");
        }
    } else {
				_<>_print("3");
    }
}

foo({}, {});
