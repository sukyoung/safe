  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 1)
      {
        testResult = (typeof val === "undefined");
      }
    }
    var obj = {
      length : 2
    };
    Object.defineProperty(obj, "1", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    Array.prototype.forEach.call(obj, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  