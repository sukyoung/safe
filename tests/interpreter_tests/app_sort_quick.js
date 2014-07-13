var arr = [4, 7, 1, 2, 9, 3, 8, 6, 0, 5];

// before
var str = "";
for(var i = 0;i < arr.length;i++) str+= arr[i] + " ";
_<>_print(str);

// quick sort
var stackBegin = [0], stackEnd = [arr.length - 1], stackCount = 1;
while(stackCount > 0)
{
	stackCount--;
	var pivot = stackBegin[stackCount], left1 = stackBegin[stackCount] + 1, right1 = stackEnd[stackCount];

	var left2 = left1, right2 = right1;
	while(true)
	{
		while(left2 <= right2 && arr[pivot] > arr[left2]) left2++;
		while(left2 <= right2 && arr[pivot] < arr[right2]) right2--;
		if(left2 <= right2)
		{
			var temp = arr[left2];
			arr[left2] = arr[right2];
			arr[right2] = temp;
		}
		else
		{
			var temp = arr[pivot];
			arr[pivot] = arr[right2];
			arr[right2] = temp;

			if(right2 - pivot > 1) stackBegin[stackCount] = pivot, stackEnd[stackCount] = right2 - 1, stackCount++;
			if(right1 - right2 > 1) stackBegin[stackCount] = left2, stackEnd[stackCount] = right1, stackCount++;

			break;
		}
	}
}

// after
str = "";
for(var i = 0;i < arr.length;i++) str+= arr[i] + " ";
_<>_print(str);

"PASS"
