var o = {x:4407, y:4409, z:4410};
o.p = {x:1234, y:5678, z:9012};

with(o)
{
    with(p)
    {
	    delete p;
	    _<>_print(x);
	}
    _<>_print(x);
}

"PASS";
