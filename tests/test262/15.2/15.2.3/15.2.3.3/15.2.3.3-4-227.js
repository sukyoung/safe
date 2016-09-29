  function testcase() 
  {
    var obj = {
      "property" : "ownDataProperty"
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    return desc.writable === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  