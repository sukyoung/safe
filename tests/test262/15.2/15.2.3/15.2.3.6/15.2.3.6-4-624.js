  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Date.prototype, "toJSON");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Date.prototype.toJSON;
    try
{      Date.prototype.toJSON = "2010";
      var isWritable = (Date.prototype.toJSON === "2010");
      var isEnumerable = false;
      for(var prop in Date.prototype)
      {
        if (prop === "toJSON")
        {
          isEnumerable = true;
        }
      }
      delete Date.prototype.toJSON;
      var isConfigurable = ! Date.prototype.hasOwnProperty("toJSON");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Date.prototype, "toJSON", {
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
  