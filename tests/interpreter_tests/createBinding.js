function print(str)
{
    _<>_print(str + "\n");
}

function f(x)
{
    var w;

    print("(delete arguments) = " + (delete arguments));
    print("(delete x) = " + (delete x));
    print("(delete w) = " + (delete w));

    return x;
};
f(10);

var y = 10;
print("(delete y) = " + (delete y));

z = 10;
print("(delete z) = " + (delete z));
