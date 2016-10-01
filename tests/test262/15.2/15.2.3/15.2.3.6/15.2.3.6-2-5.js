  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, NaN, {
      
    });
    return obj.hasOwnProperty("NaN");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  