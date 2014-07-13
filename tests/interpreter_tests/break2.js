for(var i = 0;i < 3;i++)
{
label_j:
	for(var j = 0;j < 3;j++)
	{
		for(var k = 0;k < 3;k++)
		{
			if(i < j) break label_j;
			_<>_print(i + ", " + j + ", " + k);
		}
	}
}

"PASS"
