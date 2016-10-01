  function testcase() 
  {
    var obj = {
      
    };
    var ownProp = {
      valueOf : (function () 
      {
        return "abc";
      }),
      toString : undefined
    };
    Object.defineProperty(obj, ownProp, {
      
    });
    return obj.hasOwnProperty("abc");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  