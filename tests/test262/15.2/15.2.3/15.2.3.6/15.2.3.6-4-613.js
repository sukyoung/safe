  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "lastIndexOf");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.lastIndexOf;
    try
{      Array.prototype.lastIndexOf = "2010";
      var isWritable = (Array.prototype.lastIndexOf === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "lastIndexOf")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.lastIndexOf;
      var isConfigurable = ! Array.prototype.hasOwnProperty("lastIndexOf");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "lastIndexOf", {
        value : temp,
        writable : true,
        enumerable : false,
        configurable : true
      });}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  