  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(String.prototype, "trim");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = String.prototype.trim;
    try
{      String.prototype.trim = "2010";
      var isWritable = (String.prototype.trim === "2010");
      var isEnumerable = false;
      for(var prop in String.prototype)
      {
        if (prop === "trim")
        {
          isEnumerable = true;
        }
      }
      delete String.prototype.trim;
      var isConfigurable = ! String.prototype.hasOwnProperty("trim");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(String.prototype, "trim", {
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
  