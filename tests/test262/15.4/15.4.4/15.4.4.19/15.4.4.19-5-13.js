  function testcase() 
  {
    var objNumber = new Number();
    function callbackfn(val, idx, obj) 
    {
      return this === objNumber;
    }
    var testResult = [11, ].map(callbackfn, objNumber);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  