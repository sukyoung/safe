  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    var func = (function (a, b) 
    {
      arguments[2] = 9;
      Array.prototype.forEach.call(arguments, callbackfn);
      return result;
    });
    return func(12, 11);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  