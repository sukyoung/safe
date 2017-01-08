  function testcase() 
  {
    var objDate = new Date();
    function callbackfn(val, idx, obj) 
    {
      return this === objDate;
    }
    var testResult = [11, ].map(callbackfn, objDate);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  