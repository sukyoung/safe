  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return (val > 10 && obj[idx] === val);
    }
    var testResult = [11, ].map(callbackfn);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  