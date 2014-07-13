var i;
for(i = 0;i < 5;i++) if(i == 3) break;
_<>_print(i);

i = 0;
while(i < 5) if(++i == 3) break;
_<>_print(i);

var arr = {a:5, b:4, c:3, d:2, e:1};
for(var x in arr) break;

"PASS"
