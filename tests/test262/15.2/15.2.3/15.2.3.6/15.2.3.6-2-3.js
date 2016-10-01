  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, false, {
      
    });
    return obj.hasOwnProperty("false");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  