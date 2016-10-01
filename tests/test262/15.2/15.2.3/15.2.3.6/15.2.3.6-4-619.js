  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "reduce");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.reduce;
    try
{      Array.prototype.reduce = "2010";
      var isWritable = (Array.prototype.reduce === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "reduce")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.reduce;
      var isConfigurable = ! Array.prototype.hasOwnProperty("reduce");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "reduce", {
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
  