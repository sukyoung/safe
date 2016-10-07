  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 10000000000000000000000, {
      
    });
    return obj.hasOwnProperty("1e+22");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
