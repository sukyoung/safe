  function testcase() 
  {
    var obj = {
      
    };
    try
{      Number.prototype.configurable = true;
      var numObj = new Number(- 2);
      Object.defineProperty(obj, "property", numObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Number.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  