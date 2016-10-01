  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var argObj = (function () 
    {
      return arguments;
    })(0, 1, 2);
    Object.defineProperty(obj, "property", {
      enumerable : argObj
    });
    for(var prop in obj)
    {
      if (prop === "property")
      {
        accessed = true;
      }
    }
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  