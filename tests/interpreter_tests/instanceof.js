// "15 Standard Built-in ECMAScript Objects" is not implemented.
//   ex) Object, Array, Date, ...

var obj = {a:1, b:2, c:3};
var arr = [1, 2, 3];
var date = new Date();

_<>_print(obj instanceof Object);
_<>_print(obj instanceof Array);

_<>_print(arr instanceof Object);
_<>_print(arr instanceof Array);

_<>_print(date instanceof Object);
_<>_print(date instanceof Date);

"PASS"
