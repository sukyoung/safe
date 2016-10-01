  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 1001
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === 1001 && desc.value === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  