  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "map");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.map;
    try
{      Array.prototype.map = "2010";
      var isWritable = (Array.prototype.map === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "map")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.map;
      var isConfigurable = ! Array.prototype.hasOwnProperty("map");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "map", {
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
  