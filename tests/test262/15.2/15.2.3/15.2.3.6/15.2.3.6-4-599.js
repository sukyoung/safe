  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "getOwnPropertyDescriptor");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.getOwnPropertyDescriptor;
    try
{      Object.getOwnPropertyDescriptor = "2010";
      var isWritable = (Object.getOwnPropertyDescriptor === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "getOwnPropertyDescriptor")
        {
          isEnumerable = true;
        }
      }
      delete Object.getOwnPropertyDescriptor;
      var isConfigurable = ! Object.hasOwnProperty("getOwnPropertyDescriptor");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "getOwnPropertyDescriptor", {
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
  