  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "inheritedProp", {
      get : (function () 
      {
        return 1003;
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
      get : (function () 
      {
        return 1004;
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr[p] === "inheritedProp")
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
  