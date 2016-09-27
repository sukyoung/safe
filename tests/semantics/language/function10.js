var f = function() {
}

var o = new f();
var v = o.constructor;
delete o.constructor;

var __result1 = o.constructor;
var __expect1 = v;

