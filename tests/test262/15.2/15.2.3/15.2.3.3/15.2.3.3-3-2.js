  function testcase() 
  {
    var proto = {
      property : "inheritedDataProperty"
    };
    var Con = (function () 
    {
      
    });
    Con.ptototype = proto;
    var child = new Con();
    var desc = Object.getOwnPropertyDescriptor(child, "property");
    return typeof desc === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  