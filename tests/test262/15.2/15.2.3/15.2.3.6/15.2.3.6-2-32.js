  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, 123.1234567, {
      
    });
    return obj.hasOwnProperty("123.1234567");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  