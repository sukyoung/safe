add = function(a, b) { return this.x+a+b }
this.x = 0;
_<>_print(add(1, 2))
obj = { x: 42 }
_<>_print(add.call(obj, 1, 2))
"PASS"
