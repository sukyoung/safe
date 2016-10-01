  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 123.456, {
      
    });
    return obj.hasOwnProperty("123.456");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  