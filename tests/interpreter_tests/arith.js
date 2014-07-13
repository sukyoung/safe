if (1 + 2 != 3) throw "1 + 2"
if (1 + 2.0 != 3.0) throw "1 + 2.0"
if (1.0 + 2 != 3.0) throw "1.0 + 2"
if (1.0 + 2.0 != 3.0) throw "1.0 + 2.0"

if (1 - 2 != -1) throw "1 - 2"
if (1 - 2.0 != -1.0) throw "1 - 2.0"
if (1.0 - 2 != -1.0) throw "1.0 - 2"
if (1.0 - 2.0 != -1.0) throw "1.0 - 2.0"

if (2 * 3 != 6) throw "2 * 3"
if (2 * 3.0 != 6.0) throw "2 * 3.0"
if (2.0 * 3 != 6.0) throw "2.0 * 3"
if (2.0 * 3.0 != 6.0) throw "2.0 * 3.0"

if (3 / 2 != 1.5) throw "3 / 2"
if (3 / 2.0 != 1.5) throw "3 / 2.0"
if (3.0 / 2 != 1.5) throw "3.0 / 2"
if (3.0 / 2.0 != 1.5) throw "3.0 / 2.0"

if (3 % 2 != 1) throw "3 % 2"
if (3 % 2.0 != 1) throw "3 % 2.0"
if (3.0 % 2 != 1) throw "3.0 % 2"
if (3.0 % 2.0 != 1) throw "3.0 % 2.0"

var x = 5.123;
var y = x / 0.0;
var z = 0.0 / 0.0;
_<>_print(+x);
_<>_print(-x);
_<>_print(+y);
_<>_print(-y);
_<>_print(+z);
_<>_print(-z);

"PASS"
