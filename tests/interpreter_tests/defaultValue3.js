try {
    a = { toString: function() { return {} },  substring: String.prototype.substring }
    _<>_print(a.substring(0))
} catch(e) {
    _<>_print("ERROR CAUGHT!")
}
"PASS"
