var x;

x = parseInt("543.2");
_<>_print(x);
x = parseFloat("543.2");
_<>_print(x);
x = isNaN(1 / 2);
_<>_print(x);
x = isNaN(0 / 0);
_<>_print(x);
x = isFinite(2 / 3);
_<>_print(x);
x = isFinite(1 / 0);
_<>_print(x);

"PASS"
