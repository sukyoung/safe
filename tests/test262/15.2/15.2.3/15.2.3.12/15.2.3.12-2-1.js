  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "Father", {
      value : 10,
      writable : false,
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    Object.preventExtensions(child);
    return Object.isFrozen(child);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  