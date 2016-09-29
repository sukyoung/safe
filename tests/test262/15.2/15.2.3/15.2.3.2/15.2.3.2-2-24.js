function testcase() 
{
  var obj = new Number(- 3);
  return Object.getPrototypeOf(obj) === Number.prototype;
}

var __result1 = testcase();
var __expect1 = true;
