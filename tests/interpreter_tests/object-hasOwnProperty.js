p = { a: 42 }
f = function() { this.b = 42; }
f.prototype = p
o = new f()
_<>_print("a: "+o.a)
_<>_print(o.hasOwnProperty("a"))
_<>_print("b: "+o.b)
_<>_print(o.hasOwnProperty("b"))
"PASS"
