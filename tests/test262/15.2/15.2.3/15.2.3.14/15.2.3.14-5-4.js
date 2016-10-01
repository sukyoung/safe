  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop1", {
      get : (function () 
      {
        
      }),
      enumerable : true,
      configurable : true
    });
    Object.defineProperty(obj, "prop2", {
      get : (function () 
      {
        
      }),
      enumerable : false,
      configurable : true
    });
    Object.defineProperty(obj, "prop3", {
      get : (function () 
      {
        
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr.hasOwnProperty(p))
      {
        if (arr[p] === "prop2")
        {
          return false;
        }
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  