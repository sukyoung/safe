var d = new Date();

var i, t;
t = new Array();
t["Date"] = Date;
t["Object"] = Object;
t["Array"] = Array;
for (i in t)
{
    _<>_print(d instanceof t[i]);
}

"PASS";
