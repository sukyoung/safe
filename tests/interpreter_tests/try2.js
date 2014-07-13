function test1()
{
	try
	{
		test2();
	}
	catch(e)
	{
		_<>_print(e);
	}
	finally
	{
		_<>_print("test1()");
	}
}

function test2()
{
	throw "abc";
}

try
{
	test1();
}
catch(e)
{
	_<>_print(e);
}
finally
{
	_<>_print("global");
}

"PASS";
