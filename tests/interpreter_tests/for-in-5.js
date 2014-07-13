p = { c: 42 }
f = function() { this.a = 1; this.b = 2; }
f.prototype = p
o = new f()
for (i in o) { _<>_print(i+": "+o[i]) }
"PASS"
