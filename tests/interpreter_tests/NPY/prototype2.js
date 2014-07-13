function f() { this.a = 1 }
function g() { this.b = 2 }
var x = function h() { this.c = 3 }
var gp = new x;
g.prototype = gp;
g.prototype.strG = "G fun";
var fp = new g;
f.prototype.strY = "Y fun";
f["prototype"] = fp;
var y = new f;

_<>_print(y.a);  // 1
_<>_print(fp.b); // 2
_<>_print(y.b);  // 2
_<>_print(gp.c); // 3
_<>_print(y.c);  // 3
_<>_print(y.d);  // undefined
_<>_print(y.strY);  // undefined
_<>_print(y.strG);  // G fun

