  function testcase() 
  {
    var obj = {
      
    };
    try
{      RegExp.prototype.configurable = true;
      var regObj = new RegExp();
      Object.defineProperty(obj, "property", regObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete RegExp.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  