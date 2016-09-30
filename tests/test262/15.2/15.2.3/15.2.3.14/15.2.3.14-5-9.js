  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "prop", {
      get : (function () 
      {
        
      }),
      enumerable : true,
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var obj = new Con();
    Object.defineProperty(obj, "prop", {
      value : 1003,
      enumerable : false,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr[p] === "prop")
      {
        return false;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  