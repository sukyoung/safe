  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var dateObj = new Date();
    dateObj.enumerable = true;
    Object.defineProperty(obj, "property", dateObj);
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
  