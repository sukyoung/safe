function testcase() 
{
  var obj = new Error();
  return Object.getPrototypeOf(obj) === Error.prototype;
}

var __result1 = testcase();
var __expect1 = true;
