var o = {a:10, b:20, c:30};
o.getThis = function()
{
	_<>_print(this.a);
    return this;
}

o.print = function()
{
	_<>_print(this.a);
	_<>_print(this.b);
	_<>_print(this.c);
}

var t = o.getThis();
t.print();

"PASS"
