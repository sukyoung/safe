var o;

if (@Top)
	o = Object;
else
	o = Array;

var __result1 = ({}) instanceof o
var __expect1 = @BoolTop

var __result2 = ([]) instanceof o
var __expect2 = true
