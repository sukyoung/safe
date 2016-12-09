var o = [];
o[0] = o;

var __result1;
try { JSON.stringify(o) } catch(e) { __result1 = e; }
var __expect1 = @TypeErr;

o2 = {a:10, b:20, c:30, d:40};
var o1 = {e:10, toString:20, abc:o2, def:null}
var o = [10, 20, o1, o2]

var __result2;
try { JSON.stringify(o);__result2 = 0; } catch(e) { }
var __expect2 = 0;

