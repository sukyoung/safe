o = { a: 1, b: 2 }
delete o.a
o.a = 3 
for (i in o) { _<>_print(i+": "+o[i]) }
"PASS"
