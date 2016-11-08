  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (idx === 1)
      {
        return typeof val === "undefined";
      }
      return false;
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
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[1] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  