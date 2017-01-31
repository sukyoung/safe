  function testcase() 
  {
    var arg;
    function callbackfn(val, idx, obj) 
    {
      return this === arg;
    }
    arg = (function () 
    {
      return arguments;
    })(1, 2, 3);
    var testResult = [11, ].map(callbackfn, arg);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  