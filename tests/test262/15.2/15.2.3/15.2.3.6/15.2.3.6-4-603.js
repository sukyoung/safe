  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "defineProperties");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.defineProperties;
    try
{      Object.defineProperties = "2010";
      var isWritable = (Object.defineProperties === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "defineProperties")
        {
          isEnumerable = true;
        }
      }
      delete Object.defineProperties;
      var isConfigurable = ! Object.hasOwnProperty("defineProperties");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "defineProperties", {
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
  