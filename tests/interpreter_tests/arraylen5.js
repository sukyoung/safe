// 15.4.5.1 
var arr = [0,1,2,3,4];
arr.length = 3;	
for(var x = 0; x < 5; x++) {
	_<>_print(arr[x]);	// 0, 1, 2, undefined, undefined
}
arr.length; // 3
