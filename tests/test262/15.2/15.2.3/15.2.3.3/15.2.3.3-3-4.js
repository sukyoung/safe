  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "property", {
      get : (function () 
      {
        return "inheritedDataProperty";
      }),
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.ptototype = proto;
    var child = new Con();
    Object.defineProperty(child, "property", {
      value : "ownDataProperty",
      configurable : true
    });
    var desc = Object.getOwnPropertyDescriptor(child, "property");
    return desc.value === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  