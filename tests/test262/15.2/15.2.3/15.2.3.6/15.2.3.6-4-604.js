  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "seal");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.seal;
    try
{      Object.seal = "2010";
      var isWritable = (Object.seal === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "seal")
        {
          isEnumerable = true;
        }
      }
      delete Object.seal;
      var isConfigurable = ! Object.hasOwnProperty("seal");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "seal", {
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
  