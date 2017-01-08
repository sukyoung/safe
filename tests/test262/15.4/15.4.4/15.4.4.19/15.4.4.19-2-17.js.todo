  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var func = (function (a, b) 
    {
      return Array.prototype.map.call(arguments, callbackfn);
    });
    var testResult = func(12, 11);
    return testResult.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  