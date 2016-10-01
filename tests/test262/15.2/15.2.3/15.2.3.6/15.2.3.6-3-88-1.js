  function testcase() 
  {
    var obj = {
      
    };
    try
{      String.prototype.configurable = true;
      var strObj = new String("abc");
      Object.defineProperty(obj, "property", strObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete String.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  