  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, [1, 2, ], {
      
    });
    return obj.hasOwnProperty("1,2");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  