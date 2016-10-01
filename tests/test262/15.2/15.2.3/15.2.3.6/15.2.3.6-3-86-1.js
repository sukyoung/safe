  function testcase() 
  {
    var obj = {
      
    };
    try
{      Function.prototype.configurable = true;
      var funObj = (function (a, b) 
      {
        return a + b;
      });
      Object.defineProperty(obj, "property", funObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Function.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  