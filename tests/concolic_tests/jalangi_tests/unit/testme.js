var r = 9.1;
var p = {f: 1};

function foo(x) {
    if (x > 100) {
        if (x == 200) {
            r = 0.3;
						print("1");
        } else {
            r = 4.2;
						print("2");
        }
    }
}

function bar(x, y) {
    if (y > 0)
        if (y == x) {
            p.f = 2;
						print("3");
        } else {
						print("4");
        }
    else {
				print("5");
    }
}

foo(0);
bar(0,0);

