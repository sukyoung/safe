  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop1", {
      value : 1001,
      enumerable : false,
      configurable : true
    });
    Object.defineProperty(obj, "prop2", {
      get : (function () 
      {
        return 1002;
      }),
      enumerable : false,
      configurable : true
    });
    var arr = Object.keys(obj);
    return arr.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  