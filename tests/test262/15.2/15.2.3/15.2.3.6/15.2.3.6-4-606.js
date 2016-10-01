  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "preventExtensions");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.preventExtensions;
    try
{      Object.preventExtensions = "2010";
      var isWritable = (Object.preventExtensions === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "preventExtensions")
        {
          isEnumerable = true;
        }
      }
      delete Object.preventExtensions;
      var isConfigurable = ! Object.hasOwnProperty("preventExtensions");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "preventExtensions", {
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
  