var t = 10;

function func1()
{
	_<>_print("func1");
	func2(this);
	_<>_print("func1");
}

function func2(z)
{
	_<>_print("func2");
	_<>_print(z);
	_<>_print(z.t);
	_<>_print("func2");
}

func1();

"PASS";
