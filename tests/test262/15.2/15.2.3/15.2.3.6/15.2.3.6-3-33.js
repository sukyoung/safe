  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var fun = (function () 
    {
      
    });
    fun.enumerable = true;
    Object.defineProperty(obj, "property", fun);
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
  