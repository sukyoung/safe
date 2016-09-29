function testcase() 
{
  var obj = new String("abc");
  return Object.getPrototypeOf(obj) === String.prototype;
}

var __result1 = testcase();
var __expect1 = true;
