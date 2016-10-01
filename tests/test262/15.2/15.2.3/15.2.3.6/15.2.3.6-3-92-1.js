  function testcase() 
  {
    var obj = {
      
    };
    try
{      Date.prototype.configurable = true;
      var dateObj = new Date();
      Object.defineProperty(obj, "property", dateObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Date.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  