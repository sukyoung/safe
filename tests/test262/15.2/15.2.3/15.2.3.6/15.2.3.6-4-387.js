  function testcase() 
  {
    var obj = {
      
    };
    var strObj = new String();
    Object.defineProperty(obj, "prop", {
      value : strObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === strObj && desc.value === strObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  