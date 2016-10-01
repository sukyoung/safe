  function testcase() 
  {
    var obj = {
      
    };
    var dateObj = new Date();
    Object.defineProperty(obj, "prop", {
      value : dateObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === dateObj && desc.value === dateObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  