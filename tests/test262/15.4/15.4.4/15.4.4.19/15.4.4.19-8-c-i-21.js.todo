  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        return typeof val === "undefined";
      }
      return false;
    }
    var proto = {
      length : 2
    };
    Object.defineProperty(proto, "0", {
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
    var testResult = Array.prototype.map.call(child, callbackfn);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  