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
    [11, ].forEach(callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  