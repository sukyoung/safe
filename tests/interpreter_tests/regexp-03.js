a = new RegExp(".")
_<>_print(a.test("a"))
_<>_print(a.test("b"))
_<>_print(a.test("abc"))
b = new RegExp("a.c")
_<>_print(b.test("a"))
_<>_print(b.test("b"))
_<>_print(b.test("abc"))
"PASS"
