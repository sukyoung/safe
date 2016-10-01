  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "filter");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.filter;
    try
{      Array.prototype.filter = "2010";
      var isWritable = (Array.prototype.filter === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "filter")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.filter;
      var isConfigurable = ! Array.prototype.hasOwnProperty("filter");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "filter", {
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
  