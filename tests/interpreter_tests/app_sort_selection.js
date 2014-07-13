var arr = [4, 7, 1, 2, 9, 3, 8, 6, 0, 5];

// before
var str = "";
for(var i = 0;i < arr.length;i++) str+= arr[i] + " ";
_<>_print(str);

// selection sort
for(var i = 0;i < arr.length - 1;i++)
{
	var index = i;
	for(var j = i + 1;j < arr.length;j++)
	{
		if(arr[index] > arr[j]) index = j;
	}
	if(index != i)
	{
		var temp = arr[i];
		arr[i] = arr[index];
		arr[index] = temp;
	}
}

// after
str = "";
for(var i = 0;i < arr.length;i++) str+= arr[i] + " ";
_<>_print(str);

"PASS"
