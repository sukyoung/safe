  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "defineProperty");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.defineProperty;
    try
{      Object.defineProperty = "2010";
      var isWritable = (Object.defineProperty === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "defineProperty")
        {
          isEnumerable = true;
        }
      }
      delete Object.defineProperty;
      var isConfigurable = ! Object.hasOwnProperty("defineProperty");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty = temp;
      Object.defineProperty(Object, "defineProperty", {
        enumerable : false
      });}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  