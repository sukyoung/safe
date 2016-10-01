  function testcase() 
  {
    var obj = {
      
    };
    var regObj = new RegExp();
    Object.defineProperty(obj, "prop", {
      value : regObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === regObj && desc.value === regObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  