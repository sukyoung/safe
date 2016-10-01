  function testcase() 
  {
    var obj = {
      
    };
    var boolObj = new Boolean();
    Object.defineProperty(obj, "prop", {
      value : boolObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === boolObj && desc.value === boolObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  