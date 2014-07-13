regex = new RegExp("a|bc")
_<>_print(regex.test("ab"))
_<>_print(regex.test("abc"))
_<>_print(regex.test("bc"))
_<>_print(regex.test("b"))
"PASS"
