  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "foo", {
      value : 12,
      configurable : false
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    Object.defineProperty(child, "foo", {
      get : (function () 
      {
        return 9;
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
  