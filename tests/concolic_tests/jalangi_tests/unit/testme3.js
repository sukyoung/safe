
var p = {f: 1, g: 2};

function foo(x) {
    if (x > 100) {
        if (x == 200) {
            p = {f: 2}
            print("1");
        } else {
            print("2");
        }
    } else {
        print("3");
    }
}

foo();

if (p.f == 2) {
    print("4");
}
print("5");

