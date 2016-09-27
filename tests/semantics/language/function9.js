var f = function() {
}

var o = new f();
var v = o.constructor;
o.constructor = 20;
delete o.constructor;

var __result1 = o.constructor;
var __expect1 = v;

