f = function() { 42 }
a = { "k": 1 }
f.prototype = a
x = new f()
_<>_print(x.k)
Object.prototype.k = 2
f.prototype = 1
y = new f()
_<>_print(y.k)
"PASS"
