  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : __Global
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === __Global && desc.value === __Global;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
