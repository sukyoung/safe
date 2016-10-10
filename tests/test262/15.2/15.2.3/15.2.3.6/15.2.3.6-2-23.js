  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 1E-7, {
      
    });
    return obj.hasOwnProperty("1e-7");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
