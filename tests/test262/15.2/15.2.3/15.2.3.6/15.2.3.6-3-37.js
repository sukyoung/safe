  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var numObj = new Number(- 2);
    numObj.enumerable = true;
    Object.defineProperty(obj, "property", numObj);
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
  