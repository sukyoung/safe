  function testcase() 
  {
    var obj = [2, 3, 4, 5, ];
    Object.defineProperty(obj, "prop", {
      get : (function () 
      {
        return 6;
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr.hasOwnProperty(p))
      {
        if (arr[p] === "prop")
        {
          return true;
        }
      }
    }
    return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  