  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Array.prototype.enumerable = true;
      var arrObj = [];
      Object.defineProperty(obj, "property", arrObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Array.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  