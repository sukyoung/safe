var x = 1;
var y = 2.0;
var z = 3.4
_<>_print(x); // 1
_<>_print(y); // 2
_<>_print(z); // 3.4

var a = [5, 6, 7];
_<>_print(a[0]); // 5
_<>_print(a[x]); // 6
_<>_print(a[y]); // 7
_<>_print(a[x + y]); // undefined

_<>_print(4.0 / 0.0); // Infinity
_<>_print(-4.0 / 0.0); // -Infinity
_<>_print(4.0 / -0.0); // -Infinity
_<>_print(-4.0 / -0.0); // Infinity
_<>_print(0.0 / -0.0); // NaN

_<>_print(5.6 % 1.5); // 1.0999999999999996
_<>_print(-5.6 % 1.5); // -1.0999999999999996
_<>_print(5.6 % -1.5); // 1.0999999999999996
_<>_print(-5.6 % -1.5); // -1.0999999999999996

var plusInf = 1.0 / 0.0;
var minusInf = -1.0 / 0.0;
var nan = 0.0 / 0.0;
_<>_print(plusInf / -4);
_<>_print(minusInf / -4);
_<>_print(nan / -4);
_<>_print(plusInf / 0);
_<>_print(minusInf / 0);
_<>_print(nan / 0);
_<>_print(plusInf * minusInf);
_<>_print(plusInf / nan);
_<>_print(nan % nan);

"PASS"
