function test1()
{
    _<>_print("A");
    return test2;
}

function test2()
{
    _<>_print("B");
    return test1;
}

test1()()()()();

"PASS"
