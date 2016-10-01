  function testcase() 
  {
    var obj = {
      
    };
    var errObj = new Error();
    Object.defineProperty(obj, "prop", {
      value : errObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === errObj && desc.value === errObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  