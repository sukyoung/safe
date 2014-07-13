s = new String("ab")
_<>_print(s.match.call("ab", "((a)(b))"))
_<>_print(s.match.call("ab", new RegExp("((a)(b))")))
"PASS"
