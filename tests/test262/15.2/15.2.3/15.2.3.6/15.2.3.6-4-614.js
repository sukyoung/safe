  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "every");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.every;
    try
{      Array.prototype.every = "2010";
      var isWritable = (Array.prototype.every === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "every")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.every;
      var isConfigurable = ! Array.prototype.hasOwnProperty("every");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "every", {
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
  