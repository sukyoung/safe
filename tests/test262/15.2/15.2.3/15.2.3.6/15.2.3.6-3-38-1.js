  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Object.prototype.enumerable = true;
      Object.defineProperty(obj, "property", Math);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Object.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  