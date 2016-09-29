function testcase() 
{
  var arr = [1, 2, 3, ];
  return Object.getPrototypeOf(arr) === Array.prototype;
}

var __result1 = testcase();
var __expect1 = true;
