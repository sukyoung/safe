out:
{
    var a = 10;
    switch(a)
    {
    case 10:
        break out;
    default:
        a = 20;
    }
    a = 30;
}

_<>_print(a);

"PASS"
