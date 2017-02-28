function f() {
	var x;
	return {
		set_x1: function (v) { x = v; },
		set_x2: function (v) { x = v; },
    	set_x3: function (v) { x = v; },
        get_x: function () { return x; }
    };
}

var o1 = f();

var o2 = f();

var o3 = f();
o1.set_x1(null);
o2.set_x2(10);
o3.set_x3("A");

var __result1 = o1.get_x();
var __expect1 = null;

var __result2 = o2.get_x();
var __expect2 = 10;

var __result3 = o3.get_x();
var __expect3 = "A";
