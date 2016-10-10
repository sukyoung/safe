  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 10000000000000000000.1, {
      
    });
    return obj.hasOwnProperty("10000000000000000000");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
