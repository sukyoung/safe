  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 0.0000001, {
      
    });
    return obj.hasOwnProperty("1e-7");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
