var p = {gh: 32, gb: 10}

//dumpObject(p);
var __result1 = p.gb;  // for SAFE
var __expect1 = 10;  // for SAFE

delete p.gb;
//dumpObject(p);
var __result2 = p.gb;  // for SAFE
var __expect2 = undefined;  // for SAFE

//dumpObject(this);

//dumpAttributes(this, "Function");
var attr1 = Object.getOwnPropertyDescriptor(this, "Function");  // for SAFE
var __result3 = attr1.writable;  // for SAFE
var __expect3 = true;  // for SAFE

var __result4 = attr1.enumerable;  // for SAFE
var __expect4 = false;  // for SAFE

var __result5 = attr1.configurable;  // for SAFE
var __expect5 = true;  // for SAFE

//dumpValue(delete this.Function);
var __result6 = delete this.Function;  // for SAFE
var __expect6 = true;  // for SAFE

//dumpObject(this)
var __result7 = this.Function;  // for SAFE
var __expect7 = undefined;  // for SAFE

//dumpAttributes(Number, "length");
var attr2 = Object.getOwnPropertyDescriptor(Number, "length");  // for SAFE
var __result8 = attr2.writable;  // for SAFE
var __expect8 = false;  // for SAFE

var __result9 = attr2.enumerable;  // for SAFE
var __expect9 = false;  // for SAFE

var __result10 = attr2.configurable;  // for SAFE
var __expect10 = false;  // for SAFE

//dumpValue(delete Number.length);
var __result11 = delete Number.length;  // for SAFE
var __expect11 = false;  // for SAFE

//dumpObject(Number)
var __result12 = Number.length;  // for SAFE
var __expect12 = 1;  // for SAFE

var arr = [0,1,1,2,3,5,8,13,"not a fib number",21,34,55,89];
var t = 5;
//dumpObject(arr);
var __result13 = arr[t+3];  // for SAFE
var __expect13 = "not a fib number";  // for SAFE

delete arr[t+3];
//dumpObject(arr);
var __result14 = arr[t+3];  // for SAFE
var __expect14 = undefined;  // for SAFE

function id(x) {return x}
var s = id("a") + id("b");
//dumpValue(s);
var __result15 = s;  // for SAFE
var __expect15 = "ab";  // for SAFE

//delete arr[s]
var __result16 = delete arr[s];  // for SAFE
var __expect16 = true;  // for SAFE

//dumpObject(arr);
var __result17 = arr.length;  // for SAFE
var __expect17 = 13;  // for SAFE
