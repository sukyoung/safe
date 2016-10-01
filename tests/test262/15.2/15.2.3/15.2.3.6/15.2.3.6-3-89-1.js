  function testcase() 
  {
    var obj = {
      
    };
    try
{      Boolean.prototype.configurable = true;
      var boolObj = new Boolean(true);
      Object.defineProperty(obj, "property", boolObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Boolean.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  