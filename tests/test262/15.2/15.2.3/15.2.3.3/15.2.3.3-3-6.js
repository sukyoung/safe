  function testcase() 
  {
    var proto = {
      
    };
    var fun = (function () 
    {
      return "ownAccessorProperty";
    });
    Object.defineProperty(proto, "property", {
      get : fun,
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    var desc = Object.getOwnPropertyDescriptor(child, "property");
    return typeof desc === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  