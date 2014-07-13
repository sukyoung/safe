add = function(a, b) { return this.x+a+b }
this.x = 42;
_<>_print(add(1, 2))
obj = { x: 42 }
_<>_print(add.apply(obj, [1, 2]))
"PASS"
