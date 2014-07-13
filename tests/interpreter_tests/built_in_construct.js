_<>_print(Object());
_<>_print(Object(123));
_<>_print(Object("abc"));
_<>_print(Object(true));
_<>_print(new Object());
_<>_print(new Object(123));
_<>_print(new Object("abc"));
_<>_print(new Object(true));
_<>_print(Object.prototype);

_<>_print(Function());
_<>_print(new Function());
_<>_print(Function.prototype);
var F1 = Function();
_<>_print(F1.prototype);
_<>_print(F1());
var F2 = function(a, b, c) {}
_<>_print(F2);
_<>_print(F2.length);
_<>_print(F2.call);
var F3 = new Function();
_<>_print(F3);
_<>_print(F3.length);
_<>_print(F3.call);

"PASS";
