// prototype test
var o1 = {p1:1};
var x1 = Object.create(o1);

var __result1 = x1.p1;
var __expect1 = 1;

var o2 = {p1:1};
var props = {
  p2:{value:2, writable:true, enumerable:true, configurable:false},
  p3:{value:3, writable:true, enumerable:true, configurable:true}
}

var x2 = Object.create(o2, props);

var __result2 = x2.p1;
var __expect2 = 1;

var __result3 = x2.p2;
var __expect3 = 2;

var __result4 = x2.p3;
var __expect4 = 3;

var __result5 = delete x2.p2;
var __expect5 = false;

var __result6 = delete x2.p3;
var __expect6 = true;
