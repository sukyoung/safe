  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "keys");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.keys;
    try
{      Object.keys = "2010";
      var isWritable = (Object.keys === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "keys")
        {
          isEnumerable = true;
        }
      }
      delete Object.keys;
      var isConfigurable = ! Object.hasOwnProperty("keys");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "keys", {
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
  