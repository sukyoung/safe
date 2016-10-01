  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : undefined
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.hasOwnProperty("prop") && typeof obj.prop === "undefined" && typeof desc.value === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  