  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.enumerable = true;
    Object.defineProperty(obj, "property", argObj);
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
  