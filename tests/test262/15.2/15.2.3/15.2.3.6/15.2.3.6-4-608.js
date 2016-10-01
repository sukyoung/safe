  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "isFrozen");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.isFrozen;
    try
{      Object.isFrozen = "2010";
      var isWritable = (Object.isFrozen === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "isFrozen")
        {
          isEnumerable = true;
        }
      }
      delete Object.isFrozen;
      var isConfigurable = ! Object.hasOwnProperty("isFrozen");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "isFrozen", {
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
  