  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      __Global.enumerable = true;
      Object.defineProperty(obj, "property", __Global);
      for(var prop in obj)
      {
        if (prop === "property")
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
  
