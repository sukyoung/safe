// 15.4.5.1 
var arr = [0,1,2,3,4];
arr.length = 10;
_<>_print(arr.length);
arr.length = true;
_<>_print(arr.length);
for(var x = 0; x < arr.length; x++) {
    _<>_print(arr[x]);
}
arr.length = false;
arr[-1] = 10;
_<>_print(arr.length);
for(var x = 0; x < arr.length; x++) {
    _<>_print(arr[x]);
}
for(var x in arr) {
    _<>_print(x + arr[x]);
}
