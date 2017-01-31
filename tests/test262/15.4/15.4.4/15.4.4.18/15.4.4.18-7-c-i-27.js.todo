  function testcase() 
  {
    var called = 0;
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      called++;
      if (called !== 1 && ! testResult)
      {
        return;
      }
      if (idx === 0)
      {
        testResult = (val === 11);
      }
      else
        if (idx === 1)
        {
          testResult = (val === 12);
        }
        else
          if (idx === 2)
          {
            testResult = (val === 9);
          }
          else
          {
            testResult = false;
          }
    }
    var func = (function (a, b) 
    {
      return Array.prototype.forEach.call(arguments, callbackfn);
    });
    func(11, 12, 9);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  