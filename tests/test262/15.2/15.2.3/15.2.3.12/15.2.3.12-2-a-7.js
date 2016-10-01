  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    Object.preventExtensions(obj);
    return ! Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  