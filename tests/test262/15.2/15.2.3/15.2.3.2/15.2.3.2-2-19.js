function testcase() 
{
  var obj = {

  };
  return Object.getPrototypeOf(obj) === Object.prototype;
}

var __result1 = testcase();
var __expect1 = true;
