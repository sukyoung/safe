  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "123αβπcd", {
      
    });
    return obj.hasOwnProperty("123αβπcd");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  