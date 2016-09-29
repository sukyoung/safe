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
    child.property = "ownDataProperty";
    var desc = Object.getOwnPropertyDescriptor(child, "property");
    return desc.value === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  