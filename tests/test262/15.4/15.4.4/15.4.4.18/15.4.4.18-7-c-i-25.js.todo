  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        testResult = (val === 11);
      }
    }
    var func = (function (a, b) 
    {
      return Array.prototype.forEach.call(arguments, callbackfn);
    });
    func(11);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  