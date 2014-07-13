function func1(a, b, c)
{
	_<>_print(a);
	_<>_print(b);
	_<>_print(c);
	_<>_print(arguments[0]);
	_<>_print(arguments[1]);
	_<>_print(arguments[2]);
	_<>_print(arguments[3]);
	_<>_print(arguments[4]);
	_<>_print(arguments[5]);
	_<>_print(arguments.length);
}

func1("a", "b", "c", "d", "e");

"PASS";
