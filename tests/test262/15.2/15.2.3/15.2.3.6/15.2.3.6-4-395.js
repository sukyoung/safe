  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : null
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === null && desc.value === null;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  