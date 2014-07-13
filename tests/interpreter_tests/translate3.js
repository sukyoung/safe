var temp = 10;
function f() {temp++; return temp;}

try {
  y && f() ? "y" : "z"
} catch (e) {}

_<>_print(temp)

try {
  y || f() ? "y" : "z"
} catch (e) {}

_<>_print(temp)

try {
  if (y && f()) "y"; else "z";
} catch (e) {}

_<>_print(temp)

try {
  if (y || f()) "y"; else "z";
} catch (e) {}

_<>_print(temp)

"PASS";
