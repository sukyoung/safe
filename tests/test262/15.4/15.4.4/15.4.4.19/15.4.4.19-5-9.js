  function testcase() 
  {
    var objFunction = (function () 
    {
      
    });
    function callbackfn(val, idx, obj) 
    {
      return this === objFunction;
    }
    var testResult = [11, ].map(callbackfn, objFunction);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  