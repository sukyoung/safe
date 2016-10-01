var x;
l_1 : {
	l_2 : {
		x = 1;
		break l_1;
	}
	x = 2;
	break l_1;
	x = 3;
}

var __result1 = x;
var __expect1 = 1;
