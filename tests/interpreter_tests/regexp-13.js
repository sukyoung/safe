a = new RegExp("([^<>])")
_<>_print(a.test("<"))
_<>_print(a.exec("<"))
_<>_print(a.test(">"))
_<>_print(a.exec(">"))
_<>_print(a.test("a"))
_<>_print(a.exec("a"))
"PASS"
