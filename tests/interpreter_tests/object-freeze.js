a = { x: 0 }
_<>_print(a.x)
_<>_print(a.y)
a.x = 1
a.y = 2
_<>_print(a.x)
_<>_print(a.y)
delete a.y
_<>_print(a.x)
_<>_print(a.y)
Object.freeze(a)
a.x = 3
a.y = 4
_<>_print(a.x)
_<>_print(a.y)
delete a.x
_<>_print(a.x)
_<>_print(a.y)
"PASS"
