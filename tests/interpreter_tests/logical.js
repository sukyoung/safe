var temp = 1;

_<>_print(true && true);
_<>_print(true && false);
_<>_print(false && true);
_<>_print(false && false);

_<>_print(true || true);
_<>_print(true || false);
_<>_print(false || true);
_<>_print(false || false);

(true || _<>_print('hi')) ? 42 : "";
(false && _<>_print('hi')) ? 42 : "";
if (false && _<>_print('hi')) 42;
if (true || _<>_print('hi')) 42;

"PASS"
