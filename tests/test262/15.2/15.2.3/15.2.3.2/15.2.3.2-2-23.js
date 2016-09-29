function testcase() 
{
  var obj = new Boolean(true);
  return Object.getPrototypeOf(obj) === Boolean.prototype;
}

var __result1 = testcase();
var __expect1 = true;
