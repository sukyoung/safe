  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "getPrototypeOf");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.getPrototypeOf;
    try
{      Object.getPrototypeOf = "2010";
      var isWritable = (Object.getPrototypeOf === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "getPrototypeOf")
        {
          isEnumerable = true;
        }
      }
      delete Object.getPrototypeOf;
      var isConfigurable = ! Object.hasOwnProperty("getPrototypeOf");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "getPrototypeOf", {
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
  