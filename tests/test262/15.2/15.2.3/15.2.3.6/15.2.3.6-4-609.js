  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "isExtensible");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.isExtensible;
    try
{      Object.isExtensible = "2010";
      var isWritable = (Object.isExtensible === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "isExtensible")
        {
          isEnumerable = true;
        }
      }
      delete Object.isExtensible;
      var isConfigurable = ! Object.hasOwnProperty("isExtensible");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "isExtensible", {
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
  