a = { toString: function() { return "ABC"; }, substring: String.prototype.substring }
_<>_print(a.substring(0))
"PASS"
