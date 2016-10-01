  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      String.prototype.enumerable = true;
      var strObj = new String();
      Object.defineProperty(obj, "property", strObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete String.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  