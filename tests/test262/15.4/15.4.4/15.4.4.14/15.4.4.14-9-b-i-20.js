  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "0", {
      get : (function () 
      {
        return 2;
      }),
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.length = 1;
    Object.defineProperty(child, "0", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    return Array.prototype.indexOf.call(child, undefined) === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  