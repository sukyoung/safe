  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    Object.defineProperties(obj, {
      prop : {
        enumerable : @Global
      }
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
  
