  function testcase() 
  {
    var objRegExp = new RegExp();
    function callbackfn(val, idx, obj) 
    {
      return this === objRegExp;
    }
    var testResult = [11, ].map(callbackfn, objRegExp);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  