  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Number.prototype.enumerable = true;
      var numObj = new Number(- 2);
      Object.defineProperty(obj, "property", numObj);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Number.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  