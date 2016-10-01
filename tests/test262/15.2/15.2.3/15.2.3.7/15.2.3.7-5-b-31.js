  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      __Global.enumerable = true;
      Object.defineProperties(obj, {
        prop : __Global
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
{      delete __Global.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
