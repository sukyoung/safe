function func1()
{
	_<>_print("func1");
	return "func1";
}

function func2()
{
	_<>_print("func2");
	return "func2";
}

func1();
// This program is terminated right after that the func1() returns.
// The codes below are not executed.
func2();

"PASS";
