  function testcase() 
  {
    var objBoolean = new Boolean();
    function callbackfn(val, idx, obj) 
    {
      return this === objBoolean;
    }
    var testResult = [11, ].map(callbackfn, objBoolean);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  