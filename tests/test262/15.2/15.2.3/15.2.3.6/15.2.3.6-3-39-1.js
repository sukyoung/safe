  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Date.prototype.enumerable = true;
      var dateObj = new Date();
      Object.defineProperty(obj, "property", dateObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Date.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  