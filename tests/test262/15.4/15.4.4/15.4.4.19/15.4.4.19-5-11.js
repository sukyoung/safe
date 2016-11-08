  function testcase() 
  {
    var objString = new String();
    function callbackfn(val, idx, obj) 
    {
      return this === objString;
    }
    var testResult = [11, ].map(callbackfn, objString);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  