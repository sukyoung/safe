  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, new Boolean(false), {
      
    });
    return obj.hasOwnProperty("false");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  