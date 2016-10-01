  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.prototype.configurable = true;
      var argObj = (function () 
      {
        return arguments;
      })();
      Object.defineProperty(obj, "property", argObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Object.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  