var fact = 1;
var num = 5;
var str = "1";

for(var i = 2;i <= num;i++)
{
	str+= " * " + i;
	fact*= i;
}

str+= " = " + fact;
_<>_print(str);

"PASS"
