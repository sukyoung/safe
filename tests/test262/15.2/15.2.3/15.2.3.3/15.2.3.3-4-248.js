  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      writable : true,
      configurable : true
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    return "value" in desc;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  