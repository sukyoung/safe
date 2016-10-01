  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : false
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === false && desc.value === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  