var x = {a:1,b:true}

//dumpObject(x);

//dumpAttributes(x, "a");
var attr1 = Object.getOwnPropertyDescriptor(x, "a");  // for SAFE
var __result1 = attr1.writable;  // for SAFE
var __expect1 = true;  // for SAFE

var __result2 = attr1.enumerable;  // for SAFE
var __expect2 = true;  // for SAFE

var __result3 = attr1.configurable;  // for SAFE
var __expect3 = true;  // for SAFE

//dumpAttributes(x, "nosuchattribute");
var attr2 = Object.getOwnPropertyDescriptor(x, "nosuchattribute");  // for SAFE
var __result4 = attr2;  // for SAFE
var __expect4 = undefined;  // for SAFE

//dumpAttributes(this, "NaN");
var attr3 = Object.getOwnPropertyDescriptor(this, "NaN");  // for SAFE
var __result5 = attr3.writable;  // for SAFE
var __expect5 = false;  // for SAFE

var __result6 = attr3.enumerable;  // for SAFE
var __expect6 = false;  // for SAFE

var __result7 = attr3.configurable;  // for SAFE
var __expect7 = false;  // for SAFE

//dumpAttributes(Object, "prototype");
var attr4 = Object.getOwnPropertyDescriptor(Object, "prototype");  // for SAFE
var __result8 = attr4.writable;  // for SAFE
var __expect8 = false;  // for SAFE

var __result9 = attr4.enumerable;  // for SAFE
var __expect9 = false;  // for SAFE

var __result10 = attr4.configurable;  // for SAFE
var __expect10 = false;  // for SAFE

//dumpAttributes(this, "Function");
var attr5 = Object.getOwnPropertyDescriptor(this, "Function");  // for SAFE
var __result11 = attr5.writable;  // for SAFE
var __expect11 = true;  // for SAFE

var __result12 = attr5.enumerable;  // for SAFE
var __expect12 = false;  // for SAFE

var __result13 = attr5.configurable;  // for SAFE
var __expect13 = true;  // for SAFE

function f() {} // this line shouldn't affect the last dumpAttributes
