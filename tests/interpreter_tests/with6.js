function f() { this.b = 1 }
var a = new f;
var b = 3;
_<>_print(a);
with(a) {
a = 2;
_<>_print(b);
delete b;
_<>_print(b);
_<>_print(a);
}
_<>_print(a);
