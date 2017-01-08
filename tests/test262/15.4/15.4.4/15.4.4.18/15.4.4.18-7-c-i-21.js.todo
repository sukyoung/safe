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
    var proto = {
      
    };
    Object.defineProperty(proto, "1", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.length = 2;
    Array.prototype.forEach.call(child, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  