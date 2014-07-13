function func1(a, b, c)
{
	_<>_print(delete arguments); // "#4" -> "false"
	_<>_print(arguments); // "#4" -> "[object Object]"
}

func1("a", "b", "c", "d", "e");

"PASS";
