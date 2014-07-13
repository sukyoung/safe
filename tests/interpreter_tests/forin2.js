var obj = {a:1, b:2, c:3}
for(x in obj) {
if("b" in obj) delete obj.b;
obj.b = 2;
_<>_print(x);
}

"PASS";
