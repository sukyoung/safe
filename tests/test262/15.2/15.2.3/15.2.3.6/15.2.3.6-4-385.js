  function testcase() 
  {
    var obj = {
      
    };
    var tempObj = {
      testproperty : 100
    };
    Object.defineProperty(obj, "prop", {
      value : tempObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === tempObj && desc.value === tempObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  