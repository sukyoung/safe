  function testcase() 
  {
    var obj = {
      
    };
    try
{      @Global.configurable = true;
      Object.defineProperty(obj, "property", @Global);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete @Global.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
