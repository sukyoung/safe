  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "AB\n\\cd", {
      
    });
    return obj.hasOwnProperty("AB\n\\cd");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  