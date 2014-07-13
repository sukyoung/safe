var o = {x:4407, y:4409, z:4410};

with(o)
{
	_<>_print(x); // "undefined" => "4407"
	delete x;
}
_<>_print(o.x); // "4407" => "undefined"

"PASS";
