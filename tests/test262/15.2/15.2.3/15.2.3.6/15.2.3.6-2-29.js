  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 100000000000000000000.1, {
      
    });
    return obj.hasOwnProperty("100000000000000000000");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
