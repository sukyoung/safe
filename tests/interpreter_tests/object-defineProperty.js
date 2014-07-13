Object.defineProperty(Function.prototype, "toString", { value: function() { return "HI"; }, configurable: false, enumerable: false, writable: true } );
var s = "Array has ";
for ( x in Array ) s += x;
_<>_print(s);
_<>_print("To you, "+Array);
Object.defineProperty(Function.prototype, "toString", { value: function() { return "HELLO"; }, configurable: false, enumerable: false, writable: true } );
_<>_print("To you, "+Array);
delete Function.prototype.toString;
_<>_print("To you, "+Array);
"PASS"
