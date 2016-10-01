  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Boolean.prototype.enumerable = true;
      var boolObj = new Boolean(true);
      Object.defineProperty(obj, "property", boolObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Boolean.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  