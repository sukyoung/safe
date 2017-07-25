var r = 0;

function foo(a) {
    if (a > 100) {
        if (a == 200) {
            r++;
            print("1");
        } else {
            print("2");
        }
    } else {
        print("3");
    }
}

foo(0);

print("4");
