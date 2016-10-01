  function testcase() 
  {
    var obj = {
      
    };
    var arrObj = [];
    Object.defineProperty(obj, "prop", {
      value : arrObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === arrObj && desc.value === arrObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  