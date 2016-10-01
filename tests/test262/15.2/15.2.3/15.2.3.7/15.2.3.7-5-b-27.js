  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      JSON.enumerable = true;
      Object.defineProperties(obj, {
        prop : JSON
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
{      delete JSON.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  