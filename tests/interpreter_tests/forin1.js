var obj = {a:1, b:2, c:3}
for(x in obj) {
if("c" in obj) delete obj.c;
_<>_print(x);
}

"PASS";
