// 15.4.5.1 ToUint32(length) != ToNumber(length)
var arr = [0,1,2,3,4];
arr[true] = 1;
_<>_print(arr.length); // 5
_<>_print(arr[4294967295]); // undefined
arr[-1] = -1;
_<>_print(arr[-1]); // -1
_<>_print(arr.length); // 5
