  function testcase() 
  {
    var obj = {
      
    };
    var ownProp = {
      toString : (function () 
      {
        return "abc";
      })
    };
    Object.defineProperty(obj, ownProp, {
      
    });
    return obj.hasOwnProperty("abc");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  