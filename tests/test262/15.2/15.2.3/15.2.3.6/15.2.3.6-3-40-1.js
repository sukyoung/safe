  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      RegExp.prototype.enumerable = true;
      var regObj = new RegExp();
      Object.defineProperty(obj, "property", regObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete RegExp.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  