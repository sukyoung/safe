  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, new Number(123), {
      
    });
    return obj.hasOwnProperty("123");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  