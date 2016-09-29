function testcase() 
{
  function fun() 
  {
    return arguments;
  }
  var obj = fun(1, true, 3);
  return Object.getPrototypeOf(obj) === Object.prototype;
}

var __result1 = testcase();
var __expect1 = true;
