  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "length", {
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
    child[1] = true;
    return Array.prototype.indexOf.call(child, true) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  