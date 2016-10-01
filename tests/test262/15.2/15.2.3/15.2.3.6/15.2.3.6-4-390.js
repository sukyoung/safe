  function testcase() 
  {
    var obj = {
      
    };
    var funObj = (function () 
    {
      
    });
    Object.defineProperty(obj, "prop", {
      value : funObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === funObj && desc.value === funObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  