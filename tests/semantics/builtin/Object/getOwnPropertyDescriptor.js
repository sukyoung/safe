// test for object
var o = {a: "str", b: 11};
var desc1 = Object.getOwnPropertyDescriptor(o, "a");

var __result1 = desc1.value;
var __expect1 = "str"

var __result2 = desc1.writable;
var __expect2 = true

var __result3 = desc1.enumerable;
var __expect3 = true

var __result4 = desc1.configurable;
var __expect4 = true

// test for global object
var a = "str"
var desc2 = Object.getOwnPropertyDescriptor(this, "a");

var __result5 = desc2.value;
var __expect5 = "str"

var __result6 = desc2.writable;
var __expect6 = true

var __result7 = desc2.enumerable;
var __expect7 = true

var __result8 = desc2.configurable;
var __expect8 = false

// test for non-existed property
var o2 = {};
var __result9 = Object.getOwnPropertyDescriptor(o2, "foo");
var __expect9 = undefined;
