  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "foo", {
      get : (function () 
      {
        return 9;
      }),
      configurable : false
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    Object.defineProperty(child, "foo", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    Object.preventExtensions(child);
    return ! Object.isFrozen(child);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  