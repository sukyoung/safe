  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Math.enumerable = true;
      Object.defineProperties(obj, {
        prop : Math
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
{      delete Math.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  