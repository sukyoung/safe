  function testcase() 
  {
    var obj = {
      1 : true
    };
    Object.defineProperty(obj, "length", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    return Array.prototype.indexOf.call(obj, true) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  