  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Boolean(false);
    var accessed = false;
    descObj.enumerable = true;
    Object.defineProperties(obj, {
      prop : descObj
    });
    for(var property in obj)
    {
      if (property === "prop")
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
  