  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 0.000001, {
      
    });
    return obj.hasOwnProperty("0.000001");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
