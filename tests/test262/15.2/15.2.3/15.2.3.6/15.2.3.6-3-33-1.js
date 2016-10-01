  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    try
{      Function.prototype.enumerable = true;
      var fun = (function () 
      {
        
      });
      Object.defineProperty(obj, "property", fun);
      for(var prop in obj)
      {
        if (prop === "property")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete Function.prototype.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  