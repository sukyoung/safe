  function testcase() 
  {
    var kValue = {
      
    };
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 5)
      {
        testResult = (val === kValue);
      }
    }
    var obj = {
      5 : kValue,
      length : 100
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  