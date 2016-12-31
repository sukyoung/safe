  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      @Global.enumerable = true;
      Object.defineProperty(obj, "property", @Global);
      for(var prop in obj)
      {
        if (prop === "property")
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
  
