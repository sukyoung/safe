  function testcase() 
  {
    var obj = {
      
    };
    try
{      Error.prototype.configurable = true;
      var errObj = new Error();
      Object.defineProperty(obj, "property", errObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Error.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  