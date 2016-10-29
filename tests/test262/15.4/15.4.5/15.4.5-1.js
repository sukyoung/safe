function testcase() {
  var a = [];
  var s = Object.prototype.toString.call(a);
  if (s === '[object Array]') {
    return true;
  }
 }
var __result1 = testcase()
var __expect1 = true
