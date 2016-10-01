  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : NaN
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop !== obj.prop && desc.value !== desc.value;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  