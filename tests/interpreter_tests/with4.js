function f() { this.a = 1; this.b = 2 }
var b = new f;

with(b) {
_<>_print(a) // 1
_<>_print(b) // 2
_<>_print(b.a) // undefined
delete b;
_<>_print(b) // [object Object]
_<>_print(b.a) // 1
}
