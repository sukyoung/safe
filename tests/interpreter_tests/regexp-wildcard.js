_<>_print("abcdedf".replace(/b.*d/, ""))
_<>_print("abc\ne\nf".replace(/b.*\n/, ""))
_<>_print("abc\ne\nf".replace(/b.*\n/m, ""))
_<>_print("abc\te\tf".replace(/b.*\t/m, ""))
"PASS"
