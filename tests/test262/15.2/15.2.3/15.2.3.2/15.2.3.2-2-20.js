function testcase() 
{
  var obj = (function (a, b) 
      {
        return a + b;
      });
  return Object.getPrototypeOf(obj) === Function.prototype;
}

var __result1 = testcase();
var __expect1 = true;
