try {
    y = {z:3}
	throw "asdf"
} catch(E) {
	if(@NumTop > 5) {
		y.x = 1;
	}
	else {
		y.y = 2;
	}
}
var __result1 = y.x
var __expect1 = 1
var __result2 = y.y
var __expect2 = 2
