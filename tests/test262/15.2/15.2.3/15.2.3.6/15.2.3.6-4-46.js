  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      enumerable : true
    });
    var isEnumerable = false;
    for(var item in obj)
    {
      if (obj.hasOwnProperty(item) && item === "property")
      {
        isEnumerable = true;
      }
    }
    return obj.hasOwnProperty("property") && isEnumerable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  