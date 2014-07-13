function f() { this.a = 1 } 
function g() { this.b = 2 } 
var x = function h() { this.c = 3 } 
var a = new x;
g.prototype = a; 
g.prototype.strG = "G fun";
var b = new g;
f.prototype.strY = "Y fun";
f["prototype"] = b; 
var y = new f;
var d = 4;

with(y) {
_<>_print(a)	// 1
_<>_print(b)	// 2
_<>_print(c)	// 3
delete a;
_<>_print(a)	// [object Object]
_<>_print(a.c)	// 3
delete b;
_<>_print(b.b)	// undefined
}
_<>_print(b.b)	// 2

