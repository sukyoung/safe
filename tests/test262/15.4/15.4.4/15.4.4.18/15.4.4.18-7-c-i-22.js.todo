  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        testResult = (typeof val === "undefined");
      }
    }
    try
{      Object.defineProperty(Array.prototype, "0", {
        set : (function () 
        {
          
        }),
        configurable : true
      });
      [, 1, ].forEach(callbackfn);
      return testResult;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  