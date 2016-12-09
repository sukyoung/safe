  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : @Global
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === @Global && desc.value === @Global;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
