  function testcase() 
  {
    var obj = {
      prop1 : 1001,
      prop2 : 1002
    };
    Object.defineProperty(obj, "prop3", {
      value : 1003,
      enumerable : true,
      configurable : true
    });
    Object.defineProperty(obj, "prop4", {
      value : 1004,
      enumerable : false,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr.hasOwnProperty(p))
      {
        if (arr[p] === "prop4")
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
  