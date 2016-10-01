  function testcase() 
  {
    var obj = {
      
    };
    try
{      Array.prototype.configurable = true;
      var arrObj = [1, 2, 3, ];
      Object.defineProperty(obj, "property", arrObj);
      var beforeDeleted = obj.hasOwnProperty("property");
      delete obj.property;
      var afterDeleted = obj.hasOwnProperty("property");
      return beforeDeleted === true && afterDeleted === false;}
    finally
{      delete Array.prototype.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  