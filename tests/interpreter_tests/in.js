var obj = {a:1, b:2, c:3};
var arr = [1, 2, 3];
function func(a, b, c) {}

_<>_print("a" in obj);
_<>_print("d" in obj);

_<>_print("length" in arr);

_<>_print("prototype" in func);
_<>_print("length" in func);

"PASS"
