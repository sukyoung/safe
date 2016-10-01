  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      prop : {
        value : 1001
      }
    });
    for(var prop in obj)
    {
      if (obj.hasOwnProperty(prop))
      {
        if (prop === "prop")
        {
          return false;
        }
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  