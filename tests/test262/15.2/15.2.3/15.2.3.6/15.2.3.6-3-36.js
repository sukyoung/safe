  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var boolObj = new Boolean(true);
    boolObj.enumerable = true;
    Object.defineProperty(obj, "property", boolObj);
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
  