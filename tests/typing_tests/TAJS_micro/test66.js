var a = "string"
var b = 1000
var ab = a+b 
//dumpValue(ab);
var __result1 = ab;  // for SAFE
var __expect1 = "string1000";  // for SAFE


function X() {
    this.Y = 0;
}
var object = new X()
var s = object.Y + 1;
//dumpValue(s) 
var __result2 = s;  // for SAFE
var __expect2 = 1;  // for SAFE
