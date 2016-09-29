function testcase() 
{
  var obj = new Date();
  return Object.getPrototypeOf(obj) === Date.prototype;
}

var __result1 = testcase();
var __expect1 = true;
