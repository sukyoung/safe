  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, + Infinity, {
      
    });
    return obj.hasOwnProperty("Infinity");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  