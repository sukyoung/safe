  function testcase() 
  {
    var objError = new RangeError();
    function callbackfn(val, idx, obj) 
    {
      return this === objError;
    }
    var testResult = [11, ].map(callbackfn, objError);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  