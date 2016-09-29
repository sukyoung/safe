function testcase() 
{
  var obj = new RegExp();
  return Object.getPrototypeOf(obj) === RegExp.prototype;
}

var __result1 = testcase();
var __expect1 = true;
