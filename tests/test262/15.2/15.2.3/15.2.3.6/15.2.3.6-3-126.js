  function testcase() 
  {
    var obj = {
      
    };
    var attr = {
      value : 100
    };
    Object.defineProperty(obj, "property", attr);
    return obj.property === 100;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  