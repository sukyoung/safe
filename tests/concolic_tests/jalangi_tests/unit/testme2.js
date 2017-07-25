function foo(r, s) {
    r.f = 1;
    s.f = 2;
    if (r.f != 1) {
        if (x === 100) {
						print("1");
        } else {
						print("2");
        }
    } else {
				print("3");
    }
}

foo({}, {});
