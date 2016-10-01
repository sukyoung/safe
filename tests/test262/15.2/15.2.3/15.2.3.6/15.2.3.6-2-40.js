  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, new String("Hello"), {
      
    });
    return obj.hasOwnProperty("Hello");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  