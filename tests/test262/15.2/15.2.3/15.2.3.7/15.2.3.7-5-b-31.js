  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      @Global.enumerable = true;
      Object.defineProperties(obj, {
        prop : @Global
      });
      for(var property in obj)
      {
        if (property === "prop")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete @Global.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
