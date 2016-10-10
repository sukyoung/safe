  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 1000000000000000000000.1, {
      
    });
    return obj.hasOwnProperty("1e+21");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
