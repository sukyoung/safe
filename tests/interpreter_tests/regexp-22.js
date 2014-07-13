var o1 = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/
var o2 = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$");
var o3 = new RegExp(o1.source);
_<>_print(o1.source);
_<>_print(o2.source);
_<>_print(o3.source);
"PASS"
