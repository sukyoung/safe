function f() { return this; }
var e = "global";
try { throw ReferenceError; }
catch (e) {
  e = "inner";
  _<>_print(f().e);
  _<>_print(this.e);
}
_<>_print(e);
"PASS";
