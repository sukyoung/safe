  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : Infinity
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === Infinity && desc.value === Infinity;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  