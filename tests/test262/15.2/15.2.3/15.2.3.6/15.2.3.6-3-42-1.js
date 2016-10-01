  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Error.prototype.enumerable = true;
      var errObj = new Error();
      Object.defineProperty(obj, "property", errObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Error.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  